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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

/**
 * Login Screen precisely matching the user screenshot (05 Login):
 * - Waving hand icon / emoji
 * - "Selamat Datang 👏"
 * - "Masuk untuk melanjutkan"
 * - ID Pelanggan / No. HP
 * - PIN with "Lupa PIN?" link
 * - "Ingat saya" checkbox
 * - Primary "Masuk" button
 * - "atau" divider
 * - "Masuk sebagai Demo" outline button
 * - "Belum punya akun? Daftar" footer
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onDemoLogin: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customerId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(authState) {
        if (authState is AuthUiState.Error) {
            Toast.makeText(context, (authState as AuthUiState.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.clearState()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("login_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Waving hand icon
            Text(
                text = "👋",
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("login_wave_icon")
            )

            // Heading: Selamat Datang 👏
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Selamat Datang",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.testTag("login_title")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "👏",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: Masuk untuk melanjutkan
            Text(
                text = "Masuk untuk melanjutkan",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                modifier = Modifier.testTag("login_subtitle")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Field 1: ID Pelanggan / No. HP
            SmartWaterInputField(
                value = customerId,
                onValueChange = { customerId = it },
                label = "ID Pelanggan / No. HP",
                placeholder = "Contoh: 00123456789",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.testTag("login_input_customer_id")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 2: PIN + Lupa PIN?
            SmartWaterInputField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it },
                label = "PIN",
                placeholder = "Masukkan PIN 6 digit",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                headerTrailingContent = {
                    Text(
                        text = "Lupa PIN?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SmartWaterPrimary,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onNavigateToForgotPassword()
                            }
                            .testTag("forgot_pin_link")
                    )
                },
                modifier = Modifier.testTag("login_input_pin")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox: Ingat saya
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        rememberMe = !rememberMe
                    }
                    .testTag("remember_me_row")
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SmartWaterPrimary,
                        uncheckedColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ingat saya",
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Button: Masuk
            Button(
                onClick = {
                    if (customerId.isNotBlank() && pin.length >= 4) {
                        authViewModel.login(customerId, pin) {
                            onLoginSuccess()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Masukkan ID Pelanggan dan PIN 6 digit",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = authState !is AuthUiState.Loading,
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
                    .testTag("login_button")
            ) {
                if (authState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider: atau
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color(0xFFE2E8F0)
                )
                Text(
                    text = "atau",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button: Masuk sebagai Demo
            OutlinedButton(
                onClick = {
                    authViewModel.loginAsDemo {
                        onLoginSuccess()
                    }
                },
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SmartWaterPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = SmartWaterPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("demo_login_button")
            ) {
                Text(
                    text = "Masuk sebagai Demo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmartWaterPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button: Masuk sebagai Petugas / Admin PDAM
            OutlinedButton(
                onClick = onNavigateToAdmin,
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F172A)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF8FAFC),
                    contentColor = Color(0xFF0F172A)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_login_button")
            ) {
                Text(
                    text = "🛡️ Mode Petugas / Admin PDAM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer: Belum punya akun? Daftar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum punya akun? ",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Daftar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmartWaterPrimary,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigateToRegister()
                        }
                        .testTag("register_link")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
