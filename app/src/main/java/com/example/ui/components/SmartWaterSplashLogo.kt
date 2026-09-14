package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Smart Water Logo as shown in the mockup:
 * - Pure water droplet profile on clean white canvas
 * - Upper and outer body: Vibrant Sky & Ocean Blue gradient
 * - Lower inner swirl: Fresh Emerald Green leaf swoosh accent
 * - Subtle floating breathing animation
 */
@Composable
fun SmartWaterSplashLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoBreath")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloat"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val offsetY = floatOffset

            // 1. Soft ground shadow
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x220066CC), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.94f),
                    radius = w * 0.35f
                ),
                topLeft = Offset(w * 0.15f, h * 0.88f),
                size = Size(w * 0.7f, h * 0.12f)
            )

            // 2. Full Droplet Silhouette (Blue body)
            val dropletPath = Path().apply {
                val tipX = w * 0.5f
                val tipY = h * 0.08f + offsetY
                val bottomY = h * 0.86f + offsetY
                val leftBulgeX = w * 0.12f
                val rightBulgeX = w * 0.88f
                val bulgeY = h * 0.60f + offsetY

                moveTo(tipX, tipY)
                // Left curve down to bulbous base
                cubicTo(
                    w * 0.44f, h * 0.22f + offsetY,
                    leftBulgeX, bulgeY * 0.70f + offsetY * 0.3f,
                    leftBulgeX, bulgeY
                )
                cubicTo(
                    leftBulgeX, h * 0.78f + offsetY,
                    w * 0.30f, bottomY,
                    tipX, bottomY
                )
                // Right curve back up to tip
                cubicTo(
                    w * 0.70f, bottomY,
                    rightBulgeX, h * 0.78f + offsetY,
                    rightBulgeX, bulgeY
                )
                cubicTo(
                    rightBulgeX, bulgeY * 0.70f + offsetY * 0.3f,
                    w * 0.56f, h * 0.22f + offsetY,
                    tipX, tipY
                )
                close()
            }

            // Fill Droplet with Blue Gradient
            drawPath(
                path = dropletPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00B4D8), // Cyan top
                        Color(0xFF0096C7),
                        Color(0xFF0077B6),
                        Color(0xFF023E8A)  // Deep rich blue base
                    ),
                    startY = h * 0.08f + offsetY,
                    endY = h * 0.86f + offsetY
                )
            )

            // 3. Inner Fresh Emerald Green Leaf Swoosh (representing eco/nature)
            val leafSwooshPath = Path().apply {
                val startX = w * 0.22f
                val startY = h * 0.58f + offsetY
                moveTo(startX, startY)

                // Outer boundary of green leaf along the droplet bottom
                cubicTo(
                    w * 0.25f, h * 0.78f + offsetY,
                    w * 0.42f, h * 0.86f + offsetY,
                    w * 0.68f, h * 0.82f + offsetY
                )
                cubicTo(
                    w * 0.82f, h * 0.78f + offsetY,
                    w * 0.86f, h * 0.64f + offsetY,
                    w * 0.78f, h * 0.52f + offsetY
                )
                // Inner leaf wave swooping back down and across
                cubicTo(
                    w * 0.68f, h * 0.60f + offsetY,
                    w * 0.50f, h * 0.70f + offsetY,
                    w * 0.36f, h * 0.58f + offsetY
                )
                cubicTo(
                    w * 0.28f, h * 0.52f + offsetY,
                    w * 0.24f, h * 0.54f + offsetY,
                    startX, startY
                )
                close()
            }

            drawPath(
                path = leafSwooshPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF10B981), // Emerald
                        Color(0xFF22C55E), // Fresh Leaf Green
                        Color(0xFF16A34A)  // Forest Green
                    ),
                    start = Offset(w * 0.2f, h * 0.6f + offsetY),
                    end = Offset(w * 0.8f, h * 0.85f + offsetY)
                )
            )

            // Leaf rib/accent
            val leafRib = Path().apply {
                moveTo(w * 0.30f, h * 0.70f + offsetY)
                cubicTo(
                    w * 0.45f, h * 0.75f + offsetY,
                    w * 0.60f, h * 0.70f + offsetY,
                    w * 0.75f, h * 0.58f + offsetY
                )
            }
            drawPath(
                path = leafRib,
                color = Color.White.copy(alpha = 0.45f),
                style = Stroke(width = w * 0.02f)
            )

            // 4. White droplet shine on upper left curve
            val shinePath = Path().apply {
                moveTo(w * 0.46f, h * 0.16f + offsetY)
                cubicTo(
                    w * 0.32f, h * 0.28f + offsetY,
                    w * 0.22f, h * 0.44f + offsetY,
                    w * 0.26f, h * 0.54f + offsetY
                )
                cubicTo(
                    w * 0.20f, h * 0.46f + offsetY,
                    w * 0.24f, h * 0.30f + offsetY,
                    w * 0.42f, h * 0.16f + offsetY
                )
                close()
            }
            drawPath(
                path = shinePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.15f)
                    )
                ),
                style = Fill
            )
        }
    }
}
