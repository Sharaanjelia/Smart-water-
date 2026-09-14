package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen 16: Pembayaran Berhasil! (Payment Success)
 * - Soft greenish/clean background
 * - Big green circular checkmark icon
 * - Text: "Pembayaran Berhasil!" (green bold)
 * - Amount: "Rp 150.000"
 * - Receipt Card:
 *    - Total Pembayaran: Rp 150.000
 *    - Metode Pembayaran: QRIS (or selected method)
 *    - ID Transaksi: TRK202505000001
 *    - Tanggal: 1 Mei 2025, 10:30
 * - Buttons:
 *    - "Lihat Resi" (Solid blue)
 *    - "Kembali ke Beranda" (Outlined blue)
 */
@Composable
fun PaymentSuccessScreen(
    onNavigateToHome: () -> Unit,
    amount: String = "Rp 150.000",
    paymentMethod: String = "QRIS",
    transactionId: String = "TRK202505000001",
    date: String = "1 Mei 2025, 10:30",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showReceiptDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1FAF3))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("payment_success_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Large Green Check Circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF16A34A))
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x3016A34A),
                        spotColor = Color(0x5016A34A)
                    )
                    .testTag("payment_success_icon"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Berhasil",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Title: Pembayaran Berhasil!
            Text(
                text = "Pembayaran Berhasil!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A),
                modifier = Modifier.testTag("payment_success_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Amount: Rp 150.000
            Text(
                text = amount,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp,
                modifier = Modifier.testTag("payment_success_amount")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Receipt Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color(0x10000000),
                        spotColor = Color(0x18000000)
                    )
                    .testTag("payment_receipt_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    ReceiptRow(label = "Total Pembayaran", value = amount, isBold = true)
                    Spacer(modifier = Modifier.height(10.dp))

                    ReceiptRow(label = "Metode Pembayaran", value = paymentMethod)
                    Spacer(modifier = Modifier.height(10.dp))

                    ReceiptRow(label = "ID Transaksi", value = transactionId)
                    Spacer(modifier = Modifier.height(10.dp))

                    ReceiptRow(label = "Tanggal", value = date)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary: Lihat Resi
                Button(
                    onClick = {
                        showReceiptDialog = true
                    },
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
                        .testTag("lihat_resi_button")
                ) {
                    Text(
                        text = "Lihat Resi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Secondary: Kembali ke Beranda
                OutlinedButton(
                    onClick = onNavigateToHome,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SmartWaterPrimary
                    ),
                    border = BorderStroke(1.5.dp, SmartWaterPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("kembali_ke_beranda_button")
                ) {
                    Text(
                        text = "Kembali ke Beranda",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Resi Details Modal Dialog
        if (showReceiptDialog) {
            AlertDialog(
                onDismissRequest = { showReceiptDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = SmartWaterPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resi Pembayaran Resmi",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "PAM Smart Water - Bukti Pembayaran",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(8.dp))
                        ReceiptRow(label = "No. Pelanggan", value = "12345678")
                        Spacer(modifier = Modifier.height(6.dp))
                        ReceiptRow(label = "Nama", value = "Budi Santoso")
                        Spacer(modifier = Modifier.height(6.dp))
                        ReceiptRow(label = "Periode", value = "April 2025")
                        Spacer(modifier = Modifier.height(6.dp))
                        ReceiptRow(label = "Pemakaian", value = "358 m³")
                        Spacer(modifier = Modifier.height(6.dp))
                        ReceiptRow(label = "Metode", value = paymentMethod)
                        Spacer(modifier = Modifier.height(6.dp))
                        ReceiptRow(label = "Total Lunas", value = amount, isBold = true)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Status: LUNAS (Verified by Bank Settlement)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF16A34A)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showReceiptDialog = false
                            Toast.makeText(context, "Resi berhasil diunduh ke perangkat", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Simpan Resi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReceiptDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isBold) SmartWaterPrimary else Color(0xFF0F172A)
        )
    }
}
