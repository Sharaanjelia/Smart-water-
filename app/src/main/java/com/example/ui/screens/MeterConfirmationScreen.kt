package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen 12: Konfirmasi Laporan (Konfirmasi Meter)
 * - Top Bar: Back arrow, "Konfirmasi Laporan"
 * - Subtitle: "Berhasil mendeteksi angka meter"
 * - Card: RINCIAN PEMAKAIAN
 *    - Angka Bulan Lalu: 1.000 m³ · 01 April 2025
 *    - Angka Bulan Ini: 1.358 m³
 *    - Digital counter pill: 01358 (orange digits on dark pill)
 *    - Pemakaian: 358 m³ | No. Meter: 12345678
 * - Card: Mar 2025 (Estimasi)
 *    - Rp 0 | Belum ada informasi
 * - Bottom Bar: "Kirim Laporan" (Outline) + "Bayar Sekarang" (Solid Blue)
 */
@Composable
fun MeterConfirmationScreen(
    onNavigateBack: () -> Unit,
    onSubmitReport: (String) -> Unit,
    onPayNow: () -> Unit,
    detectedValue: String = "01358",
    previousValue: String = "01000",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Perhitungan dinamis pemakaian dan tagihan
    val currentNum = detectedValue.toDoubleOrNull() ?: 0.0
    val prevNum = previousValue.toDoubleOrNull() ?: 0.0
    val isValid = currentNum >= prevNum
    val usageM3 = if (isValid) (currentNum - prevNum) else 0.0
    val tariffPerM3 = 150L // Prototype tarif simulasi Rp 150/m3
    val adminFee = 2500L
    val estimatedBill = (usageM3 * tariffPerM3).toLong() + adminFee

    val formattedDigits = detectedValue.padStart(5, '0').toList().joinToString(" ")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("meter_confirmation_screen_container")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("confirmation_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = "Konfirmasi Laporan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.testTag("confirmation_title")
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                // Subtitle
                Text(
                    text = "Berhasil mendeteksi angka meter",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                // Banner Peringatan jika angka tidak valid
                if (!isValid) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ Angka meter saat ini ($detectedValue m³) lebih kecil dari angka sebelumnya ($previousValue m³). Harap foto ulang atau periksa kembali angka meter Anda.",
                                fontSize = 12.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Card: RINCIAN PEMAKAIAN
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = Color(0x15000000),
                            spotColor = Color(0x25000000)
                        )
                        .testTag("rincian_pemakaian_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "RINCIAN PEMAKAIAN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Angka Bulan Lalu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Angka Bulan Lalu",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Periode Sebelumnya",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(
                                text = "%,.0f m³".format(prevNum).replace(',', '.'),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Angka Bulan Ini
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Angka Bulan Ini",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "%,.0f m³".format(currentNum).replace(',', '.'),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isValid) Color(0xFF0F172A) else Color(0xFFDC2626)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Digital Display counter
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formattedDigits,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isValid) Color(0xFFFF4834) else Color(0xFFF87171),
                                letterSpacing = 4.sp,
                                modifier = Modifier.testTag("digital_counter_01358")
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Big Highlight Row: Pemakaian & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pemakaian",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "%,.0f m³".format(usageM3).replace(',', '.'),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isValid) SmartWaterPrimary else Color(0xFFDC2626),
                                    letterSpacing = (-0.5).sp,
                                    modifier = Modifier.testTag("pemakaian_highlight_text")
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Status Validasi",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = if (isValid) "Sesuai Standar" else "Tidak Sesuai",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isValid) Color(0xFF16A34A) else Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card: Estimasi Tagihan
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0x10000000),
                            spotColor = Color(0x15000000)
                        )
                        .testTag("estimasi_tagihan_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Estimasi Tagihan Periode Ini",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SmartWaterPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Rp %,d".format(estimatedBill).replace(',', '.'),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmartWaterPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Pemakaian %,.0f m³ × Rp 150 + Biaya Admin Rp 2.500".format(usageM3),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Actions: Kirim Laporan & Bayar Sekarang
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Secondary Action: Kirim Laporan
                OutlinedButton(
                    onClick = {
                        if (isValid) {
                            showSuccessDialog = true
                        } else {
                            Toast.makeText(
                                context,
                                "Gagal: Angka meter baru tidak boleh lebih kecil dari angka sebelumnya!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    enabled = isValid,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF334155)
                    ),
                    border = BorderStroke(1.5.dp, if (isValid) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("kirim_laporan_button")
                ) {
                    Text(
                        text = "Kirim\nLaporan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }

                // Primary Action: Bayar Sekarang
                Button(
                    onClick = {
                        if (isValid) {
                            onSubmitReport(detectedValue)
                            onPayNow()
                        } else {
                            Toast.makeText(
                                context,
                                "Gagal: Angka meter baru tidak boleh lebih kecil dari angka sebelumnya!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    enabled = isValid,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SmartWaterPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(52.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color(0x33005BBB),
                            spotColor = Color(0x550066EE)
                        )
                        .testTag("bayar_sekarang_button")
                ) {
                    Text(
                        text = "Bayar Sekarang",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Success Dialog on Kirim Laporan
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Text(text = "Laporan Terkirim! ✅", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        text = "Angka meter $detectedValue (Pemakaian %,.0f m³) berhasil dicatat di Firestore. Estimasi tagihan: Rp %,d. Terima kasih telah melakukan pelaporan mandiri.".format(usageM3, estimatedBill).replace(',', '.'),
                        fontSize = 14.sp,
                        color = Color(0xFF475569)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onSubmitReport(detectedValue)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Kembali ke Beranda")
                    }
                }
            )
        }
    }
}
