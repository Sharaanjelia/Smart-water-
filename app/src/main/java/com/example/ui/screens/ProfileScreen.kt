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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.SmartWaterBottomBar
import com.example.ui.components.SmartWaterTab
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen 19: Profil Saya
 * Matches design in user mockup:
 * - Blue Header with Customer Avatar, Name ("Budi Santoso"), and Customer ID ("ID Pelanggan: 12345678")
 * - Section "INFORMASI PELANGGAN":
 *     1. Nomor telepon: 0812-3456-7890
 *     2. No. Meter Air: 12345678
 *     3. Kategori Tarif: R-1 (Rumah Tangga)
 *     4. Alamat: Jl. Mewar No. 10, Sumbar
 *     5. Email: budi@email.com
 * - Section "AKUN":
 *     1. Pengaturan (navigates to Screen 20 Pengaturan)
 *     2. Ubah PIN / Keamanan
 *     3. Keluar Akun
 * - Bottom Navigation Bar with 5 tabs (Profil selected)
 */
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToBill: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isCustomerInfoExpanded by remember { mutableStateOf(true) }
    var isAccountExpanded by remember { mutableStateOf(true) }

    val authRepo = remember { com.example.data.repository.AuthRepository.getInstance() }
    val currentUser by authRepo.currentUser.collectAsStateWithLifecycle()
    val name = currentUser?.fullName?.ifEmpty { "Pelanggan Smart Water" } ?: "Pelanggan Smart Water"
    val custId = currentUser?.customerId?.ifEmpty { "-" } ?: "-"
    val phone = currentUser?.phoneNumber?.ifEmpty { "-" } ?: "-"
    val meterNo = currentUser?.meterNumber?.ifEmpty { "-" } ?: "-"
    val address = currentUser?.address?.ifEmpty { "-" } ?: "-"
    val email = currentUser?.email?.ifEmpty { if (phone != "-") "$phone@smartwater.local" else "-" } ?: if (phone != "-") "$phone@smartwater.local" else "-"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Main Scrollable Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header: Deep Blue Area
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF0066CC),
                                            Color(0xFF0052B4)
                                        )
                                    )
                                )
                                .statusBarsPadding()
                                .padding(top = 16.dp, bottom = 28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Outer translucent circle around avatar
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.22f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Inner circle avatar
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E3A8A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Avatar Pengguna",
                                            tint = Color.White,
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Customer Name
                                Text(
                                    text = name,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Customer ID
                                Text(
                                    text = "ID Pelanggan: $custId",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Section: INFORMASI PELANGGAN
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isCustomerInfoExpanded = !isCustomerInfoExpanded }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "INFORMASI PELANGGAN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = Color(0xFF64748B)
                                )
                                Icon(
                                    imageVector = if (isCustomerInfoExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Toggle Informasi Pelanggan",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (isCustomerInfoExpanded) {
                                // 1. Nomor Telepon
                                ProfileInfoItem(
                                    icon = Icons.Default.Phone,
                                    iconColor = Color(0xFFEC4899), // Pink
                                    title = "Nomor telepon",
                                    subtitle = phone,
                                    onClick = {
                                        showDetailDialog = "Nomor Telepon" to "$phone\nTerhubung ke WhatsApp & SMS Pengingat"
                                    },
                                    testTag = "profile_item_phone"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // 2. No. Meter Air
                                ProfileInfoItem(
                                    icon = Icons.Default.HomeWork,
                                    iconColor = Color(0xFFF97316), // Orange
                                    title = "No. Meter Air",
                                    subtitle = meterNo,
                                    onClick = {
                                        showDetailDialog = "No. Meter Air" to "$meterNo\nAlat Ukur: Ultrasonic Digital M-20\nStatus: Normal Aktif"
                                    },
                                    testTag = "profile_item_meter"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // 3. Kategori Tarif
                                ProfileInfoItem(
                                    icon = Icons.Default.LocalOffer,
                                    iconColor = Color(0xFFF59E0B), // Amber/Yellow
                                    title = "Kategori Tarif",
                                    subtitle = "R-1 (Rumah Tangga)",
                                    onClick = {
                                        showDetailDialog = "Kategori Tarif" to "R-1 (Rumah Tangga)\nTarif Dasar: Rp 3.500 / m³ (0 - 10 m³)\nTarif Progresif: Rp 4.500 / m³ (> 10 m³)"
                                    },
                                    testTag = "profile_item_tariff"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // 4. Alamat
                                ProfileInfoItem(
                                    icon = Icons.Default.LocationOn,
                                    iconColor = Color(0xFFDB2777), // Red/Pink
                                    title = "Alamat",
                                    subtitle = address,
                                    onClick = {
                                        showDetailDialog = "Alamat Pelanggan" to "$address\nZona Distribusi: Wilayah Barat A-3"
                                    },
                                    testTag = "profile_item_address"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // 5. Email
                                ProfileInfoItem(
                                    icon = Icons.Default.Email,
                                    iconColor = Color(0xFF8B5CF6), // Purple
                                    title = "Email",
                                    subtitle = email,
                                    onClick = {
                                        showDetailDialog = "Alamat Email" to "$email\nStatus: Terverifikasi untuk akun Smart Water"
                                    },
                                    testTag = "profile_item_email"
                                )
                            }
                        }
                    }

                    // Section: AKUN
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 16.dp, bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isAccountExpanded = !isAccountExpanded }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AKUN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = Color(0xFF64748B)
                                )
                                Icon(
                                    imageVector = if (isAccountExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Toggle Akun",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (isAccountExpanded) {
                                // Pengaturan Item
                                ProfileInfoItem(
                                    icon = Icons.Default.Settings,
                                    iconColor = Color(0xFF0284C7), // Sky Blue
                                    title = "Pengaturan",
                                    subtitle = "Notifikasi, keamanan, dan bantuan",
                                    onClick = onNavigateToSettings,
                                    testTag = "profile_item_settings"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // Wallet Operator Item
                                ProfileInfoItem(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    iconColor = Color(0xFF10B981),
                                    title = "Wallet Operator",
                                    subtitle = "Saldo, QR, dan penarikan ke bank",
                                    onClick = onNavigateToWallet,
                                    testTag = "profile_item_wallet"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // Ubah PIN / Keamanan Item
                                ProfileInfoItem(
                                    icon = Icons.Default.Lock,
                                    iconColor = Color(0xFFF97316), // Orange
                                    title = "Privasi & Keamanan",
                                    subtitle = "Ubah PIN dan keamanan akun",
                                    onClick = { showPinDialog = true },
                                    testTag = "profile_item_security"
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                                // Keluar Akun Item
                                ProfileInfoItem(
                                    icon = Icons.AutoMirrored.Filled.Logout,
                                    iconColor = Color(0xFFEF4444), // Red
                                    title = "Keluar Akun",
                                    subtitle = "Keluar dari aplikasi",
                                    onClick = { showLogoutDialog = true },
                                    testTag = "profile_item_logout"
                                )
                            }
                        }
                    }
                }

                // Custom Sleek Vertical Scroll Indicator matching mockup
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, top = 160.dp)
                        .width(6.dp)
                        .height(130.dp)
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
                onNavigateToProfile = {}
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Konfirmasi Keluar", fontWeight = FontWeight.Bold) },
                text = { Text("Apakah Anda yakin ingin keluar dari akun Budi Santoso?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Keluar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Change PIN Dialog
        if (showPinDialog) {
            var pinText by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showPinDialog = false },
                title = { Text("Ubah PIN Keamanan", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Masukkan 6 digit PIN baru untuk otorisasi pembayaran dan akun:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = pinText,
                            onValueChange = { if (it.length <= 6) pinText = it },
                            label = { Text("PIN Baru") },
                            placeholder = { Text("6 digit angka") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPinDialog = false
                            Toast.makeText(context, "PIN berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Detail Info Dialog
        showDetailDialog?.let { (title, desc) ->
            AlertDialog(
                onDismissRequest = { showDetailDialog = null },
                title = { Text(title, fontWeight = FontWeight.Bold) },
                text = { Text(desc) },
                confirmButton = {
                    Button(
                        onClick = { showDetailDialog = null },
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
 * List Item Row for Customer Information in Profile
 */
@Composable
private fun ProfileInfoItem(
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
        // Colored Icon
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
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
            contentDescription = "Buka",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}

