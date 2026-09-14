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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WaterDropletPurpleGlow

/**
 * 3D-styled Water Droplet inside a vibrant circular gradient background,
 * precisely matching the Smart Water logo shown in the design.
 */
@Composable
fun WaterDropletLogo(
    modifier: Modifier = Modifier,
    size: Dp = 112.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dropletGlow")
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 14.dp,
                shape = CircleShape,
                ambientColor = Color(0x330088FF),
                spotColor = Color(0x660066EE)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00BAF7),
                        Color(0xFF0092EC),
                        Color(0xFF006FE2)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .testTag("water_droplet_logo"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Soft base shadow inside circle for 3D depth
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55003A85), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.88f),
                    radius = w * 0.35f
                ),
                topLeft = Offset(w * 0.15f, h * 0.72f),
                size = Size(w * 0.7f, h * 0.28f)
            )

            // 2. Primary 3D Droplet Path
            val dropletPath = Path().apply {
                val tipX = w * 0.5f
                val tipY = h * 0.12f + glowOffset * 0.2f
                val bottomY = h * 0.86f
                val leftControlX = w * 0.14f
                val rightControlX = w * 0.86f
                val bulgeY = h * 0.62f

                moveTo(tipX, tipY)
                // Left curve from sharp tip to bulbous base
                cubicTo(
                    w * 0.42f, h * 0.26f,
                    leftControlX, bulgeY * 0.75f,
                    leftControlX, bulgeY
                )
                cubicTo(
                    leftControlX, h * 0.80f,
                    w * 0.30f, bottomY,
                    tipX, bottomY
                )
                // Right curve from base back to tip
                cubicTo(
                    w * 0.70f, bottomY,
                    rightControlX, h * 0.80f,
                    rightControlX, bulgeY
                )
                cubicTo(
                    rightControlX, bulgeY * 0.75f,
                    w * 0.58f, h * 0.26f,
                    tipX, tipY
                )
                close()
            }

            // 3. Render droplet with multi-color gradient (Cyan -> Sky Blue -> Translucent Violet Glow)
            drawPath(
                path = dropletPath,
                brush = Brush.verticalGradient(
                    0.0f to Color(0xFF67EBFF),
                    0.25f to Color(0xFF38D5FA),
                    0.65f to Color(0xFF009EEA),
                    0.85f to Color(0xFF7098FC),
                    1.0f to WaterDropletPurpleGlow
                )
            )

            // 4. Subtle inner translucent highlight on droplet
            val innerShinePath = Path().apply {
                moveTo(w * 0.48f, h * 0.22f)
                cubicTo(
                    w * 0.35f, h * 0.38f,
                    w * 0.28f, h * 0.56f,
                    w * 0.32f, h * 0.72f
                )
                cubicTo(
                    w * 0.24f, h * 0.62f,
                    w * 0.24f, h * 0.44f,
                    w * 0.44f, h * 0.22f
                )
                close()
            }

            drawPath(
                path = innerShinePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.75f),
                        Color.White.copy(alpha = 0.25f),
                        Color.Transparent
                    )
                )
            )

            // 5. Specular highlight spot near top curve
            drawCircle(
                color = Color.White.copy(alpha = 0.85f),
                radius = w * 0.05f,
                center = Offset(w * 0.42f, h * 0.38f)
            )

            // 6. Secondary soft light reflection at bottom-right
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x99D5B8FF), Color.Transparent),
                    center = Offset(w * 0.52f, h * 0.78f),
                    radius = w * 0.22f
                ),
                topLeft = Offset(w * 0.32f, h * 0.68f),
                size = Size(w * 0.4f, h * 0.18f)
            )
        }
    }
}
