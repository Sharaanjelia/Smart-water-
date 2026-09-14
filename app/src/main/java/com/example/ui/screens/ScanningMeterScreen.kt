package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.CameraPreviewView
import kotlinx.coroutines.delay

/**
 * Screen 10: Memindai Meter (Live Camera OCR Scanning)
 * - Live CameraX viewfinder preview with torch control
 * - Runtime Camera permission request & graceful fallback
 * - Glowing scanning viewfinder box with laser sweep animation
 * - "Menganalisis angka meter..." text
 * - 5 segmented digital boxes: [0] [1] [3] [5] [8] (bright orange-red)
 * - Linear progress bar + "Memproses gambar..."
 * - Guide card: "Panduan:" with 3 bullet items
 * - Auto-transitions or tap to complete scan
 */
@Composable
fun ScanningMeterScreen(
    onNavigateBack: () -> Unit,
    onScanComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Runtime Camera Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDeniedExplicitly by remember { mutableStateOf(false) }
    var useSimulationMode by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            permissionDeniedExplicitly = true
        }
    }

    // Automatically request camera permission on first entry
    LaunchedEffect(Unit) {
        if (!hasCameraPermission && !permissionDeniedExplicitly) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var progress by remember { mutableFloatStateOf(0.15f) }

    // Laser sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser_sweep")
    val laserYOffset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    // Progress simulation towards completion
    LaunchedEffect(hasCameraPermission, useSimulationMode) {
        if (hasCameraPermission || useSimulationMode) {
            while (progress < 1.0f) {
                delay(120)
                progress += 0.06f
            }
            delay(400)
            onScanComplete()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .statusBarsPadding()
            .testTag("scanning_meter_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("scanning_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Memindai Meter",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.testTag("scanning_title")
                    )
                }

                // Torch / Flash Toggle Button
                if (hasCameraPermission) {
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier.testTag("scanning_flash_toggle")
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashOn) Color(0xFFFFD54F) else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Scanning Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Viewfinder Box with Glowing Border & Camera/Laser
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF131D31))
                        .border(
                            width = 2.dp,
                            color = Color(0xFF00E5FF),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .testTag("scanning_viewfinder_box"),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasCameraPermission && !useSimulationMode) {
                        // Live Camera Feed
                        CameraPreviewView(
                            modifier = Modifier.fillMaxSize(),
                            isTorchEnabled = isFlashOn,
                            fallbackContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF131D31))
                                )
                            }
                        )

                        // Dark scrim for readability of digits & laser
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x550F172A))
                        )
                    }

                    // Sweeping horizontal laser light
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(3.dp)
                            .offset(y = laserYOffset.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0x0000E5FF),
                                        Color(0xFF00E5FF),
                                        Color(0xFF80D8FF),
                                        Color(0xFF00E5FF),
                                        Color(0x0000E5FF)
                                    )
                                )
                            )
                    )

                    // Overlay: Analysis label and Digit Boxes
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Menganalisis angka meter...",
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.testTag("menganalisis_text")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // 5 Segmented Digit Boxes [0] [1] [3] [5] [8]
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("0", "1", "3", "5", "8").forEachIndexed { index, digit ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xDD0F172A))
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF38BDF8),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .testTag("digit_box_$index"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFFF5252)
                                    )
                                }
                            }
                        }
                    }
                }

                // If Camera Permission is not yet granted, show permission banner
                if (!hasCameraPermission && !useSimulationMode) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("camera_permission_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x3300E5FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Izin Kamera Diperlukan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Aplikasi membutuhkan izin akses kamera agar dapat menampilkan pratinjau langsung dan memindai angka meteran air Anda.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { useSimulationMode = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("skip_camera_permission_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF94A3B8)
                                    )
                                ) {
                                    Text("Simulasi", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("grant_camera_permission_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0066CC)
                                    )
                                ) {
                                    Text("Beri Izin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00E5FF),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Memproses gambar...",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.testTag("memproses_gambar_text")
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom Guide Card (Panduan) - exactly matching reference mockup
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("panduan_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF182234)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D4A))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Panduan:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• Pastikan angka terlihat jelas",
                            fontSize = 12.5.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "• Hindari pantulan cahaya",
                            fontSize = 12.5.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "• Foto dari jarak 20-30 cm",
                            fontSize = 12.5.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
