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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 * Screen 18: Notifikasi
 * - Blue Header Banner with title "Notifikasi"
 * - List of notifications:
 *    1. Blue Dot:
 *       - Pengingat Notifikasi
 *       - Aktifkan notifikasi untuk menerima tagihan bulanan.
 *       - 2 jam lalu
 *    2. Amber Dot:
 *       - Privasi & Keamanan
 *       - Perbarui PIN Anda secara berkala untuk keamanan akun.
 *       - 1 hari lalu
 *    3. Green Dot:
 *       - No. Meter Air
 *       - Nomor meter Anda: 12345678
 *       - 3 hari lalu
 *    4. Grey Dot:
 *       - Kategori Tarif
 *       - R-1 (Rumah Tangga) – Tarif standar berlaku
 *       - 5 hari lalu
 *    5. Red Dot:
 *       - FAQ / Bantuan
 *       - Hubungi kami jika ada masalah pembacaan meter.
 *       - 1 minggu lalu
 * - Bottom Navigation Bar (Tab 4: Notifikasi Active)
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val dotColor: Color
)

@Composable
fun NotificationScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToBill: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val notificationList = listOf(
        NotificationItem(
            id = "notif_air_1",
            title = "✅ Layanan Air Normal",
            description = "Sistem distribusi air berjalan normal. Tekanan dan debit di wilayah Anda sesuai standar.",
            time = "1 jam lalu",
            dotColor = Color(0xFF22C55E) // hijau — kondisi normal
        ),
        NotificationItem(
            id = "notif_air_2",
            title = "ℹ️ Pemeliharaan Jaringan",
            description = "Pemeliharaan jaringan pipa di Sektor Utara dijadwalkan 16 April 2025, pukul 08.00–12.00 WIB. Siapkan cadangan air.",
            time = "3 jam lalu",
            dotColor = Color(0xFFF59E0B) // amber — info maintenance
        ),
        NotificationItem(
            id = "notif_1",
            title = "Pengingat Notifikasi",
            description = "Aktifkan notifikasi untuk menerima tagihan bulanan.",
            time = "2 jam lalu",
            dotColor = Color(0xFF1D4ED8) // vibrant blue
        ),
        NotificationItem(
            id = "notif_2",
            title = "Privasi & Keamanan",
            description = "Perbarui PIN Anda secara berkala untuk keamanan akun.",
            time = "1 hari lalu",
            dotColor = Color(0xFFF59E0B) // amber
        ),
        NotificationItem(
            id = "notif_3",
            title = "No. Meter Air",
            description = "Nomor meter Anda: 12345678",
            time = "3 hari lalu",
            dotColor = Color(0xFF10B981) // green
        ),
        NotificationItem(
            id = "notif_4",
            title = "Kategori Tarif",
            description = "R-1 (Rumah Tangga) – Tarif standar berlaku",
            time = "5 hari lalu",
            dotColor = Color(0xFF94A3B8) // grey
        ),
        NotificationItem(
            id = "notif_5",
            title = "FAQ / Bantuan",
            description = "Hubungi kami jika ada masalah pembacaan meter.",
            time = "1 minggu lalu",
            dotColor = Color(0xFFEF4444) // red
        )
    )


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .testTag("notification_screen_container")
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
                    .testTag("notification_header_banner")
            ) {
                Text(
                    text = "Notifikasi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.testTag("notification_header_title")
                )
            }

            // Notification List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(notificationList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = Color(0x10000000),
                                spotColor = Color(0x15000000)
                            )
                            .testTag(item.id),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Circular Indicator Dot
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(item.dotColor)
                                    .testTag("${item.id}_dot")
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.testTag("${item.id}_title")
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.description,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 18.sp,
                                    modifier = Modifier.testTag("${item.id}_desc")
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.time,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar (Tab 4 "Notifikasi" Active)
            SmartWaterBottomBar(
                selectedTab = SmartWaterTab.NOTIFIKASI,
                onNavigateToHome = onNavigateToHome,
                onNavigateToTagihan = onNavigateToBill,
                onNavigateToMeter = onNavigateToReport,
                onNavigateToNotification = {},
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

