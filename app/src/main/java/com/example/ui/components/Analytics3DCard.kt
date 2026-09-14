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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SmartWaterCard

/**
 * 3D Analytics & Savings Illustration Card for slide 3:
 * "Pantau penggunaan air harian dan hemat pengeluaran."
 */
@Composable
fun Analytics3DCard(
    modifier: Modifier = Modifier,
    width: Dp = 270.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "analyticsAnim")
    val pulseHeight by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chartPulse"
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
            .testTag("analytics_3d_card"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.5f

            // 1. Analytics Chart Card Surface inside the main card
            val chartW = 160.dp.toPx()
            val chartH = 95.dp.toPx()
            val chartTop = cy - chartH * 0.5f

            // Soft white background card
            drawRoundRect(
                color = Color.White.copy(alpha = 0.92f),
                topLeft = Offset(cx - chartW * 0.5f, chartTop),
                size = Size(chartW, chartH),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
            )

            // Horizontal grid lines
            for (i in 1..3) {
                val y = chartTop + i * (chartH / 4f)
                drawLine(
                    color = Color(0xFFE2EAF0),
                    start = Offset(cx - chartW * 0.42f, y),
                    end = Offset(cx + chartW * 0.42f, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Animated Bar Charts showing water efficiency
            val barWidth = 14.dp.toPx()
            val baseLine = chartTop + chartH - 16.dp.toPx()
            val barHeights = listOf(
                32.dp.toPx(),
                46.dp.toPx(),
                58.dp.toPx() * pulseHeight,
                38.dp.toPx(),
                24.dp.toPx()
            )
            val barSpacing = 24.dp.toPx()
            val startX = cx - (barSpacing * 2f)

            barHeights.forEachIndexed { index, bHeight ->
                val bx = startX + index * barSpacing
                val isOptimal = index == 4 || index == 3

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = if (isOptimal) {
                            listOf(Color(0xFF00C897), Color(0xFF009668))
                        } else {
                            listOf(Color(0xFF0095FF), Color(0xFF0058C6))
                        },
                        startY = baseLine - bHeight,
                        endY = baseLine
                    ),
                    topLeft = Offset(bx - barWidth * 0.5f, baseLine - bHeight),
                    size = Size(barWidth, bHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }

            // Savings badge pill in top right corner of the chart
            val badgeW = 42.dp.toPx()
            val badgeH = 18.dp.toPx()
            drawRoundRect(
                color = Color(0xFFE8F8F2),
                topLeft = Offset(cx + chartW * 0.5f - badgeW - 10.dp.toPx(), chartTop + 10.dp.toPx()),
                size = Size(badgeW, badgeH),
                cornerRadius = CornerRadius(badgeH * 0.5f, badgeH * 0.5f)
            )

            // Trend line
            val trendPath = Path().apply {
                moveTo(startX - barWidth * 0.5f, baseLine - barHeights[0] * 0.9f)
                cubicTo(
                    startX + barSpacing * 0.5f, baseLine - barHeights[1],
                    startX + barSpacing * 1.5f, baseLine - barHeights[2],
                    startX + barSpacing * 2f, baseLine - barHeights[2]
                )
                cubicTo(
                    startX + barSpacing * 2.5f, baseLine - barHeights[2],
                    startX + barSpacing * 3f, baseLine - barHeights[3],
                    startX + barSpacing * 4f, baseLine - barHeights[4]
                )
            }

            // Green trend curve
            drawPath(
                path = trendPath,
                color = Color(0xFF00A86B),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }
    }
}
