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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SmartWaterCard

/**
 * 3D Shower Head Illustration Card precisely reproducing the screenshot:
 * - Rounded sky-blue card (#82D2F4)
 * - Metallic 3D shower head angled at ~45 degrees
 * - 3 distinct blue water droplets spraying from the nozzle
 */
@Composable
fun Shower3DCard(
    modifier: Modifier = Modifier,
    width: Dp = 270.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "showerAnim")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "showerFloat"
    )
    val sprayPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sprayPulse"
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
            .testTag("shower_3d_card"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.52f
            val cy = size.height * 0.48f + floatOffset

            // Rotate coordinate system by -45 degrees so shower points down-left
            // Center of rotation is at the nozzle
            rotate(degrees = -45f, pivot = Offset(cx, cy)) {
                // 1. Shower Stem / Handle (extends upwards in rotated frame)
                val stemWidth = 18.dp.toPx()
                val stemHeight = 44.dp.toPx()
                val stemTop = cy - 74.dp.toPx()

                // Stem shadow
                drawRoundRect(
                    color = Color(0x2A003366),
                    topLeft = Offset(cx - stemWidth * 0.45f + 4f, stemTop + 6f),
                    size = Size(stemWidth, stemHeight),
                    cornerRadius = CornerRadius(stemWidth * 0.4f, stemWidth * 0.4f)
                )

                // Stem body with cylindrical metallic gradient
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        0.0f to Color(0xFFBDC7CF),
                        0.3f to Color(0xFFF1F5F8),
                        0.75f to Color(0xFFD3DDE4),
                        1.0f to Color(0xFF9AA7B2),
                        startX = cx - stemWidth * 0.5f,
                        endX = cx + stemWidth * 0.5f
                    ),
                    topLeft = Offset(cx - stemWidth * 0.5f, stemTop),
                    size = Size(stemWidth, stemHeight),
                    cornerRadius = CornerRadius(stemWidth * 0.35f, stemWidth * 0.35f)
                )

                // 2. Dome / Bell of the Shower Head
                val domeWidth = 62.dp.toPx()
                val domeHeight = 42.dp.toPx()
                val domeTop = cy - 36.dp.toPx()

                // Dome 3D shadow underneath
                val domeShadowPath = Path().apply {
                    moveTo(cx - domeWidth * 0.48f + 5f, domeTop + domeHeight + 4f)
                    cubicTo(
                        cx - domeWidth * 0.48f + 5f, domeTop + 6f,
                        cx + domeWidth * 0.48f + 5f, domeTop + 6f,
                        cx + domeWidth * 0.48f + 5f, domeTop + domeHeight + 4f
                    )
                    close()
                }
                drawPath(domeShadowPath, color = Color(0x22003366))

                // Dome metallic curved body
                val domePath = Path().apply {
                    moveTo(cx - domeWidth * 0.5f, domeTop + domeHeight)
                    cubicTo(
                        cx - domeWidth * 0.52f, domeTop + domeHeight * 0.3f,
                        cx - stemWidth * 0.6f, domeTop,
                        cx, domeTop
                    )
                    cubicTo(
                        cx + stemWidth * 0.6f, domeTop,
                        cx + domeWidth * 0.52f, domeTop + domeHeight * 0.3f,
                        cx + domeWidth * 0.5f, domeTop + domeHeight
                    )
                    close()
                }

                drawPath(
                    path = domePath,
                    brush = Brush.horizontalGradient(
                        0.0f to Color(0xFFB4C0C9),
                        0.25f to Color(0xFFF7FAFC),
                        0.6f to Color(0xFFDFE6EC),
                        0.85f to Color(0xFFC0CCD6),
                        1.0f to Color(0xFF9BA8B3),
                        startX = cx - domeWidth * 0.5f,
                        endX = cx + domeWidth * 0.5f
                    )
                )

                // 3. Nozzle Face / Rim Plate
                val rimWidth = 66.dp.toPx()
                val rimHeight = 13.dp.toPx()
                val rimTop = domeTop + domeHeight - rimHeight * 0.3f

                // Rim bevel
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF8D9CA7), Color(0xFF71808C)),
                        startY = rimTop,
                        endY = rimTop + rimHeight
                    ),
                    topLeft = Offset(cx - rimWidth * 0.5f, rimTop + 2f),
                    size = Size(rimWidth, rimHeight)
                )

                // Rim top metallic surface
                drawOval(
                    brush = Brush.horizontalGradient(
                        0.0f to Color(0xFFCAD4DC),
                        0.3f to Color(0xFFFFFFFF),
                        0.7f to Color(0xFFD6DFE6),
                        1.0f to Color(0xFFA5B2BD),
                        startX = cx - rimWidth * 0.5f,
                        endX = cx + rimWidth * 0.5f
                    ),
                    topLeft = Offset(cx - rimWidth * 0.5f, rimTop),
                    size = Size(rimWidth, rimHeight)
                )

                // Tiny nozzle spray holes
                val holeY = rimTop + rimHeight * 0.5f
                val holeSpacing = 11.dp.toPx()
                for (i in -2..2) {
                    drawCircle(
                        color = Color(0xFF637482),
                        radius = 1.8.dp.toPx(),
                        center = Offset(cx + i * holeSpacing, holeY)
                    )
                }

                // 4. Three 3D Water Spray Droplets (capsule shaped)
                // In the rotated frame, they shoot downwards below the nozzle rim
                val dropWidth = 10.dp.toPx()
                val dropHeight = 22.dp.toPx()
                val dropStartY = rimTop + rimHeight + 9.dp.toPx() + sprayPulse

                // 3 droplets spaced evenly: left, center, right
                val dropXOffsets = listOf(
                    -16.dp.toPx(),
                    0f,
                    16.dp.toPx()
                )

                dropXOffsets.forEachIndexed { index, xOffset ->
                    val dropletX = cx + xOffset
                    val dropletY = dropStartY + (if (index == 1) 4.dp.toPx() else 0f)

                    // Droplet drop shadow
                    drawRoundRect(
                        color = Color(0x33004499),
                        topLeft = Offset(dropletX - dropWidth * 0.5f + 2f, dropletY + 3f),
                        size = Size(dropWidth, dropHeight),
                        cornerRadius = CornerRadius(dropWidth * 0.5f, dropWidth * 0.5f)
                    )

                    // 3D vibrant blue droplet with cyan highlight
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            0.0f to Color(0xFF42C7FF),
                            0.3f to Color(0xFF0088FF),
                            0.7f to Color(0xFF0066EE),
                            1.0f to Color(0xFF004CB8),
                            startY = dropletY,
                            endY = dropletY + dropHeight
                        ),
                        topLeft = Offset(dropletX - dropWidth * 0.5f, dropletY),
                        size = Size(dropWidth, dropHeight),
                        cornerRadius = CornerRadius(dropWidth * 0.5f, dropWidth * 0.5f)
                    )

                    // Specular white shine near top-left of each drop
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.85f),
                        topLeft = Offset(dropletX - dropWidth * 0.28f, dropletY + 3.dp.toPx()),
                        size = Size(dropWidth * 0.35f, dropHeight * 0.35f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }
            }
        }
    }
}
