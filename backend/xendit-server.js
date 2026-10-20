/**
 * Smart Water PDAM - Xendit Webhook & API Server
 * 
 * Server ini berfungsi untuk:
 * 1. Menerima Webhook / Callback dari Xendit saat pembayaran lunas
 * 2. Mengupdate Cloud Firestore secara otomatis (status = "LUNAS", paymentStatus = "paid")
 * 3. Menyediakan API untuk membuat Invoice Xendit jika ingin dibuat dari server
 */

const express = require('express');
const admin = require('firebase-admin');
require('dotenv').config();

const app = express();
app.use(express.json());

// Inisialisasi Firebase Admin jika serviceAccountKey tersedia
// Jika belum diset, server tetap berjalan dalam mode logger & fallback
let db = null;
try {
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
    db = admin.firestore();
    console.log('[Firebase] Terhubung dengan Cloud Firestore');
  } else {
    // Coba inisialisasi default aplikasi jika berjalan di GCP / Firebase Cloud Functions
    admin.initializeApp();
    db = admin.firestore();
    console.log('[Firebase] Inisialisasi default credential berhasil');
  }
} catch (e) {
  console.warn('[Firebase] Firebase Admin belum terhubung credential. Mode logging callback aktif.');
}

const XENDIT_CALLBACK_TOKEN = process.env.XENDIT_CALLBACK_TOKEN || 'smart_water_xendit_webhook_secret_2026';
const XENDIT_SECRET_KEY = process.env.XENDIT_SECRET_KEY || 'xnd_development_...';

// Health Check
app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'Smart Water Xendit Webhook Server' });
});

/**
 * 1. Endpoint Callback / Webhook Xendit
 * Daftarkan URL ini di Dashboard Xendit: Settings > Callbacks > Invoices Paid
 * URL Contoh: https://domain-anda.com/xendit/callback
 */
app.post('/xendit/callback', async (req, res) => {
  try {
    const callbackToken = req.headers['x-callback-token'];

    // Verifikasi Token Callback jika diset di Dashboard Xendit
    if (XENDIT_CALLBACK_TOKEN && callbackToken && callbackToken !== XENDIT_CALLBACK_TOKEN) {
      console.warn('[Webhook] Token callback tidak cocok!');
      return res.status(403).json({ message: 'Invalid callback token' });
    }

    const payload = req.body;
    console.log('[Webhook] Menerima Notifikasi dari Xendit:', {
      id: payload.id,
      external_id: payload.external_id,
      status: payload.status,
      payment_method: payload.payment_method,
      payment_channel: payload.payment_channel,
      paid_amount: payload.paid_amount,
      paid_at: payload.paid_at
    });

    // Periksa status invoice
    if (payload.status === 'PAID' || payload.status === 'SETTLED') {
      const externalId = payload.external_id || '';
      // Ekstrak billId asli (format: "BILL-202504-001" dari "BILL-202504-001-1718000000")
      const billId = externalId.includes('-') && externalId.lastIndexOf('-') > 4
        ? externalId.substring(0, externalId.lastIndexOf('-'))
        : externalId;

      console.log(`[Webhook] Memproses pelunasan untuk tagihan: ${billId}`);

      if (db) {
        // Cari dokumen di koleksi "bills"
        const billRef = db.collection('bills').doc(billId);
        const billDoc = await billRef.get();

        if (billDoc.exists) {
          const currentBill = billDoc.data();
          const paidMethod = payload.payment_channel || payload.payment_method || 'Xendit Checkout';

          await billRef.update({
            status: 'LUNAS',
            paymentStatus: 'paid',
            paidAt: Date.now(),
            paymentMethod: paidMethod,
            transactionRef: payload.id || `XND-${Date.now()}`
          });

          console.log(`[Firestore] Tagihan ${billId} berhasil diupdate ke LUNAS/paid!`);

          // Catat ke transaksi dompet / wallet
          const walletUserId = currentBill.userId || currentBill.customerId;
          if (walletUserId) {
            try {
              const walletRef = db.collection('wallets').doc(walletUserId);
              await db.runTransaction(async (transaction) => {
                const wDoc = await transaction.get(walletRef);
                if (wDoc.exists) {
                  const currentBalance = wDoc.data().balance || 0;
                  const newBalance = currentBalance + (payload.paid_amount || currentBill.totalAmount || 0);
                  transaction.update(walletRef, {
                    balance: newBalance,
                    lastUpdated: Date.now()
                  });
                }
              });
              console.log(`[Wallet] Saldo wallet untuk user ${walletUserId} berhasil ditambah.`);
            } catch (wErr) {
              console.warn('[Wallet] Gagal update wallet:', wErr.message);
            }
          }
        } else {
          console.warn(`[Firestore] Dokumen tagihan ${billId} tidak ditemukan.`);
        }
      }
    }

    // Selalu respon 200 OK ke Xendit agar Xendit tidak melakukan retry callback berulang kali
    res.status(200).json({ received: true });
  } catch (err) {
    console.error('[Webhook Error]:', err);
    res.status(500).json({ error: err.message });
  }
});

/**
 * 2. Endpoint Pembuatan Invoice Xendit (Opsional jika ingin request dari server)
 */
app.post('/xendit/create-invoice', async (req, res) => {
  try {
    const { billId, amount, customerName, customerEmail, customerPhone, description } = req.body;

    const authHeader = 'Basic ' + Buffer.from(XENDIT_SECRET_KEY + ':').toString('base64');
    const invoicePayload = {
      external_id: `${billId}-${Date.now()}`,
      amount: Number(amount),
      description: description || `Tagihan Air Smart Water (${billId})`,
      invoice_duration: 86400,
      currency: 'IDR',
      success_redirect_url: 'smartwater://payment-return?status=success',
      failure_redirect_url: 'smartwater://payment-return?status=failed',
      customer: {
        given_names: customerName || 'Pelanggan Smart Water',
        email: customerEmail || 'pelanggan@smartwater.id',
        mobile_number: customerPhone || '+6281234567890'
      }
    };

    const response = await fetch('https://api.xendit.co/v2/invoices', {
      method: 'POST',
      headers: {
        'Authorization': authHeader,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(invoicePayload)
    });

    const data = await response.json();
    if (!response.ok) {
      return res.status(response.status).json(data);
    }

    res.json(data);
  } catch (error) {
    console.error('[Create Invoice Error]:', error);
    res.status(500).json({ error: error.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`[Smart Water] Xendit Webhook Server aktif di port ${PORT}`);
  console.log(`- Webhook URL: http://localhost:${PORT}/xendit/callback`);
});
