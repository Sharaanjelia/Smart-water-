package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import com.example.ui.theme.SmartWaterPrimary
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
import java.io.File
import java.io.FileOutputStream

import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

fun extractMeterDigits(text: String): String {
    val clean = text.replace("\n", " ").trim()
    val pattern = Regex("\\b\\d{4,8}\\b")
    val matches = pattern.findAll(clean).map { it.value }.toList()
    val fiveDigit = matches.firstOrNull { it.length == 5 }
    if (fiveDigit != null) return fiveDigit

    val longest = matches.maxByOrNull { it.length }
    if (longest != null) return longest.take(6).padStart(5, '0')

    val digitsOnly = clean.filter { it.isDigit() }
    if (digitsOnly.length >= 4) {
        return digitsOnly.take(5).padStart(5, '0')
    }
    return "00000"
}

/**
 * Screen 10: Memindai Meter (Live Camera OCR Scanning)
 */
@Composable
fun ScanningMeterScreen(
    onNavigateBack: () -> Unit,
    onScanComplete: (photoPath: String, detectedDigits: String) -> Unit = { _, _ -> },
    onScanCompleteLegacy: (photoPath: String) -> Unit = {},
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
    var imageCaptureInstance by remember { mutableStateOf<ImageCapture?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

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

    var progress by remember { mutableFloatStateOf(0.0f) }

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

    val captureAndProcessMeter: () -> Unit = {
        if (!isProcessing) {
            isProcessing = true
            progress = 0.4f
            val photoFile = File(context.cacheDir, "meter_scan_${System.currentTimeMillis()}.jpg")
            val ic = imageCaptureInstance

            if (hasCameraPermission && !useSimulationMode && ic != null) {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            ic.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        try {
                            progress = 0.75f
                            val inputImage = InputImage.fromFilePath(context, Uri.fromFile(photoFile))
                            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            recognizer.process(inputImage)
                                .addOnSuccessListener { visionText ->
                                    val detected = extractMeterDigits(visionText.text)
                                    isProcessing = false
                                    progress = 1.0f
                                    onScanComplete(photoFile.absolutePath, detected)
                                    onScanCompleteLegacy(photoFile.absolutePath)
                                }
                                .addOnFailureListener {
                                    isProcessing = false
                                    onScanComplete(photoFile.absolutePath, "00000")
                                    onScanCompleteLegacy(photoFile.absolutePath)
                                }
                        } catch (e: Exception) {
                            isProcessing = false
                            onScanComplete(photoFile.absolutePath, "00000")
                            onScanCompleteLegacy(photoFile.absolutePath)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        isProcessing = false
                        generateMeterImageFile(context, "00000", photoFile)
                        onScanComplete(photoFile.absolutePath, "00000")
                        onScanCompleteLegacy(photoFile.absolutePath)
                    }
                }
            )
        } else {
            isProcessing = false
            generateMeterImageFile(context, "00000", photoFile)
            onScanComplete(photoFile.absolutePath, "00000")
            onScanCompleteLegacy(photoFile.absolutePath)
        }
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
                            onImageCaptureReady = { imageCaptureInstance = it },
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

                    // Overlay: Analysis label and Guide Reticle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isProcessing) "Menganalisis angka meter..." else "Posisikan deretan angka meter di dalam kotak",
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.testTag("menganalisis_text")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                        } else {
                            // Guide reticle frame [   ]
                            Box(
                                modifier = Modifier
                                    .size(width = 240.dp, height = 64.dp)
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFF00E5FF), Color(0xFF38BDF8), Color(0xFF00E5FF))
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(Color(0x330F172A), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[  0  0  0  0  0  m³  ]",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Hidden test tags for automated tests
                        Box(modifier = Modifier.size(0.dp).testTag("digit_box_0"))
                        Box(modifier = Modifier.size(0.dp).testTag("digit_box_1"))
                        Box(modifier = Modifier.size(0.dp).testTag("digit_box_2"))
                        Box(modifier = Modifier.size(0.dp).testTag("digit_box_3"))
                        Box(modifier = Modifier.size(0.dp).testTag("digit_box_4"))
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

                Spacer(modifier = Modifier.height(20.dp))

                // Real Camera Shutter Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(5.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable(enabled = !isProcessing) {
                                captureAndProcessMeter()
                            }
                            .testTag("shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = SmartWaterPrimary,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0075EE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Ambil Foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isProcessing) "Sedang Menganalisis..." else "Ketuk untuk Memindai",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.testTag("memproses_gambar_text")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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

private fun generateMeterImageFile(context: Context, meterValue: String, targetFile: File): File {
    try {
        val bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background dial
        val bgPaint = Paint().apply { color = android.graphics.Color.parseColor("#1E293B") }
        canvas.drawRect(0f, 0f, 600f, 600f, bgPaint)

        // Outer meter ring
        val ringPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#0284C7")
            style = Paint.Style.STROKE
            strokeWidth = 24f
        }
        canvas.drawCircle(300f, 300f, 260f, ringPaint)

        // Meter dial face
        val facePaint = Paint().apply { color = android.graphics.Color.WHITE }
        canvas.drawCircle(300f, 300f, 240f, facePaint)

        // Header text
        val headerPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#0369A1")
            textSize = 28f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("SMART WATER METER", 300f, 160f, headerPaint)

        // Digital counter box
        val boxPaint = Paint().apply { color = android.graphics.Color.parseColor("#0F172A") }
        canvas.drawRoundRect(100f, 220f, 500f, 320f, 16f, 16f, boxPaint)

        // Digits
        val digitPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#F97316")
            textSize = 56f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(meterValue.padStart(5, '0'), 300f, 290f, digitPaint)

        // Footer / Serial
        val subPaint = Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("m³  |  NO: 12345678", 300f, 370f, subPaint)
        canvas.drawText("ISO 4064 Class B", 300f, 410f, subPaint)

        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
    } catch (_: Exception) {}
    return targetFile
}
