package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrendPoint

/**
 * Grafik garis tren berbasis Canvas untuk metrik tekanan air (bar) dan debit air (L/s).
 * Ringan, responsif, dan menyatu alami dengan tema Smart Water.
 */
@Composable
fun DistributionTrendChart(
    title: String,
    unit: String,
    points: List<TrendPoint>,
    primaryColor: Color = Color(0xFF0066E0),
    secondaryColor: Color = Color(0xFF38BDF8),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Chart Title & Latest Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Riwayat 6 Jam Terakhir",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                if (points.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${points.last().value} $unit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (points.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data historis", fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
            } else {
                val minVal = (points.minOfOrNull { it.value } ?: 0f) * 0.85f
                val maxVal = (points.maxOfOrNull { it.value } ?: 10f) * 1.15f
                val range = if (maxVal > minVal) maxVal - minVal else 1f

                // Canvas Line Chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / (points.size - 1).coerceAtLeast(1)

                    // Draw 3 horizontal dotted/dashed grid lines
                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = height * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color(0xFFF1F5F9),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Compute points coordinates
                    val coords = points.mapIndexed { index, item ->
                        val x = index * spacing
                        val normalized = (item.value - minVal) / range
                        val y = height - (normalized * height)
                        Offset(x, y)
                    }

                    // Draw area fill
                    val fillPath = Path().apply {
                        moveTo(coords.first().x, height)
                        coords.forEach { lineTo(it.x, it.y) }
                        lineTo(coords.last().x, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.25f),
                                primaryColor.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw line
                    val strokePath = Path().apply {
                        moveTo(coords.first().x, coords.first().y)
                        for (i in 1 until coords.size) {
                            lineTo(coords[i].x, coords[i].y)
                        }
                    }

                    drawPath(
                        path = strokePath,
                        color = primaryColor,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dots on data points
                    coords.forEach { point ->
                        drawCircle(
                            color = Color.White,
                            radius = 4.5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 3.dp.toPx(),
                            center = point
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // X-Axis Time Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    points.forEach { point ->
                        Text(
                            text = point.time,
                            fontSize = 9.5.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
