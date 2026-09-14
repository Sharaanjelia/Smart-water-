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
                // 1. Concrete wall background with subtle texture
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE2E8F0),
                            Color(0xFFCBD5E1),
                            Color(0xFF94A3B8)
                        )
                    )
                )

                // Horizontal wall seams
                drawLine(
                    color = Color(0xFF64748B).copy(alpha = 0.25f),
                    start = Offset(0f, h * 0.28f),
                    end = Offset(w, h * 0.28f),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF64748B).copy(alpha = 0.25f),
                    start = Offset(0f, h * 0.72f),
                    end = Offset(w, h * 0.72f),
                    strokeWidth = 1.5.dp.toPx()
                )
            val meterRadius = h * 0.38f

            // 2. Horizontal Pipes (Left and Right)
            val pipeH = h * 0.16f
            val pipeY = cy - pipeH / 2f

            // Left Blue pipe
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0284C7), Color(0xFF0055AA), Color(0xFF003875)),
                    startY = pipeY,
                    endY = pipeY + pipeH
                ),
                topLeft = Offset(0f, pipeY),
                size = Size(cx - meterRadius * 0.6f, pipeH)
            )
            // Left Brass Fitting / Hex Nut
            val brassLeftX = cx - meterRadius * 0.85f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFD97706), Color(0xFFB45309), Color(0xFF78350F)),
                    startY = pipeY - 4.dp.toPx(),
                    endY = pipeY + pipeH + 4.dp.toPx()
                ),
                topLeft = Offset(brassLeftX, pipeY - 4.dp.toPx()),
                size = Size(w * 0.08f, pipeH + 8.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Right Blue pipe
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0284C7), Color(0xFF0055AA), Color(0xFF003875)),
                    startY = pipeY,
                    endY = pipeY + pipeH
                ),
                topLeft = Offset(cx + meterRadius * 0.6f, pipeY),
                size = Size(w - (cx + meterRadius * 0.6f), pipeH)
            )
            // Right Brass Fitting / Hex Nut
            val brassRightX = cx + meterRadius * 0.65f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFD97706), Color(0xFFB45309), Color(0xFF78350F)),
                    startY = pipeY - 4.dp.toPx(),
                    endY = pipeY + pipeH + 4.dp.toPx()
                ),
                topLeft = Offset(brassRightX, pipeY - 4.dp.toPx()),
                size = Size(w * 0.08f, pipeH + 8.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // 3. Water Meter Cast Iron Housing (Vibrant Industrial Blue)
            // Shadow under meter
            drawCircle(
                color = Color(0x33000000),
                radius = meterRadius * 1.05f,
                center = Offset(cx, cy + 4.dp.toPx())
            )

            // Outer Blue Housing
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0284C7),
                        Color(0xFF0369A1),
                        Color(0xFF075985),
                        Color(0xFF0C4A6E)
                    ),
                    center = Offset(cx - meterRadius * 0.3f, cy - meterRadius * 0.3f),
                    radius = meterRadius * 1.2f
                ),
                radius = meterRadius,
                center = Offset(cx, cy)
            )

            // Metallic Bezel Ring
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.8f),
                radius = meterRadius * 0.96f,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner dark rim
            drawCircle(
                color = Color(0xFF0F172A),
                radius = meterRadius * 0.90f,
                center = Offset(cx, cy),
                style = Stroke(width = 3.dp.toPx())
            )

            // 4. Clean White Dial Face
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFE2E8F0)),
                    center = Offset(cx, cy),
                    radius = meterRadius * 0.88f
                ),
                radius = meterRadius * 0.88f,
                center = Offset(cx, cy)
            )

            // 5. Dial Text & Elements (Native Canvas)
            val paintSerial = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#475569")
                textSize = 9.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            drawContext.canvas.nativeCanvas.drawText("12345678", cx, cy - meterRadius * 0.52f, paintSerial)

            // 6. Odometer Counter Window
            val odoW = meterRadius * 1.22f
            val odoH = meterRadius * 0.34f
            val odoX = cx - odoW / 2f
            val odoY = cy - meterRadius * 0.38f

            // Frame border of counter
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(odoX, odoY),
                size = Size(odoW, odoH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            val digitW = odoW / 6f
            val paintDigits = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = odoH * 0.68f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.MONOSPACE
            }

            // 5 Black digits: 0 1 0 0 0
            val digitsList = listOf("0", "1", "0", "0", "0")
            for (i in digitsList.indices) {
                drawRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(odoX + i * digitW + 0.5.dp.toPx(), odoY + 0.5.dp.toPx()),
                    size = Size(digitW - 1.dp.toPx(), odoH - 1.dp.toPx())
                )
                val textX = odoX + i * digitW + digitW / 2f
                val textY = odoY + odoH * 0.74f
                drawContext.canvas.nativeCanvas.drawText(digitsList[i], textX, textY, paintDigits)
            }

            // 6th Red decimal digit: 1
            drawRect(
                color = Color(0xFFDC2626),
                topLeft = Offset(odoX + 5 * digitW + 0.5.dp.toPx(), odoY + 0.5.dp.toPx()),
                size = Size(digitW - 1.dp.toPx(), odoH - 1.dp.toPx())
            )
            drawContext.canvas.nativeCanvas.drawText(
                "1",
                odoX + 5 * digitW + digitW / 2f,
                odoY + odoH * 0.74f,
                paintDigits
            )

            // "m³" indicator label next to counter
            val paintM3 = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#334155")
                textSize = 8.5.sp.toPx()
                isFakeBoldText = true
            }
            drawContext.canvas.nativeCanvas.drawText("m³", odoX + odoW + 3.dp.toPx(), odoY + odoH * 0.70f, paintM3)

            // "H2O" branding in dial
            val paintH2O = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#0369A1")
                textSize = 10.sp.toPx()
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText("H2O", cx, cy + meterRadius * 0.08f, paintH2O)

            // 7. Small Sub-dials at the bottom of the meter
            val subDialR = meterRadius * 0.18f
            val subDialY = cy + meterRadius * 0.42f
            val dialCenters = listOf(
                Offset(cx - meterRadius * 0.38f, subDialY),
                Offset(cx, subDialY + meterRadius * 0.12f),
                Offset(cx + meterRadius * 0.38f, subDialY)
            )

            for (center in dialCenters) {
                // Circle tick border
                drawCircle(
                    color = Color(0xFFCBD5E1),
                    radius = subDialR,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Center red pivot
                drawCircle(
                    color = Color(0xFFEF4444),
                    radius = 2.dp.toPx(),
                    center = center
                )
                // Red needle pointer
                drawLine(
                    color = Color(0xFFDC2626),
                    start = center,
                    end = Offset(center.x + subDialR * 0.7f, center.y - subDialR * 0.4f),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Central rotating star indicator
            drawCircle(
                color = Color(0xFFEF4444),
                radius = 3.5.dp.toPx(),
                center = Offset(cx, cy + meterRadius * 0.22f)
            )

            // 8. Glass reflection / lens shine
            val lensReflection = Path().apply {
                moveTo(cx - meterRadius * 0.7f, cy - meterRadius * 0.5f)
                cubicTo(cx - meterRadius * 0.2f, cy - meterRadius * 0.85f, cx + meterRadius * 0.4f, cy - meterRadius * 0.75f, cx + meterRadius * 0.75f, cy - meterRadius * 0.35f)
                cubicTo(cx + meterRadius * 0.45f, cy - meterRadius * 0.55f, cx - meterRadius * 0.1f, cy - meterRadius * 0.65f, cx - meterRadius * 0.7f, cy - meterRadius * 0.5f)
                close()
            }
            drawPath(
                path = lensReflection,
                color = Color.White.copy(alpha = 0.45f),
                style = Fill
            )
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
