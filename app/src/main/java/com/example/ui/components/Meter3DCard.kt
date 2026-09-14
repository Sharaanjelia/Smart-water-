package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SmartWaterCard

/**
 * 3D Water Meter Illustration Card for slide 2:
 * "Smart Self Meter Reading"
 */
@Composable
fun Meter3DCard(
    modifier: Modifier = Modifier,
    width: Dp = 270.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meterAnim")
    val needleRot by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "needleRotation"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0x33005BBB),
                spotColor = Color(0x4D0070E0)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(SmartWaterCard)
            .testTag("meter_3d_card"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.5f

            // 1. Water Pipes on left and right (Brass/Metallic)
            val pipeY = cy
            val pipeH = 22.dp.toPx()
            val pipeW = 42.dp.toPx()

            // Left pipe
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE5B564), Color(0xFFC48E33), Color(0xFF91651B)),
                    startY = pipeY - pipeH * 0.5f,
                    endY = pipeY + pipeH * 0.5f
                ),
                topLeft = Offset(cx - 78.dp.toPx(), pipeY - pipeH * 0.5f),
                size = Size(pipeW, pipeH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Right pipe
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE5B564), Color(0xFFC48E33), Color(0xFF91651B)),
                    startY = pipeY - pipeH * 0.5f,
                    endY = pipeY + pipeH * 0.5f
                ),
                topLeft = Offset(cx + 36.dp.toPx(), pipeY - pipeH * 0.5f),
                size = Size(pipeW, pipeH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // 2. Meter Main Circular Housing
            val outerRadius = 52.dp.toPx()

            // Drop shadow
            drawCircle(
                color = Color(0x30002A54),
                radius = outerRadius,
                center = Offset(cx + 4f, cy + 6f)
            )

            // Outer Blue Bezel Casing
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF0090F0), Color(0xFF0058C6)),
                    start = Offset(cx - outerRadius, cy - outerRadius),
                    end = Offset(cx + outerRadius, cy + outerRadius)
                ),
                radius = outerRadius,
                center = Offset(cx, cy)
            )

            // Inner Chrome Rim
            val chromeRadius = 45.dp.toPx()
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFCCD6DD), Color(0xFF8E9BA6)),
                    start = Offset(cx - chromeRadius, cy - chromeRadius),
                    end = Offset(cx + chromeRadius, cy + chromeRadius)
                ),
                radius = chromeRadius,
                center = Offset(cx, cy)
            )

            // White Dial Face
            val dialRadius = 40.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF2F6F9)),
                    center = Offset(cx, cy),
                    radius = dialRadius
                ),
                radius = dialRadius,
                center = Offset(cx, cy)
            )

            // Odometer / Digital counter window
            val counterW = 46.dp.toPx()
            val counterH = 16.dp.toPx()
            val counterY = cy - 16.dp.toPx()

            drawRoundRect(
                color = Color(0xFF1E2832),
                topLeft = Offset(cx - counterW * 0.5f, counterY),
                size = Size(counterW, counterH),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Digits representation (white / red digit reels)
            val digitW = counterW / 5f
            for (i in 0 until 5) {
                val isRed = i >= 3
                drawRoundRect(
                    color = if (isRed) Color(0xFFE53935) else Color(0xFFFFFFFF),
                    topLeft = Offset(cx - counterW * 0.5f + i * digitW + 1f, counterY + 2f),
                    size = Size(digitW - 2f, counterH - 4f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }

            // Meter needle (rotating)
            val needlePivot = Offset(cx, cy + 12.dp.toPx())
            rotate(degrees = needleRot, pivot = needlePivot) {
                val needlePath = Path().apply {
                    moveTo(needlePivot.x - 3f, needlePivot.y)
                    lineTo(needlePivot.x, needlePivot.y - 20.dp.toPx())
                    lineTo(needlePivot.x + 3f, needlePivot.y)
                    close()
                }
                drawPath(needlePath, color = Color(0xFFD32F2F))
                drawCircle(color = Color(0xFF263238), radius = 5.dp.toPx(), center = needlePivot)
                drawCircle(color = Color(0xFFFFFFFF), radius = 2.dp.toPx(), center = needlePivot)
            }

            // Small dial ticks around face
            for (angle in 0 until 12) {
                rotate(degrees = angle * 30f, pivot = Offset(cx, cy)) {
                    drawLine(
                        color = Color(0xFF78909C),
                        start = Offset(cx, cy - dialRadius + 2.dp.toPx()),
                        end = Offset(cx, cy - dialRadius + 5.dp.toPx()),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            // Camera scanner frame overlay to symbolize mobile self-reading
            val scanColor = Color(0xFF0077FF)
            val scanW = 120.dp.toPx()
            val scanH = 100.dp.toPx()
            val cornerLen = 14.dp.toPx()
            val strokeW = 3.dp.toPx()

            // Top-left corner
            drawLine(scanColor, Offset(cx - scanW * 0.5f, cy - scanH * 0.5f), Offset(cx - scanW * 0.5f + cornerLen, cy - scanH * 0.5f), strokeW)
            drawLine(scanColor, Offset(cx - scanW * 0.5f, cy - scanH * 0.5f), Offset(cx - scanW * 0.5f, cy - scanH * 0.5f + cornerLen), strokeW)
            // Top-right corner
            drawLine(scanColor, Offset(cx + scanW * 0.5f, cy - scanH * 0.5f), Offset(cx + scanW * 0.5f - cornerLen, cy - scanH * 0.5f), strokeW)
            drawLine(scanColor, Offset(cx + scanW * 0.5f, cy - scanH * 0.5f), Offset(cx + scanW * 0.5f, cy - scanH * 0.5f + cornerLen), strokeW)
            // Bottom-left corner
            drawLine(scanColor, Offset(cx - scanW * 0.5f, cy + scanH * 0.5f), Offset(cx - scanW * 0.5f + cornerLen, cy + scanH * 0.5f), strokeW)
            drawLine(scanColor, Offset(cx - scanW * 0.5f, cy + scanH * 0.5f), Offset(cx - scanW * 0.5f, cy + scanH * 0.5f - cornerLen), strokeW)
            // Bottom-right corner
            drawLine(scanColor, Offset(cx + scanW * 0.5f, cy + scanH * 0.5f), Offset(cx + scanW * 0.5f - cornerLen, cy + scanH * 0.5f), strokeW)
            drawLine(scanColor, Offset(cx + scanW * 0.5f, cy + scanH * 0.5f), Offset(cx + scanW * 0.5f, cy + scanH * 0.5f - cornerLen), strokeW)
        }
    }
}
