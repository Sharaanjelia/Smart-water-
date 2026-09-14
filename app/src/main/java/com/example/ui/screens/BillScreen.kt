package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SmartWaterBottomBar
import com.example.ui.components.SmartWaterTab

/**
 * Screen 13: Tagihan Saya
 * Redesigned to match the clean modern aesthetic:
 * - Blue gradient header with back button, modern typography, and 3D glassy water droplet illustration
 * - Segmented tabs: Tagihan Aktif | Riwayat
 * - Clean white card with soft shadow and rounded corners (24dp):
 *    - Calendar icon + "April 2025" and red status badge "Belum Bayar"
 *    - Total Tagihan with large font "Rp 150.000" and due date
 *    - RINCIAN TAGIHAN with structured colorful icon badges:
 *        - Pemakaian Air (350 m³): Rp 53.700
 *        - Administrasi: Rp 5.000
 *        - Kompleter: Rp 5.000
 *        - Denda: Rp 0
 *    - Summary container with highlight "Rp 150.000"
 * - Prominent "Bayar Sekarang" pill button with wallet icon and arrow
 * - Trust guarantee badge "Pembayaran Anda aman dan terenkripsi"
 * - Clean SmartWaterBottomBar with 5 tabs
 */
@Composable
fun BillScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToNotification: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Tagihan Aktif, 1: Riwayat

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .testTag("bill_screen_container")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: Blue Gradient Banner with Modern 3D Water Droplet Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0072F5),
                                Color(0xFF0052CC)
                            )
                        )
                    )
                    .testTag("bill_header_banner")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp)
                ) {
                    // Back Navigation Button
                    IconButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("bill_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Beranda",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title & Subtitle
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Tagihan Saya",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.testTag("bill_header_title")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Pantau dan kelola tagihan air Anda\ndengan mudah dan aman.",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }

                        // Right 3D Illustration: Floating Water Droplet & Bill Document
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_bill_header_water_doc),
                                contentDescription = "Ilustrasi Tagihan Air",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Segmented Tabs Pill: Tagihan Aktif | Riwayat
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color(0xFFEFF4FA))
                            .padding(4.dp)
                            .testTag("bill_segmented_tabs"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tagihan Aktif Tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (selectedTab == 0) Color(0xFF0066CC) else Color.Transparent)
                                .clickable { selectedTab = 0 }
                                .padding(horizontal = 28.dp, vertical = 9.dp)
                                .testTag("tab_tagihan_aktif"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tagihan Aktif",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) Color.White else Color(0xFF64748B)
                            )
                        }

                        // Riwayat Tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (selectedTab == 1) Color(0xFF0066CC) else Color.Transparent)
                                .clickable {
                                    selectedTab = 1
                                    onNavigateToHistory()
                                }
                                .padding(horizontal = 28.dp, vertical = 9.dp)
                                .testTag("tab_riwayat"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Riwayat",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (selectedTab == 0) {
                    // Tagihan Aktif Card (Clean Shadow and Rounded Corners)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x0A000000),
                                spotColor = Color(0x18000000)
                            )
                            .testTag("active_bill_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Top Row: Month with Calendar Icon and Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFEFF6FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Color(0xFF0066CC),
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = "April 2025",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.testTag("bill_month_text")
                                    )
                                }

                                // Soft Red "Belum Bayar" Badge with red dot
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFFFEBEB))
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                        .testTag("unpaid_badge")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE11D48))
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Belum Bayar",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE11D48)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Total Tagihan Big Amount
                            Text(
                                text = "Total Tagihan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Rp 150.000",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0066CC),
                                letterSpacing = (-0.5).sp,
                                modifier = Modifier.testTag("bill_amount_text")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Jatuh tempo 10 Mei 2025",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.testTag("bill_due_date_text")
                                )
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            // Subheader: RINCIAN TAGIHAN
                            Text(
                                text = "RINCIAN TAGIHAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.8.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Structured Item 1: Pemakaian Air
                            BillStructuredLineItem(
                                icon = Icons.Default.WaterDrop,
                                iconBgColor = Color(0xFFE0F2FE),
                                iconTint = Color(0xFF0284C7),
                                title = "Pemakaian Air",
                                subtitle = "(350 m³)",
                                amount = "Rp 53.700"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Structured Item 2: Administrasi
                            BillStructuredLineItem(
                                icon = Icons.Default.Build,
                                iconBgColor = Color(0xFFEDE9FE),
                                iconTint = Color(0xFF6366F1),
                                title = "Administrasi",
                                subtitle = null,
                                amount = "Rp 5.000"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Structured Item 3: Kompleter
                            BillStructuredLineItem(
                                icon = Icons.Default.Person,
                                iconBgColor = Color(0xFFDCFCE7),
                                iconTint = Color(0xFF10B981),
                                title = "Kompleter",
                                subtitle = null,
                                amount = "Rp 5.000"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Structured Item 4: Denda
                            BillStructuredLineItem(
                                icon = Icons.Default.Receipt,
                                iconBgColor = Color(0xFFF1F5F9),
                                iconTint = Color(0xFF64748B),
                                title = "Denda",
                                subtitle = null,
                                amount = "Rp 0"
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Bottom Sub-summary inside card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF0F7FF))
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Tagihan",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Jatuh tempo 10 Mei 2025",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Text(
                                        text = "Rp 150.000",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0066CC),
                                        modifier = Modifier.testTag("bill_total_highlight")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Prominent Action Button: Bayar Sekarang
                    Button(
                        onClick = onNavigateToDetail,
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0066CC),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(27.dp),
                                ambientColor = Color(0x330066CC),
                                spotColor = Color(0x550066CC)
                            )
                            .testTag("bayar_sekarang_main_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Bayar Sekarang",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Security Guarantee Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pembayaran Anda aman dan terenkripsi",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                    }
                } else {
                    // Riwayat Tab Content
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        HistoryBillCard(
                            month = "Maret 2025",
                            amount = "Rp 142.500",
                            datePaid = "04 April 2025",
                            status = "Lunas",
                            usage = "340 m³"
                        )
                        HistoryBillCard(
                            month = "Februari 2025",
                            amount = "Rp 138.000",
                            datePaid = "03 Maret 2025",
                            status = "Lunas",
                            usage = "328 m³"
                        )
                        HistoryBillCard(
                            month = "Januari 2025",
                            amount = "Rp 145.200",
                            datePaid = "05 Februari 2025",
                            status = "Lunas",
                            usage = "348 m³"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation Bar (Tab 2 "Tagihan" Active)
            SmartWaterBottomBar(
                selectedTab = SmartWaterTab.TAGIHAN,
                onNavigateToHome = onNavigateToHome,
                onNavigateToTagihan = {},
                onNavigateToMeter = onNavigateToReport,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

/**
 * Structured bill line item with rounded colored icon badge
 */
@Composable
private fun BillStructuredLineItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String?,
    amount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
private fun HistoryBillCard(
    month: String,
    amount: String,
    datePaid: String,
    status: String,
    usage: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x0A000000),
                spotColor = Color(0x14000000)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = month,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Pemakaian $usage • Dibayar $datePaid",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}
