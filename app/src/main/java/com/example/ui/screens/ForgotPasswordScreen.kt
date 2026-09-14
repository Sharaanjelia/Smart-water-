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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SmartWaterInputField
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen matching 07 Forgot Password:
 * - "Lupa PIN?" title
 * - "Ikuti langkah berikut untuk mengatur ulang PIN Anda." subtitle
 * - 4 Steps with blue circular badges (1, 2, 3, 4)
 * - "No. HP" input field with placeholder "Contoh: 08123456789"
 * - "Lanjut Verifikasi" button
 * - "Ulang Survey" / "Kembali ke Login" footer link
 */
@Composable
fun ForgotPasswordScreen(
    onNavigateBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var currentStep by remember { mutableIntStateOf(1) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var newPin by remember { mutableStateOf("") }

    val steps = listOf(
        Pair("Verifikasi Identitas", "Masukkan ID Pelanggan atau No. HP Anda"),
        Pair("Kode OTP", "Masukkan Kode OTP yang dikirim ke nomor Anda"),
        Pair("Buat PIN Baru", "Buat PIN baru yang mudah Anda ingat"),
        Pair("Selesai", "PIN berhasil direset")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("forgot_password_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Title
            Text(
                text = "Lupa PIN?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp,
                modifier = Modifier.testTag("forgot_password_title")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "Ikuti langkah berikut untuk mengatur ulang PIN Anda.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                modifier = Modifier.testTag("forgot_password_subtitle")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 4 Step Items
            steps.forEachIndexed { index, (title, description) ->
                val stepNumber = index + 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SmartWaterPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNumber",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input Field: No. HP
            SmartWaterInputField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "No. HP",
                placeholder = "Contoh: 08123456789",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.testTag("forgot_password_input_phone")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Action Button: Lanjut Verifikasi
            Button(
                onClick = {
                    if (phoneNumber.length >= 8) {
                        showSuccessDialog = true
                    } else {
                        Toast.makeText(
                            context,
                            "Masukkan nomor HP yang terdaftar untuk verifikasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SmartWaterPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = Color(0x33005BBB),
                        spotColor = Color(0x550066EE)
                    )
                    .testTag("forgot_password_submit_button")
            ) {
                Text(
                    text = "Lanjut Verifikasi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Link: Ulang Survey / Kembali
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ulang Survey",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigateBackToLogin()
                        }
                        .testTag("ulang_survey_link")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Verification Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Text(text = "Verifikasi Kode OTP", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "Kode OTP telah dikirimkan ke $phoneNumber via SMS/WhatsApp. Masukkan PIN 6 digit baru Anda:",
                            fontSize = 14.sp,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SmartWaterInputField(
                            value = newPin,
                            onValueChange = { if (it.length <= 6) newPin = it },
                            placeholder = "PIN 6 digit baru",
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            Toast.makeText(
                                context,
                                "PIN Anda berhasil diperbarui! Silakan masuk.",
                                Toast.LENGTH_LONG
                            ).show()
                            onNavigateBackToLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartWaterPrimary)
                    ) {
                        Text("Simpan PIN")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}
