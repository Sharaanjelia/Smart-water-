package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary
import com.example.ui.components.SmartWaterBottomBar
import com.example.ui.components.SmartWaterTab

/**
 * Screen 17: Riwayat Tagihan
 * - Blue header banner with title "Riwayat Tagihan"
 * - List of payment history cards:
 *    - Maret 2025 | Rp 145.000 | chevron right >
 *      - Lunas 08 Apr 2025
 *      - TRK202504000111
 *      - Green pill "Lunas"
 *    - Februari 2025 | Rp 130.000 | >
 *      - Lunas 09 Mar 2025
 *      - TRK202503000458
 *      - Green pill "Lunas"
 *    - Januari 2025 | Rp 142.000 | >
 *      - Lunas 08 Feb 2025
 *      - TRK202502000789
 *      - Green pill "Lunas"
 *    - Desember 2024 | Rp 138.000 | >
 *      - Lunas 08 Jan 2025
 *      - TRK202501000112
 *      - Green pill "Lunas"
 * - Bottom Navigation Bar (Tab 2: Tagihan Active)
 */
data class BillHistoryItem(
    val month: String,
    val amount: String,
    val datePaid: String,
    val transactionId: String,
    val status: String = "Lunas",
    val usage: String = "340 m³"
)

@Composable
fun BillHistoryScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToBill: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf<BillHistoryItem?>(null) }

    val historyList = listOf(
        BillHistoryItem(
            month = "Maret 2025",
            amount = "Rp 145.000",
            datePaid = "08 Apr 2025",
            transactionId = "TRK202504000111",
            usage = "345 m³"
        ),
        BillHistoryItem(
            month = "Februari 2025",
            amount = "Rp 130.000",
            datePaid = "09 Mar 2025",
            transactionId = "TRK202503000458",
            usage = "310 m³"
        ),
        BillHistoryItem(
            month = "Januari 2025",
            amount = "Rp 142.000",
            datePaid = "08 Feb 2025",
            transactionId = "TRK202502000789",
            usage = "338 m³"
        ),
        BillHistoryItem(
            month = "Desember 2024",
            amount = "Rp 138.000",
            datePaid = "08 Jan 2025",
            transactionId = "TRK202501000112",
            usage = "328 m³"
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .testTag("bill_history_screen_container")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: Vibrant Blue Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0060DC),
                                SmartWaterPrimary
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .testTag("bill_history_header_banner")
            ) {
                Text(
                    text = "Riwayat Tagihan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.testTag("bill_history_title")
                )
            }

            // List of History Cards
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(historyList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = Color(0x10000000),
                                spotColor = Color(0x15000000)
                            )
                            .clickable { selectedItem = item }
                            .testTag("history_card_${item.month.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Top Row: Month and Amount + Arrow
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.month,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.amount,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Detail",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Bottom Row: Lunas info, Transaction ID, and Lunas Pill Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "Lunas ${item.datePaid}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.transactionId,
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                // Light green pill badge "Lunas"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            SmartWaterBottomBar(
                selectedTab = SmartWaterTab.TAGIHAN,
                onNavigateToHome = onNavigateToHome,
                onNavigateToTagihan = onNavigateToBill,
                onNavigateToMeter = onNavigateToReport,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToProfile = onNavigateToProfile
            )
        }

        // Receipt Modal Dialog when item clicked
        selectedItem?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = SmartWaterPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Detail Pembayaran",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Periode ${item.month}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(8.dp))
                        HistoryDetailRow(label = "Nominal", value = item.amount, isBold = true)
                        Spacer(modifier = Modifier.height(6.dp))
                        HistoryDetailRow(label = "Volume Pemakaian", value = item.usage)
                        Spacer(modifier = Modifier.height(6.dp))
                        HistoryDetailRow(label = "Tanggal Bayar", value = item.datePaid)
                        Spacer(modifier = Modifier.height(6.dp))
                        HistoryDetailRow(label = "No. Transaksi", value = item.transactionId)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LUNAS - Pembayaran Terverifikasi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedItem = null },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryDetailRow(
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

