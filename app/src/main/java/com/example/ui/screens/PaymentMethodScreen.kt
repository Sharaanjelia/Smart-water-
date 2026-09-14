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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen 15: Pilih Metode Pembayaran
 * - Top Bar: Back arrow, "Pilih Metode Pembayaran"
 * - Blue Header Card:
 *    - "Total Tagihan"
 *    - "Rp 150.000"
 *    - "Metode Pembayaran"
 * - List of payment methods:
 *    1. QRIS - "Scan QR Code" (default selected)
 *    2. Transfer Bank - "BCA, Mandiri, BNI, BRI"
 *    3. E-Wallet - "GoPay, OVO, DANA"
 *    4. Virtual Account - "Permata, BCA, Mandiri"
 * - Bottom Bar: "Lanjutkan" (Solid blue)
 */
@Composable
fun PaymentMethodScreen(
    onNavigateBack: () -> Unit,
    onProceedToSuccess: (paymentMethod: String) -> Unit,
    totalAmount: String = "Rp 150.000",
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf("QRIS") }

    val paymentOptions = listOf(
        PaymentOption(
            id = "QRIS",
            name = "QRIS",
            description = "Scan QR Code",
            icon = Icons.Default.QrCodeScanner,
            iconBg = Color(0xFFEFF6FF),
            iconColor = Color(0xFF2563EB)
        ),
        PaymentOption(
            id = "Transfer Bank",
            name = "Transfer Bank",
            description = "BCA, Mandiri, BNI, BRI",
            icon = Icons.Default.AccountBalance,
            iconBg = Color(0xFFF3E8FF),
            iconColor = Color(0xFF7C3AED)
        ),
        PaymentOption(
            id = "E-Wallet",
            name = "E-Wallet",
            description = "GoPay, OVO, DANA",
            icon = Icons.Default.AccountBalanceWallet,
            iconBg = Color(0xFFFFEDD5),
            iconColor = Color(0xFFEA580C)
        ),
        PaymentOption(
            id = "Virtual Account",
            name = "Virtual Account",
            description = "Permata, BCA, Mandiri",
            icon = Icons.Default.Computer,
            iconBg = Color(0xFFE0F2FE),
            iconColor = Color(0xFF0284C7)
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
                            text = "Total Tagihan",
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
                            text = "Metode Pembayaran",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Payment Method Items
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    paymentOptions.forEach { option ->
                        val isSelected = selectedMethod == option.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = if (isSelected) 4.dp else 2.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color(0x10000000),
                                    spotColor = Color(0x15000000)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) SmartWaterPrimary else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedMethod = option.id }
                                .testTag("payment_option_${option.id.lowercase().replace(" ", "_")}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFF0F7FF) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Option Icon Box
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
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

                                    Column {
                                        Text(
                                            text = option.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = option.description,
                                            fontSize = 12.sp,
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
                    Text(
                        text = "Lanjutkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
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
    val iconColor: Color
)
