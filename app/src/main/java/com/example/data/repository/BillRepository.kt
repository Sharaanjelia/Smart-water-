package com.example.data.repository

import android.util.Log
import com.example.data.model.Bill
import com.example.data.model.XenditInvoiceRequest
import com.example.data.model.XenditInvoiceResponse
import com.example.data.service.XenditService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk Tagihan Air & Pembayaran
 * Menggunakan Cloud Firestore koleksi "bills".
 */
class BillRepository private constructor() {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore tidak tersedia: ${e.message}")
            null
        }
    }

    // Tagihan aktif saat ini
    private val _activeBill = MutableStateFlow<Bill?>(
        Bill(
            billId = "BILL-202504-001",
            customerId = "1234567890",
            period = "April 2025",
            previousReading = "00981",
            currentReading = "01000",
            usageM3 = 19.0,
            waterTariff = 148300L,
            adminFee = 2500L,
            totalAmount = 150800L,
            status = "BELUM_BAYAR",
            paymentStatus = "unpaid",
            dueDate = "20 April 2025"
        )
    )
    val activeBill: StateFlow<Bill?> = _activeBill.asStateFlow()

    // Riwayat tagihan untuk user aktif
    private val _billsHistory = MutableStateFlow<List<Bill>>(emptyList())
    val billsHistory: StateFlow<List<Bill>> = _billsHistory.asStateFlow()

    // Seluruh tagihan di sistem (khusus Admin)
    private val _allBills = MutableStateFlow<List<Bill>>(emptyList())
    val allBills: StateFlow<List<Bill>> = _allBills.asStateFlow()

    init {
        // Inisialisasi awal tagihan demo agar UI tetap graceful
        _billsHistory.value = listOf(
            Bill(
                billId = "BILL-202504-001",
                customerId = "1234567890",
                period = "April 2025",
                previousReading = "00981",
                currentReading = "01000",
                usageM3 = 19.0,
                waterTariff = 148300L,
                adminFee = 2500L,
                totalAmount = 150800L,
                status = "BELUM_BAYAR",
                paymentStatus = "unpaid",
                dueDate = "20 April 2025"
            ),
            Bill(
                billId = "BILL-202503-082",
                customerId = "1234567890",
                period = "Maret 2025",
                previousReading = "00962",
                currentReading = "00981",
                usageM3 = 19.0,
                waterTariff = 140000L,
                adminFee = 2500L,
                totalAmount = 142500L,
                status = "LUNAS",
                paymentStatus = "paid",
                dueDate = "20 Maret 2025",
                paidAt = 1710921600000L,
                paymentMethod = "QRIS",
                transactionRef = "TRX-20250320-991"
            )
        )
        _allBills.value = _billsHistory.value
    }

    /**
     * Membuat dan menyimpan tagihan baru ke Firestore
     */
    suspend fun createBill(bill: Bill): Result<Bill> {
        return try {
            firestore?.collection("bills")?.document(bill.billId)?.set(bill)?.await()
            Log.d(TAG, "Tagihan ${bill.billId} berhasil dibuat di Firestore")
            _activeBill.value = bill
            _billsHistory.value = listOf(bill) + _billsHistory.value.filter { it.billId != bill.billId }
            _allBills.value = listOf(bill) + _allBills.value.filter { it.billId != bill.billId }
            Result.success(bill)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membuat tagihan: ${e.message}")
            _activeBill.value = bill
            _billsHistory.value = listOf(bill) + _billsHistory.value.filter { it.billId != bill.billId }
            _allBills.value = listOf(bill) + _allBills.value.filter { it.billId != bill.billId }
            Result.success(bill)
        }
    }

    /**
     * Memproses pembayaran tagihan
     */
    suspend fun payBill(billId: String, paymentMethod: String, expectedAmount: Long = -1L): Result<Bill> {
        val currentBills = _billsHistory.value
        val targetBill = currentBills.find { it.billId == billId }
            ?: _allBills.value.find { it.billId == billId }
            ?: _activeBill.value
            ?: return Result.failure(IllegalArgumentException("Tagihan tidak ditemukan"))

        val fixedAmount = if (expectedAmount > 0L) expectedAmount else targetBill.totalAmount
        if (fixedAmount != targetBill.totalAmount) {
            return Result.failure(
                IllegalArgumentException(
                    "Nominal pembayaran tidak valid. Harus sesuai tagihan Rp ${targetBill.totalAmount}"
                )
            )
        }

        val updatedBill = targetBill.copy(
            status = "LUNAS",
            paymentStatus = "paid",
            paidAt = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            transactionRef = "TRX-${System.currentTimeMillis()}"
        )

        return try {
            firestore?.collection("bills")?.document(billId)?.set(updatedBill)?.await()
            Log.d(TAG, "Pembayaran tagihan $billId berhasil diupdate ke LUNAS/paid di Firestore")

            val walletUserId = if (targetBill.userId.isNotBlank()) targetBill.userId else targetBill.customerId
            if (walletUserId.isNotBlank()) {
                WalletRepository.getInstance().creditWallet(
                    userId = walletUserId,
                    amount = targetBill.totalAmount,
                    note = "Pembayaran tagihan ${targetBill.billId}",
                    referenceId = updatedBill.transactionRef
                )
            }

            _billsHistory.value = currentBills.map {
                if (it.billId == billId) updatedBill else it
            }
            _allBills.value = _allBills.value.map {
                if (it.billId == billId) updatedBill else it
            }
            if (_activeBill.value?.billId == billId) {
                _activeBill.value = updatedBill
            }
            Result.success(updatedBill)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memproses pembayaran: ${e.message}")
            _billsHistory.value = currentBills.map {
                if (it.billId == billId) updatedBill else it
            }
            _allBills.value = _allBills.value.map {
                if (it.billId == billId) updatedBill else it
            }
            if (_activeBill.value?.billId == billId) {
                _activeBill.value = updatedBill
            }
            Result.success(updatedBill)
        }
    }

    /**
     * Memuat tagihan dari Firestore untuk user tertentu (Customer)
     */
    fun loadBillsForUser(userId: String) {
        val db = firestore ?: return
        db.collection("bills")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error load bills: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.toObjects(Bill::class.java)
                    _billsHistory.value = list
                    _activeBill.value = list.find { it.status == "BELUM_BAYAR" || it.paymentStatus == "unpaid" } ?: list.firstOrNull()
                }
            }
    }

    /**
     * Listener real-time seluruh tagihan untuk Admin PDAM
     */
    fun observeAllBills() {
        val db = firestore ?: return
        db.collection("bills")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error observe all bills: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.toObjects(Bill::class.java)
                    _allBills.value = list
                }
            }
    }

    /**
     * Membuat Invoice Xendit untuk tagihan yang dipilih
     */
    suspend fun createXenditInvoice(
        billId: String,
        customerName: String = "Pelanggan Smart Water",
        customerEmail: String = "pelanggan@smartwater.id",
        customerPhone: String = "+6281234567890"
    ): Result<XenditInvoiceResponse> {
        val currentBills = _billsHistory.value
        val targetBill = currentBills.find { it.billId == billId }
            ?: _allBills.value.find { it.billId == billId }
            ?: _activeBill.value
            ?: return Result.failure(IllegalArgumentException("Tagihan tidak ditemukan"))

        val externalId = "${targetBill.billId}-${System.currentTimeMillis()}"
        val request = XenditInvoiceRequest(
            externalId = externalId,
            amount = targetBill.totalAmount,
            description = "Tagihan Air Smart Water Periode ${targetBill.period} (${targetBill.billId})",
            customerName = customerName,
            customerEmail = customerEmail,
            customerPhone = customerPhone
        )

        val invoiceResult = XenditService.getInstance().createInvoice(request)
        if (invoiceResult.isSuccess) {
            val invoice = invoiceResult.getOrThrow()
            val updatedBill = targetBill.copy(
                invoiceUrl = invoice.invoiceUrl,
                xenditInvoiceId = invoice.id,
                paymentStatus = "pending"
            )

            try {
                firestore?.collection("bills")?.document(billId)?.set(updatedBill)?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Gagal update invoiceUrl ke Firestore: ${e.message}")
            }

            _billsHistory.value = currentBills.map { if (it.billId == billId) updatedBill else it }
            _allBills.value = _allBills.value.map { if (it.billId == billId) updatedBill else it }
            if (_activeBill.value?.billId == billId) {
                _activeBill.value = updatedBill
            }
        }
        return invoiceResult
    }

    /**
     * Mengecek status pembayaran Xendit secara langsung (API check)
     */
    suspend fun checkXenditInvoiceStatus(billId: String, invoiceId: String): Result<Bill> {
        val invoiceResult = XenditService.getInstance().getInvoice(invoiceId)
        if (invoiceResult.isFailure) {
            return Result.failure(invoiceResult.exceptionOrNull() ?: Exception("Gagal mengecek invoice"))
        }

        val invoice = invoiceResult.getOrThrow()
        if (invoice.isPaid) {
            val paymentMethod = invoice.paymentChannel ?: invoice.paymentMethod ?: "Xendit Checkout"
            return payBill(billId = billId, paymentMethod = paymentMethod, expectedAmount = invoice.amount)
        }

        val targetBill = _billsHistory.value.find { it.billId == billId }
            ?: _activeBill.value
            ?: return Result.failure(IllegalArgumentException("Tagihan tidak ditemukan"))

        return Result.success(targetBill)
    }

    /**
     * Mensimulasikan pembayaran lunas di Sandbox Xendit
     */
    suspend fun simulateXenditPayment(billId: String, invoiceId: String): Result<Bill> {
        val simResult = XenditService.getInstance().simulatePaymentSandbox(invoiceId)
        if (simResult.isSuccess) {
            val targetBill = _billsHistory.value.find { it.billId == billId } ?: _activeBill.value
            val amount = targetBill?.totalAmount ?: 150800L
            return payBill(billId = billId, paymentMethod = "Xendit Sandbox (QRIS/VA)", expectedAmount = amount)
        }
        return Result.failure(Exception("Gagal mensimulasikan pembayaran Xendit Sandbox"))
    }

    companion object {
        private const val TAG = "BillRepository"
        @Volatile
        private var instance: BillRepository? = null

        fun getInstance(): BillRepository {
            return instance ?: synchronized(this) {
                instance ?: BillRepository().also { instance = it }
            }
        }
    }
}
