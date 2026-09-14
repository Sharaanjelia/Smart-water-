package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Bottom wavy water layers with fresh green plant leaves emerging from the right,
 * as depicted in Screen 01 (Splash).
 */
@Composable
fun SplashWaterWaveBackground(
    modifier: Modifier = Modifier,
    height: Dp = 220.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height

        // 1. Back wave (Soft light blue)
        val backWave = Path().apply {
            moveTo(0f, h * 0.45f)
            cubicTo(
                w * 0.25f, h * 0.35f,
                w * 0.50f, h * 0.60f,
                w * 0.75f, h * 0.40f
            )
            cubicTo(
                w * 0.88f, h * 0.30f,
                w * 0.95f, h * 0.32f,
                w, h * 0.38f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = backWave,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFBAE6FD).copy(alpha = 0.55f),
                    Color(0xFF7DD3FC).copy(alpha = 0.70f)
                )
            )
        )

        // 2. Middle wave (Sky blue)
        val midWave = Path().apply {
            moveTo(0f, h * 0.60f)
            cubicTo(
                w * 0.20f, h * 0.50f,
                w * 0.45f, h * 0.72f,
                w * 0.70f, h * 0.56f
            )
            cubicTo(
                w * 0.85f, h * 0.46f,
                w * 0.95f, h * 0.52f,
                w, h * 0.55f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = midWave,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF38BDF8),
                    Color(0xFF0284C7)
                )
            )
        )

        // 3. Front wave (Deep rich ocean blue)
        val frontWave = Path().apply {
            moveTo(0f, h * 0.76f)
            cubicTo(
                w * 0.18f, h * 0.68f,
                w * 0.40f, h * 0.85f,
                w * 0.65f, h * 0.70f
            )
            cubicTo(
                w * 0.82f, h * 0.60f,
                w * 0.92f, h * 0.68f,
                w, h * 0.72f
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            path = frontWave,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0284C7),
                    Color(0xFF0369A1),
                    Color(0xFF075985)
                )
            )
        )

        // 4. Botanical plant / green leaves sprouting from bottom-right
        val plantBaseX = w * 0.78f
        val plantBaseY = h * 0.96f

        // Leaf 1: Large leaf pointing up-left
        drawLeaf(
            startX = plantBaseX,
            startY = plantBaseY,
            tipX = w * 0.60f,
            tipY = h * 0.62f,
            belly1X = w * 0.64f,
            belly1Y = h * 0.80f,
            belly2X = w * 0.72f,
            belly2Y = h * 0.68f,
            fillColor = Color(0xFF16A34A),
            highlightColor = Color(0xFF22C55E)
        )

        // Leaf 2: Center upright leaf
        drawLeaf(
            startX = plantBaseX + w * 0.04f,
            startY = plantBaseY,
            tipX = w * 0.72f,
            tipY = h * 0.52f,
            belly1X = w * 0.70f,
            belly1Y = h * 0.70f,
            belly2X = w * 0.80f,
            belly2Y = h * 0.65f,
            fillColor = Color(0xFF15803D),
            highlightColor = Color(0xFF4ADE80)
        )

        // Leaf 3: Leaf pointing up-right
        drawLeaf(
            startX = plantBaseX + w * 0.06f,
            startY = plantBaseY,
            tipX = w * 0.88f,
            tipY = h * 0.58f,
            belly1X = w * 0.80f,
            belly1Y = h * 0.76f,
            belly2X = w * 0.90f,
            belly2Y = h * 0.70f,
            fillColor = Color(0xFF16A34A),
            highlightColor = Color(0xFF22C55E)
        )

        // Leaf 4: Smaller right leaf
        drawLeaf(
            startX = plantBaseX + w * 0.08f,
            startY = plantBaseY,
            tipX = w * 0.96f,
            tipY = h * 0.72f,
            belly1X = w * 0.88f,
            belly1Y = h * 0.85f,
            belly2X = w * 0.96f,
            belly2Y = h * 0.80f,
            fillColor = Color(0xFF15803D),
            highlightColor = Color(0xFF10B981)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(
    startX: Float,
    startY: Float,
    tipX: Float,
    tipY: Float,
    belly1X: Float,
    belly1Y: Float,
    belly2X: Float,
    belly2Y: Float,
    fillColor: Color,
    highlightColor: Color
) {
    val leafPath = Path().apply {
        moveTo(startX, startY)
        quadraticBezierTo(belly1X, belly1Y, tipX, tipY)
        quadraticBezierTo(belly2X, belly2Y, startX, startY)
        close()
    }

    drawPath(
        path = leafPath,
        brush = Brush.linearGradient(
            colors = listOf(highlightColor, fillColor),
            start = Offset(tipX, tipY),
            end = Offset(startX, startY)
        ),
        style = Fill
    )

    // Center vein
    drawLine(
        color = Color.White.copy(alpha = 0.35f),
        start = Offset(startX, startY),
        end = Offset(tipX, tipY),
        strokeWidth = 2.5f
    )
}
