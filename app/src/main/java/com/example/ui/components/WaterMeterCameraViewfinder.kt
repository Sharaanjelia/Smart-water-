package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Realistic Camera Viewfinder Component matching the Laporkan Meter screen screenshot:
 * - Concrete/industrial background wall texture
 * - Realistic blue water meter with brass pipe connectors on left and right
 * - Detailed white dial face with serial "12345678", odometer "01000 1 m³", "H2O", dials
 * - 4 White rounded corner brackets [  ] framing the meter dial
 * - Subtle scanning laser pulse
 * - Bottom semi-transparent dark pill: camera icon + "Pastikan angka meter terlihat jelas dan tidak buram."
 */
@Composable
fun WaterMeterCameraViewfinder(
    onTapToFocus: () -> Unit = {},
    hasCameraPermission: Boolean = false,
    isFlashOn: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val laserAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(235.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFCBD5E1))
            .clickable(onClick = onTapToFocus)
            .testTag("camera_viewfinder_box"),
        contentAlignment = Alignment.Center
    ) {
        if (hasCameraPermission) {
            CameraPreviewView(
                modifier = Modifier.fillMaxSize(),
                isTorchEnabled = isFlashOn,
                fallbackContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F172A))
                    )
                }
            )
        }

        // Canvas rendering realistic water meter setup or live brackets overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cy = h * 0.44f
            val cx = w * 0.50f

            if (!hasCameraPermission) {
                // Clean dark camera standby background
                drawRect(color = Color(0xFF0F172A))
            }

            // 9. Camera Viewfinder Corner Brackets [  ]
            val bracketW = w * 0.68f
            val bracketH = h * 0.70f
            val bLeft = cx - bracketW / 2f
            val bRight = cx + bracketW / 2f
            val bTop = cy - bracketH / 2f
            val bBottom = cy + bracketH / 2f
            val cornerLen = 24.dp.toPx()
            val bracketStroke = 3.5.dp.toPx()
            val bracketColor = Color.White
            val cornerRadiusPx = 10.dp.toPx()

            // Top-Left Corner
            val tlPath = Path().apply {
                moveTo(bLeft, bTop + cornerLen)
                lineTo(bLeft, bTop + cornerRadiusPx)
                cubicTo(bLeft, bTop, bLeft, bTop, bLeft + cornerRadiusPx, bTop)
                lineTo(bLeft + cornerLen, bTop)
            }
            drawPath(tlPath, color = bracketColor, style = Stroke(width = bracketStroke, cap = StrokeCap.Round))

            // Top-Right Corner
            val trPath = Path().apply {
                moveTo(bRight - cornerLen, bTop)
                lineTo(bRight - cornerRadiusPx, bTop)
                cubicTo(bRight, bTop, bRight, bTop, bRight, bTop + cornerRadiusPx)
                lineTo(bRight, bTop + cornerLen)
            }
            drawPath(trPath, color = bracketColor, style = Stroke(width = bracketStroke, cap = StrokeCap.Round))

            // Bottom-Left Corner
            val blPath = Path().apply {
                moveTo(bLeft, bBottom - cornerLen)
                lineTo(bLeft, bBottom - cornerRadiusPx)
                cubicTo(bLeft, bBottom, bLeft, bBottom, bLeft + cornerRadiusPx, bBottom)
                lineTo(bLeft + cornerLen, bBottom)
            }
            drawPath(blPath, color = bracketColor, style = Stroke(width = bracketStroke, cap = StrokeCap.Round))

            // Bottom-Right Corner
            val brPath = Path().apply {
                moveTo(bRight - cornerLen, bBottom)
                lineTo(bRight - cornerRadiusPx, bBottom)
                cubicTo(bRight, bBottom, bRight, bBottom, bRight, bBottom - cornerRadiusPx)
                lineTo(bRight, bBottom - cornerLen)
            }
            drawPath(brPath, color = bracketColor, style = Stroke(width = bracketStroke, cap = StrokeCap.Round))

            // Laser scan highlight line
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF38BDF8).copy(alpha = laserAlpha),
                        Color.White.copy(alpha = laserAlpha),
                        Color(0xFF38BDF8).copy(alpha = laserAlpha),
                        Color.Transparent
                    )
                ),
                start = Offset(bLeft + 6.dp.toPx(), cy),
                end = Offset(bRight - 6.dp.toPx(), cy),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 10. Bottom Semi-Transparent Dark Pill Overlay:
        // Camera icon in circular focus reticle + "Pastikan angka meter terlihat jelas\ndan tidak buram."
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC0F172A))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Focus camera icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Pastikan angka meter terlihat jelas\ndan tidak buram.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
