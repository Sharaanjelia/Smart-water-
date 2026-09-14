package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
 * Top-left Header Logo: Clean white droplet silhouette with fresh green leaf
 * matching the header in the screenshot.
 */
@Composable
fun SmartWaterHeaderLogo(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // White droplet body
            val dropPath = Path().apply {
                moveTo(w * 0.50f, h * 0.08f)
                cubicTo(
                    w * 0.70f, h * 0.32f,
                    w * 0.92f, h * 0.58f,
                    w * 0.90f, h * 0.76f
                )
                cubicTo(
                    w * 0.88f, h * 0.94f,
                    w * 0.12f, h * 0.94f,
                    w * 0.10f, h * 0.76f
                )
                cubicTo(
                    w * 0.08f, h * 0.58f,
                    w * 0.30f, h * 0.32f,
                    w * 0.50f, h * 0.08f
                )
                close()
            }
            drawPath(
                path = dropPath,
                color = Color.White
            )

            // Inner Green Leaf Swoosh
            val leafPath = Path().apply {
                moveTo(w * 0.50f, h * 0.44f)
                cubicTo(
                    w * 0.74f, h * 0.52f,
                    w * 0.80f, h * 0.74f,
                    w * 0.60f, h * 0.86f
                )
                cubicTo(
                    w * 0.36f, h * 0.84f,
                    w * 0.32f, h * 0.68f,
                    w * 0.50f, h * 0.44f
                )
                close()
            }
            drawPath(
                path = leafPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF22C55E), Color(0xFF16A34A)),
                    start = Offset(w * 0.35f, h * 0.45f),
                    end = Offset(w * 0.75f, h * 0.85f)
                )
            )

            // Subtle leaf vein
            val vein = Path().apply {
                moveTo(w * 0.42f, h * 0.78f)
                cubicTo(w * 0.52f, h * 0.70f, w * 0.58f, h * 0.60f, w * 0.52f, h * 0.48f)
            }
            drawPath(
                path = vein,
                color = Color.White.copy(alpha = 0.5f),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}

/**
 * Header Right Art:
 * - Fluid water wave layers
 * - Giant glowing translucent water drop
 * - Fresh green leaves surrounding the water drop
 */
@Composable
fun HomeHeaderDecorativeArt(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Darker blue wave shape on the right edge
        val wave1 = Path().apply {
            moveTo(w * 0.55f, 0f)
            cubicTo(w * 0.65f, h * 0.30f, w * 0.50f, h * 0.65f, w * 0.60f, h)
            lineTo(w, h)
            lineTo(w, 0f)
            close()
        }
        drawPath(
            path = wave1,
            color = Color(0xFF0072EA).copy(alpha = 0.45f)
        )

        // 2. Cyan / lighter blue wave curve
        val wave2 = Path().apply {
            moveTo(w * 0.70f, 0f)
            cubicTo(w * 0.80f, h * 0.40f, w * 0.65f, h * 0.75f, w * 0.75f, h)
            lineTo(w, h)
            lineTo(w, 0f)
            close()
        }
        drawPath(
            path = wave2,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0088FF).copy(alpha = 0.4f), Color(0xFF00A3FF).copy(alpha = 0.25f))
            )
        )

        // 3. Green botanical leaves behind water drop
        // Leaf pointing top right
        val leafTopRight = Path().apply {
            moveTo(w * 0.82f, h * 0.52f)
            cubicTo(w * 0.88f, h * 0.35f, w * 0.98f, h * 0.25f, w * 1.02f, h * 0.20f)
            cubicTo(w * 1.05f, h * 0.38f, w * 0.95f, h * 0.55f, w * 0.82f, h * 0.52f)
            close()
        }
        drawPath(
            path = leafTopRight,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF22C55E), Color(0xFF15803D)),
                start = Offset(w * 0.82f, h * 0.50f),
                end = Offset(w * 1.02f, h * 0.20f)
            )
        )

        // Leaf pointing top left
        val leafTopLeft = Path().apply {
            moveTo(w * 0.74f, h * 0.55f)
            cubicTo(w * 0.66f, h * 0.42f, w * 0.62f, h * 0.28f, w * 0.64f, h * 0.20f)
            cubicTo(w * 0.72f, h * 0.22f, w * 0.76f, h * 0.35f, w * 0.74f, h * 0.55f)
            close()
        }
        drawPath(
            path = leafTopLeft,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A)),
                start = Offset(w * 0.74f, h * 0.55f),
                end = Offset(w * 0.64f, h * 0.20f)
            )
        )

        // Leaf pointing right
        val leafRight = Path().apply {
            moveTo(w * 0.84f, h * 0.60f)
            cubicTo(w * 0.94f, h * 0.58f, w * 1.02f, h * 0.64f, w * 1.05f, h * 0.68f)
            cubicTo(w * 0.98f, h * 0.78f, w * 0.88f, h * 0.74f, w * 0.84f, h * 0.60f)
            close()
        }
        drawPath(
            path = leafRight,
            color = Color(0xFF16A34A)
        )

        // 4. Giant Glowing Translucent Water Drop
        val dropCx = w * 0.78f
        val dropCy = h * 0.45f
        val dropW = w * 0.22f
        val dropH = h * 0.42f

        val bigDrop = Path().apply {
            moveTo(dropCx, dropCy - dropH * 0.55f)
            cubicTo(
                dropCx + dropW * 0.50f, dropCy - dropH * 0.15f,
                dropCx + dropW * 0.60f, dropCy + dropH * 0.30f,
                dropCx + dropW * 0.35f, dropCy + dropH * 0.50f
            )
            cubicTo(
                dropCx + dropW * 0.10f, dropCy + dropH * 0.62f,
                dropCx - dropW * 0.35f, dropCy + dropH * 0.62f,
                dropCx - dropW * 0.45f, dropCy + dropH * 0.42f
            )
            cubicTo(
                dropCx - dropW * 0.58f, dropCy + dropH * 0.15f,
                dropCx - dropW * 0.35f, dropCy - dropH * 0.18f,
                dropCx, dropCy - dropH * 0.55f
            )
            close()
        }

        // Drop gradient body
        drawPath(
            path = bigDrop,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color(0xFFBAE6FD).copy(alpha = 0.80f),
                    Color(0xFF38BDF8).copy(alpha = 0.45f)
                ),
                center = Offset(dropCx - dropW * 0.15f, dropCy - dropH * 0.15f),
                radius = dropW * 0.9f
            )
        )

        // White shine highlight inside drop
        val dropShine = Path().apply {
            moveTo(dropCx - dropW * 0.20f, dropCy - dropH * 0.28f)
            cubicTo(
                dropCx - dropW * 0.32f, dropCy - dropH * 0.10f,
                dropCx - dropW * 0.30f, dropCy + dropH * 0.10f,
                dropCx - dropW * 0.24f, dropCy + dropH * 0.15f
            )
            cubicTo(
                dropCx - dropW * 0.26f, dropCy + dropH * 0.05f,
                dropCx - dropW * 0.28f, dropCy - dropH * 0.10f,
                dropCx - dropW * 0.16f, dropCy - dropH * 0.24f
            )
            close()
        }
        drawPath(
            path = dropShine,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
