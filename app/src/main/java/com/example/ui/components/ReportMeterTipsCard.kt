package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tips Foto Meter Card matching screenshot 09:
 * - Light bulb icon badge (top left)
 * - Title "Tips Foto Meter"
 * - 3 checklist items with green circular checkmarks:
 *   - Pastikan pencahayaan cukup
 *   - Fokus pada angka meter
 *   - Hindari bayangan dan blur
 * - Right side artistic illustration: Glowing cyan water drop & botanical green leaves
 */
@Composable
fun ReportMeterTipsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("report_meter_tips_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE0F2FE), RoundedCornerShape(20.dp))
        ) {
            // Right Decorative Botanical Leaves & Droplet Art
            TipsCardDecorativeArt(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(130.dp)
                    .height(115.dp)
            )

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Header Row: [Bulb Icon] Tips Foto Meter
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Tips Foto Meter",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Checklist Items
                TipsCheckItem(text = "Pastikan pencahayaan cukup")
                Spacer(modifier = Modifier.height(7.dp))
                TipsCheckItem(text = "Fokus pada angka meter")
                Spacer(modifier = Modifier.height(7.dp))
                TipsCheckItem(text = "Hindari bayangan dan blur")
            }
        }
    }
}

@Composable
private fun TipsCheckItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Green circle with white checkmark
        Box(
            modifier = Modifier
                .size(17.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 11.5.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TipsCardDecorativeArt(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Soft light-blue cloud arcs in the background
        val cloudPath = Path().apply {
            moveTo(w * 0.30f, h)
            cubicTo(w * 0.25f, h * 0.70f, w * 0.50f, h * 0.65f, w * 0.60f, h * 0.75f)
            cubicTo(w * 0.75f, h * 0.55f, w * 1.05f, h * 0.65f, w, h)
            close()
        }
        drawPath(
            path = cloudPath,
            color = Color(0xFFBAE6FD).copy(alpha = 0.4f)
        )

        // 2. Translucent Cyan Water Drop
        val dropCx = w * 0.72f
        val dropCy = h * 0.42f
        val dropW = w * 0.24f
        val dropH = h * 0.46f

        val dropPath = Path().apply {
            moveTo(dropCx, dropCy - dropH * 0.5f)
            cubicTo(
                dropCx + dropW * 0.5f, dropCy - dropH * 0.1f,
                dropCx + dropW * 0.55f, dropCy + dropH * 0.3f,
                dropCx + dropW * 0.3f, dropCy + dropH * 0.5f
            )
            cubicTo(
                dropCx + dropW * 0.05f, dropCy + dropH * 0.65f,
                dropCx - dropW * 0.3f, dropCy + dropH * 0.65f,
                dropCx - dropW * 0.4f, dropCy + dropH * 0.4f
            )
            cubicTo(
                dropCx - dropW * 0.55f, dropCy + dropH * 0.15f,
                dropCx - dropW * 0.35f, dropCy - dropH * 0.15f,
                dropCx, dropCy - dropH * 0.5f
            )
            close()
        }
        drawPath(
            path = dropPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF38BDF8),
                    Color(0xFF0284C7),
                    Color(0xFF0369A1)
                ),
                center = Offset(dropCx - dropW * 0.15f, dropCy - dropH * 0.15f),
                radius = dropW * 0.9f
            )
        )

        // Drop White Highlight
        val dropShine = Path().apply {
            moveTo(dropCx - dropW * 0.18f, dropCy - dropH * 0.22f)
            cubicTo(
                dropCx - dropW * 0.28f, dropCy - dropH * 0.08f,
                dropCx - dropW * 0.25f, dropCy + dropH * 0.10f,
                dropCx - dropW * 0.18f, dropCy + dropH * 0.14f
            )
            cubicTo(
                dropCx - dropW * 0.22f, dropCy + dropH * 0.04f,
                dropCx - dropW * 0.24f, dropCy - dropH * 0.08f,
                dropCx - dropW * 0.14f, dropCy - dropH * 0.18f
            )
            close()
        }
        drawPath(dropShine, color = Color.White.copy(alpha = 0.85f))

        // 3. Green Botanical Leaves (Bottom Right)
        // Leaf 1 (Big leaf pointing up-right)
        val leaf1 = Path().apply {
            moveTo(w * 0.78f, h * 0.95f)
            cubicTo(w * 0.75f, h * 0.68f, w * 0.90f, h * 0.55f, w * 0.98f, h * 0.50f)
            cubicTo(w * 1.05f, h * 0.72f, w * 0.96f, h * 0.92f, w * 0.78f, h * 0.95f)
            close()
        }
        drawPath(
            path = leaf1,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF22C55E), Color(0xFF15803D)),
                start = Offset(w * 0.78f, h * 0.95f),
                end = Offset(w * 0.98f, h * 0.50f)
            )
        )

        // Leaf 2 (Main leaf pointing up-left)
        val leaf2 = Path().apply {
            moveTo(w * 0.78f, h * 0.95f)
            cubicTo(w * 0.65f, h * 0.82f, w * 0.62f, h * 0.65f, w * 0.72f, h * 0.60f)
            cubicTo(w * 0.82f, h * 0.65f, w * 0.84f, h * 0.82f, w * 0.78f, h * 0.95f)
            close()
        }
        drawPath(
            path = leaf2,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4ADE80), Color(0xFF16A34A)),
                start = Offset(w * 0.72f, h * 0.60f),
                end = Offset(w * 0.78f, h * 0.95f)
            )
        )

        // Leaf 3 (Bottom leaf pointing left)
        val leaf3 = Path().apply {
            moveTo(w * 0.76f, h)
            cubicTo(w * 0.55f, h * 0.92f, w * 0.52f, h * 0.76f, w * 0.60f, h * 0.72f)
            cubicTo(w * 0.70f, h * 0.74f, w * 0.72f, h * 0.90f, w * 0.76f, h)
            close()
        }
        drawPath(
            path = leaf3,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF86EFAC), Color(0xFF22C55E)),
                start = Offset(w * 0.60f, h * 0.72f),
                end = Offset(w * 0.76f, h)
            )
        )
    }
}
