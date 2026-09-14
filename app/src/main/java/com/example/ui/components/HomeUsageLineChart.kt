package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Water Usage Trend Card matching screenshot 08:
 * - Title: "Tren Pemakaian Air" with blue bar icon
 * - Filter pill: "3 Bulan Terakhir ∨"
 * - Clean area line chart:
 *    - Jan: 320 m³ (blue point)
 *    - Feb: 345 m³ (blue point)
 *    - Mar: 358 m³ (green point)
 * - Area gradient under line, gridlines and X-axis labels
 */
@Composable
fun HomeUsageTrendCard(
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trend_usage_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Header Row: [Bar Icon] Tren Pemakaian Air | [3 Bulan Terakhir v]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Small Bar chart icon
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val barW = 3.dp.toPx()
                            // Bar 1
                            drawRoundRect(
                                color = Color(0xFF0066CC),
                                topLeft = Offset(1.dp.toPx(), 8.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(barW, 8.dp.toPx())
                            )
                            // Bar 2
                            drawRoundRect(
                                color = Color(0xFF0284C7),
                                topLeft = Offset(6.dp.toPx(), 4.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(barW, 12.dp.toPx())
                            )
                            // Bar 3
                            drawRoundRect(
                                color = Color(0xFF38BDF8),
                                topLeft = Offset(11.dp.toPx(), 1.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(barW, 15.dp.toPx())
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tren Pemakaian Air",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                // Filter Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable(onClick = onFilterClick)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3 Bulan Terakhir",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Pilih Periode",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Line Chart Area
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .testTag("trend_line_chart_canvas")
            ) {
                val w = size.width
                val h = size.height

                // Chart geometry
                val xJan = w * 0.18f
                val xFeb = w * 0.50f
                val xMar = w * 0.82f

                val yJan = h * 0.58f
                val yFeb = h * 0.44f
                val yMar = h * 0.32f
                val groundY = h * 0.82f

                // 1. Subtle horizontal base grid line
                drawLine(
                    color = Color(0xFFF1F5F9),
                    start = Offset(w * 0.08f, groundY),
                    end = Offset(w * 0.92f, groundY),
                    strokeWidth = 1.dp.toPx()
                )

                // 2. Light vertical dashed guideline for March (highlighted)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(xMar, yMar),
                    end = Offset(xMar, groundY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                // 3. Shaded Area Under Curve
                val areaPath = Path().apply {
                    moveTo(xJan, yJan)
                    cubicTo(
                        (xJan + xFeb) / 2f, yJan - 2f,
                        (xJan + xFeb) / 2f, yFeb + 2f,
                        xFeb, yFeb
                    )
                    cubicTo(
                        (xFeb + xMar) / 2f, yFeb - 2f,
                        (xFeb + xMar) / 2f, yMar + 2f,
                        xMar, yMar
                    )
                    lineTo(xMar, groundY)
                    lineTo(xJan, groundY)
                    close()
                }

                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0077FF).copy(alpha = 0.22f),
                            Color(0xFF38BDF8).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = yMar,
                        endY = groundY
                    )
                )

                // 4. Smooth Connection Line
                val linePath = Path().apply {
                    moveTo(xJan, yJan)
                    cubicTo(
                        (xJan + xFeb) / 2f, yJan - 2f,
                        (xJan + xFeb) / 2f, yFeb + 2f,
                        xFeb, yFeb
                    )
                    cubicTo(
                        (xFeb + xMar) / 2f, yFeb - 2f,
                        (xFeb + xMar) / 2f, yMar + 2f,
                        xMar, yMar
                    )
                }

                drawPath(
                    path = linePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0066CC), Color(0xFF0284C7), Color(0xFF10B981))
                    ),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // 5. Data Points
                // Point 1: Jan (Blue)
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(xJan, yJan))
                drawCircle(color = Color(0xFF0066CC), radius = 3.5.dp.toPx(), center = Offset(xJan, yJan))

                // Point 2: Feb (Blue)
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(xFeb, yFeb))
                drawCircle(color = Color(0xFF0284C7), radius = 3.5.dp.toPx(), center = Offset(xFeb, yFeb))

                // Point 3: Mar (Green highlighted)
                drawCircle(color = Color(0xFFDCFCE7), radius = 8.dp.toPx(), center = Offset(xMar, yMar))
                drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(xMar, yMar))
                drawCircle(color = Color(0xFF16A34A), radius = 3.5.dp.toPx(), center = Offset(xMar, yMar))

                // 6. Text Labels for Values (above points)
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#475569")
                    textSize = 10.sp.toPx()
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val highlightPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#0066CC")
                    textSize = 10.5.sp.toPx()
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                drawContext.canvas.nativeCanvas.drawText("320 m³", xJan, yJan - 9.dp.toPx(), textPaint)
                drawContext.canvas.nativeCanvas.drawText("345 m³", xFeb, yFeb - 9.dp.toPx(), textPaint)
                drawContext.canvas.nativeCanvas.drawText("358 m³", xMar, yMar - 9.dp.toPx(), highlightPaint)

                // 7. X-Axis Month Labels
                val monthPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#94A3B8")
                    textSize = 11.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val labelY = groundY + 14.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText("Jan", xJan, labelY, monthPaint)
                drawContext.canvas.nativeCanvas.drawText("Feb", xFeb, labelY, monthPaint)
                drawContext.canvas.nativeCanvas.drawText("Mar", xMar, labelY, monthPaint)
            }
        }
    }
}
