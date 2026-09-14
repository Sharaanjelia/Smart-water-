package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Water Meter Graphic Illustration matching the first card on the Home Screen:
 * - Circular water meter device with metallic blue bezel
 * - Side pipe connector tubes
 * - Inner meter dial with digital readout "01000"
 * - Surrounding green botanical leaves
 * - Cyan floating water droplets
 */
@Composable
fun HomeWaterMeterIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.52f
            val cy = h * 0.50f
            val r = w * 0.32f

            // 1. Decorative botanical leaves in the background (left and top)
            // Leaf 1: Left leaf
            val leaf1 = Path().apply {
                moveTo(w * 0.22f, h * 0.70f)
                cubicTo(w * 0.05f, h * 0.60f, w * 0.08f, h * 0.35f, w * 0.28f, h * 0.32f)
                cubicTo(w * 0.32f, h * 0.48f, w * 0.30f, h * 0.62f, w * 0.22f, h * 0.70f)
                close()
            }
            drawPath(
                path = leaf1,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E)),
                    start = Offset(w * 0.08f, h * 0.32f),
                    end = Offset(w * 0.28f, h * 0.70f)
                )
            )

            // Leaf 2: Bottom left leaf
            val leaf2 = Path().apply {
                moveTo(w * 0.26f, h * 0.78f)
                cubicTo(w * 0.12f, h * 0.75f, w * 0.14f, h * 0.90f, w * 0.32f, h * 0.92f)
                cubicTo(w * 0.34f, h * 0.85f, w * 0.30f, h * 0.80f, w * 0.26f, h * 0.78f)
                close()
            }
            drawPath(
                path = leaf2,
                color = Color(0xFF16A34A)
            )

            // 2. Cyan Floating Water Droplets
            // Drop 1 (top right)
            val drop1 = Path().apply {
                moveTo(w * 0.82f, h * 0.28f)
                cubicTo(w * 0.86f, h * 0.34f, w * 0.86f, h * 0.40f, w * 0.82f, h * 0.42f)
                cubicTo(w * 0.78f, h * 0.40f, w * 0.78f, h * 0.34f, w * 0.82f, h * 0.28f)
                close()
            }
            drawPath(drop1, color = Color(0xFF38BDF8))

            // Drop 2 (bottom right)
            drawCircle(
                color = Color(0xFF7DD3FC),
                radius = w * 0.032f,
                center = Offset(w * 0.78f, h * 0.72f)
            )

            // 3. Pipe Connectors
            // Left pipe
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0066CC), Color(0xFF004499))
                ),
                topLeft = Offset(cx - r - w * 0.14f, cy - h * 0.08f),
                size = Size(w * 0.18f, h * 0.16f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Left pipe flange
            drawRoundRect(
                color = Color(0xFF003388),
                topLeft = Offset(cx - r - w * 0.15f, cy - h * 0.11f),
                size = Size(w * 0.05f, h * 0.22f),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Right pipe
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0066CC), Color(0xFF004499))
                ),
                topLeft = Offset(cx + r - w * 0.04f, cy - h * 0.08f),
                size = Size(w * 0.18f, h * 0.16f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Right pipe flange
            drawRoundRect(
                color = Color(0xFF003388),
                topLeft = Offset(cx + r + w * 0.10f, cy - h * 0.11f),
                size = Size(w * 0.05f, h * 0.22f),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // 4. Bottom mount / base
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0055BB), Color(0xFF003388))
                ),
                topLeft = Offset(cx - w * 0.09f, cy + r - h * 0.05f),
                size = Size(w * 0.18f, h * 0.22f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // 5. Outer Bezel / Meter Housing
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF0077EE), Color(0xFF0055BB), Color(0xFF003B82)),
                    center = Offset(cx - r * 0.3f, cy - r * 0.3f),
                    radius = r * 1.2f
                ),
                radius = r,
                center = Offset(cx, cy)
            )

            // 6. Silver / Chrome Inner Ring
            drawCircle(
                color = Color(0xFF93C5FD),
                radius = r * 0.88f,
                center = Offset(cx, cy),
                style = Stroke(width = w * 0.025f)
            )

            // 7. Glass Dial Face (Clean white & sky gradient)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFF0F9FF), Color(0xFFE0F2FE)),
                    center = Offset(cx, cy),
                    radius = r * 0.82f
                ),
                radius = r * 0.84f,
                center = Offset(cx, cy)
            )

            // 8. Meter Dial Ticks
            val tickCount = 12
            for (i in 0 until tickCount) {
                val angle = (i * 360f / tickCount) * (Math.PI / 180f)
                val startR = r * 0.72f
                val endR = r * 0.80f
                val x1 = cx + (startR * Math.cos(angle)).toFloat()
                val y1 = cy + (startR * Math.sin(angle)).toFloat()
                val x2 = cx + (endR * Math.cos(angle)).toFloat()
                val y2 = cy + (endR * Math.sin(angle)).toFloat()
                drawLine(
                    color = Color(0xFF94A3B8),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 9. Digital Counter Window in Meter
            val counterW = w * 0.38f
            val counterH = h * 0.16f
            val counterX = cx - counterW / 2f
            val counterY = cy - counterH * 0.5f

            // Frame of counter
            drawRoundRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(counterX, counterY),
                size = Size(counterW, counterH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Digit sections inside counter window
            val digitW = counterW / 5f
            // Digits 1-3: Black background
            for (i in 0..2) {
                drawRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(counterX + i * digitW + 0.5.dp.toPx(), counterY + 0.5.dp.toPx()),
                    size = Size(digitW - 1.dp.toPx(), counterH - 1.dp.toPx())
                )
            }
            // Digits 4-5: Red background for decimal digits
            for (i in 3..4) {
                drawRect(
                    color = Color(0xFFDC2626),
                    topLeft = Offset(counterX + i * digitW + 0.5.dp.toPx(), counterY + 0.5.dp.toPx()),
                    size = Size(digitW - 1.dp.toPx(), counterH - 1.dp.toPx())
                )
            }

            // Text "01000" using native canvas
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = counterH * 0.65f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.MONOSPACE
            }
            val digits = listOf("0", "1", "0", "0", "0")
            for (i in digits.indices) {
                val charX = counterX + i * digitW + digitW / 2f
                val charY = counterY + counterH * 0.72f
                drawContext.canvas.nativeCanvas.drawText(
                    digits[i],
                    charX,
                    charY,
                    paint
                )
            }

            // 10. Center star / spinner wheel
            drawCircle(
                color = Color(0xFF0284C7),
                radius = w * 0.05f,
                center = Offset(cx, cy + r * 0.42f)
            )
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = w * 0.025f,
                center = Offset(cx, cy + r * 0.42f)
            )

            // 11. Glass reflection curved shine
            val shineArc = Path().apply {
                moveTo(cx - r * 0.6f, cy - r * 0.5f)
                cubicTo(cx - r * 0.2f, cy - r * 0.75f, cx + r * 0.4f, cy - r * 0.65f, cx + r * 0.65f, cy - r * 0.35f)
                cubicTo(cx + r * 0.35f, cy - r * 0.55f, cx - r * 0.15f, cy - r * 0.6f, cx - r * 0.6f, cy - r * 0.5f)
                close()
            }
            drawPath(
                path = shineArc,
                color = Color.White.copy(alpha = 0.55f),
                style = Fill
            )
        }
    }
}
