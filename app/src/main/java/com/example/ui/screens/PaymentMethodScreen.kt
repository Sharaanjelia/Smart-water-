package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen 15: Pilih Metode Pembayaran
 * Integrasi resmi Xendit Payment Gateway (Checkout / Invoices)
 */
@Composable
fun PaymentMethodScreen(
    onNavigateBack: () -> Unit,
    onProceedToSuccess: (paymentMethod: String) -> Unit,
    totalAmount: String = "Rp 150.800",
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf("Xendit Checkout") }

    val paymentOptions = listOf(
        PaymentOption(
            id = "Xendit Checkout",
            name = "Xendit Checkout",
            description = "QRIS, Semua Bank (BCA, Mandiri, BRI, BNI), OVO, DANA",
            icon = Icons.Default.Payment,
            iconBg = Color(0xFFEFF6FF),
            iconColor = Color(0xFF2563EB),
            badge = "Rekomendasi • Instan"
        ),
        PaymentOption(
            id = "QRIS",
            name = "QRIS Xendit",
            description = "Scan via GoPay, OVO, DANA, BCA, Mandiri",
            icon = Icons.Default.QrCodeScanner,
            iconBg = Color(0xFFEFF6FF),
            iconColor = Color(0xFF2563EB)
        ),
        PaymentOption(
            id = "Virtual Account",
            name = "Virtual Account Bank",
            description = "BCA, Mandiri, BRI, BNI, Permata (Xendit VA)",
            icon = Icons.Default.Computer,
            iconBg = Color(0xFFE0F2FE),
            iconColor = Color(0xFF0284C7)
        ),
        PaymentOption(
            id = "E-Wallet",
            name = "E-Wallet (OVO / DANA / ShopeePay)",
            description = "Pembayaran instan langsung dari dompet digital",
            icon = Icons.Default.AccountBalanceWallet,
            iconBg = Color(0xFFFFEDD5),
            iconColor = Color(0xFFEA580C)
        ),
        PaymentOption(
            id = "Transfer Bank",
            name = "Transfer Bank Manual",
            description = "Konfirmasi struk transfer manual ke PDAM",
            icon = Icons.Default.AccountBalance,
            iconBg = Color(0xFFF3E8FF),
            iconColor = Color(0xFF7C3AED)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("payment_method_screen_container")
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
                    modifier = Modifier.testTag("payment_method_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = "Pilih Metode Pembayaran",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.testTag("payment_method_title")
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                // Blue Card: Total Tagihan
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = Color(0x33005BBB),
                            spotColor = Color(0x550066EE)
                        )
                        .testTag("total_tagihan_blue_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SmartWaterPrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Total Tagihan Air",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = totalAmount,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp,
                            modifier = Modifier.testTag("blue_card_amount")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Metode Pembayaran Resmi Xendit Gateway",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Payment Options List
                paymentOptions.forEach { option ->
                    val isSelected = selectedMethod == option.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .shadow(
                                elevation = if (isSelected) 4.dp else 1.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = if (isSelected) Color(0x22005BBB) else Color(0x0A000000)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) SmartWaterPrimary else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedMethod = option.id }
                            .testTag("payment_option_${option.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFF0F7FF) else Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Icon Box
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(option.iconBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = option.icon,
                                            contentDescription = option.name,
                                            tint = option.iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = option.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF0F172A)
                                            )
                                            if (option.badge != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFFDBEAFE))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = option.badge,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1D4ED8)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = option.description,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                // Radio Button
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethod = option.id },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = SmartWaterPrimary,
                                        unselectedColor = Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security & Official Gateway Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Keamanan",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Didukung resmi oleh Xendit Payment Gateway. Enkripsi 256-bit dan berlisensi Bank Indonesia.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = Color(0xFF475569)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Button: Lanjutkan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = { onProceedToSuccess(selectedMethod) },
                    enabled = !isLoading,
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
                        .testTag("payment_lanjutkan_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Membuat Invoice Xendit...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = if (selectedMethod == "Xendit Checkout") "Lanjut ke Pembayaran Xendit" else "Lanjutkan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private data class PaymentOption(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconColor: Color,
    val badge: String? = null
)
