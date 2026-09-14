package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.SmartWaterBottomBar
import com.example.ui.components.SmartWaterTab
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen 20: Pengaturan
 * Matches the uploaded design:
 * - Header: Bold "Pengaturan"
 * - Group 1:
 *     1. Pengaturan Notifikasi (Yellow container) - "Kelola notifikasi tagihan"
 *     2. Privasi & Keamanan (Peach/Orange container) - "Ubah PIN dan keamanan"
 * - Section "INFO METER":
 *     3. No. Meter Air (Cyan/Blue container) - "12345678"
 *     4. Kategori Tarif (Pink container) - "R-1 (Rumah Tangga)"
 * - Section "BANTUAN":
 *     5. FAQ / Bantuan (Mint green container) - "Pertanyaan yang sering ditanyakan"
 *     6. Tentang Aplikasi (Lavender container) - "Versi 1.0.0"
 * - Sleek scroll indicator on the right
 * - Bottom Navigation Bar with 5 tabs (Profil selected)
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBill: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Dialog state
    var showNotifSettingsDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showMeterInfoDialog by remember { mutableStateOf(false) }
    var showTariffInfoDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Notif preferences
    var notifTagihan by remember { mutableStateOf(true) }
    var notifJatuhTempo by remember { mutableStateOf(true) }
    var notifPemeliharaan by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top Header: "Pengaturan"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Profil",
                        tint = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Pengaturan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            // Main Content Area with Scroll indicator
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    // Group 1: General Settings
                    item {
                        Spacer(modifier = Modifier.height(4.dp))

                        // 1. Pengaturan Notifikasi
                        SettingsCardItem(
                            containerColor = Color(0xFFFEF3C7), // Light amber/yellow
                            icon = Icons.Default.Notifications,
                            iconColor = Color(0xFFF59E0B),
                            title = "Pengaturan Notifikasi",
                            subtitle = "Kelola notifikasi tagihan",
                            onClick = { showNotifSettingsDialog = true },
                            testTag = "settings_item_notif"
                        )
                        HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                        // 2. Privasi & Keamanan
                        SettingsCardItem(
                            containerColor = Color(0xFFFFEDD5), // Light peach/orange
                            icon = Icons.Default.Lock,
                            iconColor = Color(0xFFEA580C),
                            title = "Privasi & Keamanan",
                            subtitle = "Ubah PIN dan keamanan",
                            onClick = { showSecurityDialog = true },
                            testTag = "settings_item_security"
                        )
                        HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Group 2: INFO METER
                    item {
                        Text(
                            text = "INFO METER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // 3. No. Meter Air
                        SettingsCardItem(
                            containerColor = Color(0xFFE0F2FE), // Light sky blue
                            icon = Icons.Default.WaterDrop,
                            iconColor = Color(0xFF0284C7),
                            title = "No. Meter Air",
                            subtitle = "12345678",
                            onClick = { showMeterInfoDialog = true },
                            testTag = "settings_item_meter"
                        )
                        HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                        // 4. Kategori Tarif
                        SettingsCardItem(
                            containerColor = Color(0xFFFCE7F3), // Light pink
                            icon = Icons.Default.LocalOffer,
                            iconColor = Color(0xFFEC4899),
                            title = "Kategori Tarif",
                            subtitle = "R-1 (Rumah Tangga)",
                            onClick = { showTariffInfoDialog = true },
                            testTag = "settings_item_tariff"
                        )
                        HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Group 3: BANTUAN
                    item {
                        Text(
                            text = "BANTUAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // 5. FAQ / Bantuan
                        SettingsCardItem(
                            containerColor = Color(0xFFDCFCE7), // Light mint green
                            icon = Icons.Default.HelpOutline,
                            iconColor = Color(0xFFEF4444), // Coral red mark
                            title = "FAQ / Bantuan",
                            subtitle = "Pertanyaan yang sering ditanyakan",
                            onClick = { showFaqDialog = true },
                            testTag = "settings_item_faq"
                        )
                        HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)

                        // 6. Tentang Aplikasi
                        SettingsCardItem(
                            containerColor = Color(0xFFEDE9FE), // Light lavender
                            icon = Icons.Default.Smartphone,
                            iconColor = Color(0xFF6366F1),
                            title = "Tentang Aplikasi",
                            subtitle = "Versi 1.0.0",
                            onClick = { showAboutDialog = true },
                            testTag = "settings_item_about"
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Sleek Vertical Scroll Indicator matching mockup
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, top = 20.dp)
                        .width(6.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF94A3B8).copy(alpha = 0.6f))
                )
            }

            // Bottom Navigation Bar (Tab 5 "Profil" Selected)
            SmartWaterBottomBar(
                selectedTab = SmartWaterTab.PROFIL,
                onNavigateToHome = onNavigateToHome,
                onNavigateToTagihan = onNavigateToBill,
                onNavigateToMeter = onNavigateToReport,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToProfile = onNavigateBack
            )
        }

        // 1. Notif Settings Dialog
        if (showNotifSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showNotifSettingsDialog = false },
                title = { Text("Pengaturan Notifikasi", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Tagihan Baru", fontWeight = FontWeight.Medium)
                                Text("Pemberitahuan terbit e-bill bulanan", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            Switch(
                                checked = notifTagihan,
                                onCheckedChange = { notifTagihan = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SmartWaterPrimary)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pengingat Jatuh Tempo", fontWeight = FontWeight.Medium)
                                Text("Peringatan H-3 batas waktu pembayaran", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            Switch(
                                checked = notifJatuhTempo,
                                onCheckedChange = { notifJatuhTempo = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SmartWaterPrimary)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Info Gangguan & Pemeliharaan", fontWeight = FontWeight.Medium)
                                Text("Kabar maintenance pipa jaringan PDAM", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                            Switch(
                                checked = notifPemeliharaan,
                                onCheckedChange = { notifPemeliharaan = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SmartWaterPrimary)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showNotifSettingsDialog = false
                            Toast.makeText(context, "Preferensi notifikasi disimpan", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Simpan")
                    }
                }
            )
        }

        // 2. Security Dialog
        if (showSecurityDialog) {
            var pinInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showSecurityDialog = false },
                title = { Text("Privasi & Keamanan", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Perbarui PIN Transaksi (6 Digit) Anda untuk mengamankan pembayaran & baca meter mandiri:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { if (it.length <= 6) pinInput = it },
                            label = { Text("PIN Baru") },
                            placeholder = { Text("Contoh: 123456") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSecurityDialog = false
                            Toast.makeText(context, "PIN keamanan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSecurityDialog = false }) { Text("Batal") }
                }
            )
        }

        // 3. Meter Info Dialog
        if (showMeterInfoDialog) {
            AlertDialog(
                onDismissRequest = { showMeterInfoDialog = false },
                title = { Text("Detail Meter Air", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Nomor Meter: 12345678", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tipe Meteran: Ultrasonic Smart Flow Meter", fontSize = 13.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tanggal Pemasangan: 12 Januari 2023", fontSize = 13.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Kalibrasi Terakhir: 15 Desember 2024", fontSize = 13.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Status: Terhubung & Normal Aktif", fontSize = 13.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showMeterInfoDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }

        // 4. Tariff Info Dialog
        if (showTariffInfoDialog) {
            AlertDialog(
                onDismissRequest = { showTariffInfoDialog = false },
                title = { Text("Kategori Tarif R-1", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Golongan: Rumah Tangga 1 (R-1)", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Blok I (0 - 10 m³): Rp 3.500 / m³", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Blok II (11 - 20 m³): Rp 4.500 / m³", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Blok III (> 20 m³): Rp 6.000 / m³", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Biaya Pemeliharaan Meter: Rp 5.000 / bln", fontSize = 13.sp)
                        Text("• Biaya Administrasi: Rp 2.500 / bln", fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showTariffInfoDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }

        // 5. FAQ Dialog
        if (showFaqDialog) {
            AlertDialog(
                onDismissRequest = { showFaqDialog = false },
                title = { Text("FAQ / Bantuan", fontWeight = FontWeight.Bold) },
                text = {
                    LazyColumn(modifier = Modifier.height(280.dp)) {
                        item {
                            Text("1. Kapan batas lapor meter mandiri?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Setiap tanggal 20 hingga 25 setiap bulannya melalui fitur 'Laporkan'.", fontSize = 12.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("2. Bagaimana jika foto meter buram?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Pastikan kaca meteran bersih dari debu atau air, dan hidupkan lampu kilat (flash) saat memotret.", fontSize = 12.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("3. Saluran Layanan Pengaduan 24 Jam", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Call Center: 1500-123\nWhatsApp Pengaduan: 0811-9988-7766\nEmail: cs@smartwater.id", fontSize = 12.sp, color = Color(0xFF0284C7))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showFaqDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }

        // 6. About App Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("Tentang Remix Smart Water", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Remix Smart Water", fontWeight = FontWeight.Bold, color = SmartWaterPrimary)
                        Text("Versi: 1.0.0 (Build 2026.03)")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Aplikasi Pembacaan Meter Air Mandiri dan Pembayaran Tagihan Terintegrasi untuk efisiensi dan transparansi pelanggan PDAM.",
                            fontSize = 13.sp,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("© 2026 Perumda Air Minum Mandiri. Hak Cipta Dilindungi.", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showAboutDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}

/**
 * Settings Card Item with rounded soft colored box container
 */
@Composable
private fun SettingsCardItem(
    containerColor: Color,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Soft rounded container with colored icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // End Chevron
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Detail",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}

