package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ReportMeterTipsCard
import com.example.ui.components.SmartWaterBottomBar
import com.example.ui.components.SmartWaterTab
import com.example.ui.components.WaterMeterCameraViewfinder
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen: Laporkan Meter matching user provided screenshot:
 * - Top Bar: Back arrow, "Laporkan Meter" (bold), "Ambil foto meter air Anda dengan jelas",
 *   elevated circular white flash button with blue lightning icon.
 * - Viewfinder Card: Realistic photo illustration of water meter on wall with pipes,
 *   white corner crop brackets, bottom dark pill with focus camera icon & guide text.
 * - Meter Air Utama Card:
 *   - Water droplet badge, "Meter Air Utama", "No. Meter 12345678", "● Aktif" green badge.
 *   - Left sub-box: "Meter Saat Ini ⓘ", 5 dark navy odometer tiles [0] [1] [0] [0] [0] + m³.
 *   - Right column: "Nama: Budi Santoso", "Terakhir Dibaca: 01 April 2025".
 * - Tips Foto Meter Card:
 *   - Lightbulb badge, 3 items with green circular checkmarks.
 *   - Botanical leaves & glowing cyan water drop decorative art.
 * - Bottom Circular Camera Shutter Button.
 */
@Composable
fun ReportMeterScreen(
    onNavigateBack: () -> Unit,
    onStartScan: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTagihan: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    currentMeterValue: String = "01000",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEDF6FD))
            .statusBarsPadding()
            .testTag("report_meter_screen_container")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Back button + Title & Subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("report_meter_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF0F2942),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Laporkan Meter",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2942),
                            modifier = Modifier.testTag("report_meter_title")
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ambil foto meter air Anda dengan jelas",
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Right: Elevated White Circular Flash Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape, ambientColor = Color(0x180066CC), spotColor = Color(0x280066CC))
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            isFlashOn = !isFlashOn
                            Toast.makeText(
                                context,
                                if (isFlashOn) "Lampu kilat aktif" else "Lampu kilat nonaktif",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .testTag("flash_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color(0xFFFFB300) else Color(0xFF0077EE),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 2. Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Viewfinder Card
                WaterMeterCameraViewfinder(
                    onTapToFocus = onStartScan,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Meter Air Utama
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = Color(0x10000000),
                            spotColor = Color(0x18000000)
                        )
                        .testTag("meter_info_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        // Header Row: [Droplet] Meter Air Utama / No. Meter | [● Aktif]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Meter Air Utama",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "No. Meter 12345678",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            // "● Aktif" Pill Badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF16A34A))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Aktif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Section: Left (Meter Saat Ini & Odometer) | Right (Nama & Terakhir Dibaca)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Sub-Box: Meter Saat Ini
                            Box(
                                modifier = Modifier
                                    .weight(1.15f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF0F9FF))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Meter Saat Ini",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF475569)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Info",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Odometer Digit Blocks: [0] [1] [0] [0] [0] m³
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val digits = currentMeterValue.padStart(5, '0')
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                                            modifier = Modifier.testTag("meter_number_digits")
                                        ) {
                                            digits.forEach { digit ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 19.dp, height = 26.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFF0F2942)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = digit.toString(),
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            text = "m³",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F2942)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Right Column: Nama & Terakhir Dibaca
                            Column(
                                modifier = Modifier.weight(0.95f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Nama Row
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Nama",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                        Text(
                                            text = "Budi Santoso",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }

                                // Terakhir Dibaca Row
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFE0F2FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Terakhir Dibaca",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                        Text(
                                            text = "01 April 2025",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 3: Tips Foto Meter
                ReportMeterTipsCard(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Shutter Camera Action Button matching bottom of screenshot
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Color(0xFF90CAF9), CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0077EE),
                                    Color(0xFF0284C7)
                                )
                            )
                        )
                        .clickable { onStartScan() }
                        .testTag("laporkan_sekarang_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Ambil Foto Meter",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom Navigation Bar (Tab 3 "Meter" Active)
            SmartWaterBottomBar(
                selectedTab = SmartWaterTab.METER,
                onNavigateToHome = onNavigateToHome,
                onNavigateToTagihan = onNavigateToTagihan,
                onNavigateToMeter = {},
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

