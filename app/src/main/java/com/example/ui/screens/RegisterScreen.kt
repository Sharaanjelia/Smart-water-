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
import androidx.compose.ui.text.input.KeyboardCapitalization
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
 * Register Screen precisely matching the user screenshot (06 Register):
 * - "Buat Akun Baru" heading
 * - "Daftar untuk mulai menggunakan Smart Water" subtitle
 * - Nama Lengkap
 * - No. Meter
 * - No. HP
 * - Alamat
 * - Buat PIN (6 digit)
 * - "Daftar" button
 * - "Sudah punya akun? Masuk" footer link
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var meterNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
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
            .testTag("register_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Heading: Buat Akun Baru
            Text(
                text = "Buat Akun Baru",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                letterSpacing = (-0.5).sp,
                modifier = Modifier.testTag("register_title")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: Daftar untuk mulai menggunakan Smart Water
            Text(
                text = "Daftar untuk mulai menggunakan Smart Water",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                modifier = Modifier.testTag("register_subtitle")
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Field 1: Nama Lengkap
            SmartWaterInputField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.testTag("register_input_name")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 2: No. Meter
            SmartWaterInputField(
                value = meterNumber,
                onValueChange = { if (it.length <= 11 && it.all { c -> c.isDigit() }) meterNumber = it },
                label = "No. Meter (11 digit)",
                placeholder = "Contoh: 00123456789",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.testTag("register_input_meter")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 3: No. HP
            SmartWaterInputField(
                value = phoneNumber,
                onValueChange = { if (it.length <= 15 && it.all { c -> c.isDigit() }) phoneNumber = it },
                label = "No. HP (min. 12 digit)",
                placeholder = "Contoh: 081234567890",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.testTag("register_input_phone")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 4: Alamat
            SmartWaterInputField(
                value = address,
                onValueChange = { address = it },
                label = "Alamat",
                placeholder = "Masukkan alamat lengkap",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.testTag("register_input_address")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 5: Buat PIN (6 digit)
            SmartWaterInputField(
                value = pin,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pin = it },
                label = "Buat PIN (6 digit)",
                placeholder = "• Masukkan 6 digit PIN",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.testTag("register_input_pin")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Button: Daftar
            Button(
                onClick = {
                    val cleanMeter = meterNumber.filter { it.isDigit() }
                    val cleanPhone = phoneNumber.filter { it.isDigit() }
                    val cleanPin = pin.filter { it.isDigit() }

                    when {
                        fullName.isBlank() -> {
                            Toast.makeText(context, "Silakan isi nama lengkap Anda", Toast.LENGTH_SHORT).show()
                        }
                        cleanMeter.length != 11 -> {
                            Toast.makeText(context, "Nomor meteran harus 11 digit angka", Toast.LENGTH_SHORT).show()
                        }
                        cleanPhone.length < 12 -> {
                            Toast.makeText(context, "Nomor HP minimal 12 digit angka", Toast.LENGTH_SHORT).show()
                        }
                        address.isBlank() -> {
                            Toast.makeText(context, "Silakan isi alamat lengkap Anda", Toast.LENGTH_SHORT).show()
                        }
                        cleanPin.length != 6 -> {
                            Toast.makeText(context, "PIN harus tepat 6 digit angka", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            authViewModel.register(
                                fullName = fullName.trim(),
                                meterNumber = cleanMeter,
                                phoneNumber = cleanPhone,
                                address = address.trim(),
                                pin = cleanPin
                            ) {
                                Toast.makeText(
                                    context,
                                    "Pendaftaran berhasil! Selamat datang di Smart Water.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onRegisterSuccess()
                            }
                        }
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
                    .testTag("register_submit_button")
            ) {
                if (authState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Daftar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer: Sudah punya akun? Masuk
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Masuk",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmartWaterPrimary,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigateToLogin()
                        }
                        .testTag("login_link")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
