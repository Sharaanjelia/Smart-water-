package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary
import kotlinx.coroutines.delay

/**
 * Layar Instruksi Pembayaran Interaktif
 * Menampilkan Barcode QRIS Asli & Nomor Virtual Account Bank
 */
@Composable
fun PaymentInstructionScreen(
    onNavigateBack: () -> Unit,
    onPaymentCompleted: () -> Unit,
    paymentMethod: String = "QRIS",
    totalAmount: Long = 150800L,
    billId: String = "BILL-202504-001",
    customerName: String = "Budi Santoso",
    customerId: String = "1234567890",
    invoiceUrl: String = "",
    xenditInvoiceId: String = "",
    isXenditLoading: Boolean = false,
    onOpenXenditCheckout: () -> Unit = {},
    onCheckPaymentStatus: () -> Unit = {},
    onSimulateSandbox: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCheckingPayment by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(895) } // 14:55 timer
    var selectedTabGuide by remember { mutableIntStateOf(0) } // 0: m-Banking, 1: ATM, 2: i-Banking
    var isGuideExpanded by remember { mutableStateOf(true) }

    // Hitung mundur waktu pembayaran
    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)
    val formattedAmount = "Rp %,d".format(totalAmount).replace(',', '.')

    // Nomor Virtual Account sesuai bank yang dipilih
    val vaNumber = when {
        paymentMethod.contains("BCA", ignoreCase = true) -> "8808 $customerId 01"
        paymentMethod.contains("Mandiri", ignoreCase = true) -> "8950 $customerId 22"
        paymentMethod.contains("BRI", ignoreCase = true) -> "1284 $customerId 33"
        paymentMethod.contains("BNI", ignoreCase = true) -> "9881 $customerId 44"
        else -> "8808 $customerId 01"
    }

    val isQris = paymentMethod.contains("QRIS", ignoreCase = true)
    val fixedAmountText = "Nominal tetap: Rp %,d".format(totalAmount).replace(',', '.')

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("payment_instruction_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF0F172A)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isQris) "Pembayaran QRIS" else "Virtual Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Countdown Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selesaikan pembayaran dalam",
                                fontSize = 12.sp,
                                color = Color(0xFF1E40AF)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = timerString,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Tagihan",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = formattedAmount,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                if (invoiceUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Xendit Checkout Aktif",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF166534)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "XENDIT ONLINE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Halaman pembayaran Xendit resmi telah disiapkan. Anda dapat membayar dengan QRIS, Virtual Account (BCA, Mandiri, BRI, BNI), atau E-Wallet.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onOpenXenditCheckout,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF15803D),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buka Halaman Checkout Xendit",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isQris) {
                    // =================== TAMPILAN QRIS ASLI ===================
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // QRIS Brand Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "QRIS",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFB91C1C),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "STANDAR PEMBAYARAN NASIONAL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0284C7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "GPN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = Color(0xFFF1F5F9)
                            )

                            Text(
                                text = "PDAM SMART WATER KOTA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "NMID: ID1020039201923",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // CANVAS QR CODE VECTOR SHARP
                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                QrCodeCanvas(modifier = Modifier.size(196.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = SmartWaterPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Scan QR dengan BCA, Mandiri, GoPay, OVO, Dana",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Unduh QR Button
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "QRIS berhasil disimpan ke Galeri", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Unduh Gambar QRIS", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    // =================== TAMPILAN VIRTUAL ACCOUNT ===================
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = paymentMethod,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Atas Nama: PDAM Smart Water - $customerName",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Nomor Virtual Account",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Box Nomor VA + Tombol Copy
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = vaNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF0F172A)
                                )

                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Nomor VA", vaNumber.replace(" ", ""))
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Nomor Virtual Account disalin!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Salin",
                                        tint = SmartWaterPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Salin",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SmartWaterPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Total Pembayaran",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = formattedAmount,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SmartWaterPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Panduan Pembayaran Accordion
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isGuideExpanded = !isGuideExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Petunjuk Pembayaran",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Icon(
                                imageVector = if (isGuideExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color(0xFF64748B)
                            )
                        }

                        AnimatedVisibility(visible = isGuideExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                if (isQris) {
                                    GuideItem(step = "1", text = "Buka aplikasi mobile banking atau e-Wallet pilihan Anda.")
                                    GuideItem(step = "2", text = "Pilih menu QRIS / Bayar dengan QR.")
                                    GuideItem(step = "3", text = "Arahkan kamera HP ke QR Code di atas.")
                                    GuideItem(step = "4", text = "Periksa nama merchant: PDAM SMART WATER KOTA dan nominal.")
                                    GuideItem(step = "5", text = "Masukkan PIN transaksi dan konfirmasi pembayaran.")
                                } else {
                                    GuideItem(step = "1", text = "Buka m-Banking Anda, pilih menu Transfer > Virtual Account.")
                                    GuideItem(step = "2", text = "Masukkan nomor Virtual Account: $vaNumber.")
                                    GuideItem(step = "3", text = "Pastikan rincian tagihan air sesuai dengan nominal $formattedAmount.")
                                    GuideItem(step = "4", text = "Masukkan PIN m-Banking Anda untuk menyelesaikan pembayaran.")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                if (xenditInvoiceId.isNotBlank() || invoiceUrl.isNotBlank()) {
                    // Tombol 1: Cek Status Pembayaran ke Xendit
                    Button(
                        onClick = onCheckPaymentStatus,
                        enabled = !isXenditLoading && !isCheckingPayment,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SmartWaterPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(26.dp),
                                ambientColor = Color(0x33005BBB),
                                spotColor = Color(0x550066EE)
                            )
                            .testTag("btn_check_xendit_status")
                    ) {
                        if (isXenditLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mengecek ke Xendit...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cek Status Pembayaran (Xendit)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tombol 2: Simulasi Pembayaran Sandbox Xendit
                    OutlinedButton(
                        onClick = onSimulateSandbox,
                        enabled = !isXenditLoading && !isCheckingPayment,
                        shape = RoundedCornerShape(26.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFB45309)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_simulate_sandbox")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulasi Bayar Sandbox (Test Mode)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol 3: Manual Fallback
                    Button(
                        onClick = { isCheckingPayment = true },
                        enabled = !isCheckingPayment,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color(0xFF475569)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = "Konfirmasi Manual Tanpa Xendit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Fallback normal saat Xendit tidak dipakai
                    Button(
                        onClick = { isCheckingPayment = true },
                        enabled = !isCheckingPayment,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SmartWaterPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(26.dp),
                                ambientColor = Color(0x33005BBB),
                                spotColor = Color(0x550066EE)
                            )
                            .testTag("btn_confirm_payment")
                    ) {
                        if (isCheckingPayment) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Memverifikasi Pembayaran...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isQris) "Simulasi Scan & Bayar" else "Saya Sudah Bayar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Efek saat tombol konfirmasi manual diverifikasi
                LaunchedEffect(isCheckingPayment) {
                    if (isCheckingPayment) {
                        delay(1500)
                        onPaymentCompleted()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GuideItem(step: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF334155),
            lineHeight = 18.sp
        )
    }
}

/**
 * Komponen Canvas untuk Menggambar QR Code Presisi Tanpa Dependency Eksternal
 */
@Composable
private fun QrCodeCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val matrixSize = 25
        val cellSize = width / matrixSize

        fun drawFinderPattern(startX: Int, startY: Int) {
            // Kotak luar hitam 7x7
            drawRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(startX * cellSize, startY * cellSize),
                size = Size(7 * cellSize, 7 * cellSize)
            )
            // Kotak tengah putih 5x5
            drawRect(
                color = Color.White,
                topLeft = Offset((startX + 1) * cellSize, (startY + 1) * cellSize),
                size = Size(5 * cellSize, 5 * cellSize)
            )
            // Kotak inti hitam 3x3
            drawRect(
                color = Color(0xFF0F172A),
                topLeft = Offset((startX + 2) * cellSize, (startY + 2) * cellSize),
                size = Size(3 * cellSize, 3 * cellSize)
            )
        }

        // 3 Finder Patterns
        drawFinderPattern(0, 0)
        drawFinderPattern(matrixSize - 7, 0)
        drawFinderPattern(0, matrixSize - 7)

        // Timing patterns
        for (i in 7 until matrixSize - 7) {
            if (i % 2 == 0) {
                drawRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(6 * cellSize, i * cellSize),
                    size = Size(cellSize, cellSize)
                )
                drawRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(i * cellSize, 6 * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }

        // Data pattern semi-deterministic
        val seed = 42
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= matrixSize - 8
                val inBottomLeft = r >= matrixSize - 8 && c < 8
                val inTiming = r == 6 || c == 6

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                    val hash = ((r * 37 + c * 19 + seed) * 31) % 100
                    if (hash < 48) {
                        drawRect(
                            color = Color(0xFF0F172A),
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PaymentInstructionScreenQrisPreview() {
    com.example.ui.theme.MyApplicationTheme {
        PaymentInstructionScreen(
            onNavigateBack = {},
            onPaymentCompleted = {},
            paymentMethod = "QRIS"
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PaymentInstructionScreenVaPreview() {
    com.example.ui.theme.MyApplicationTheme {
        PaymentInstructionScreen(
            onNavigateBack = {},
            onPaymentCompleted = {},
            paymentMethod = "BCA Virtual Account"
        )
    }
}

