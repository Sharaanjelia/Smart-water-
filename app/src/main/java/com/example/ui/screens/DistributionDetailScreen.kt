package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.WaterMonitoringRepository
import com.example.ui.components.DistributionTrendChart
import com.example.ui.theme.SmartWaterPrimary

/**
 * Halaman Detail Titik Distribusi Air:
 * Menampilkan nama titik, status, tekanan air (bar), debit aliran (L/s), waktu update,
 * serta grafik sederhana riwayat tekanan dan debit air (data simulasi).
 */
@Composable
fun DistributionDetailScreen(
    pointId: String,
    onNavigateBack: () -> Unit,
    repository: WaterMonitoringRepository = WaterMonitoringRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val distributionPoints by repository.distributionPoints.collectAsStateWithLifecycle()
    val point = distributionPoints.find { it.id == pointId } ?: distributionPoints.first()

    val statusColor = when (point.status) {
        "WASPADA" -> Color(0xFFEAB308)
        "KRITIS" -> Color(0xFFEF4444)
        else -> Color(0xFF22C55E)
    }

    val statusBg = when (point.status) {
        "WASPADA" -> Color(0xFFFEF9C3)
        "KRITIS" -> Color(0xFFFEE2E2)
        else -> Color(0xFFDCFCE7)
    }

    val statusText = when (point.status) {
        "WASPADA" -> Color(0xFF854D0E)
        "KRITIS" -> Color(0xFF991B1B)
        else -> Color(0xFF166534)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = point.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = point.zone,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Highlight Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = point.name.uppercase(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(statusBg)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = point.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Dual Primary Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Metric 1: Tekanan
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFF0FDF4))
                                        .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "TEKANAN AIR",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${point.pressureBar} bar",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            text = if (point.pressureBar >= 2.0) "Tekanan Optimal" else "Tekanan Rendah",
                                            fontSize = 10.5.sp,
                                            color = if (point.pressureBar >= 2.0) Color(0xFF16A34A) else Color(0xFFDC2626),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Metric 2: Debit
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFEFF6FF))
                                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "DEBIT ALIRAN",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${point.flowRateLps} L/s",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SmartWaterPrimary
                                        )
                                        Text(
                                            text = "Aliran Kontinu",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Timestamp and pipe info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Diameter Pipa: Ø ${point.pipeDiameterMm} mm",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Update terakhir: ${point.lastUpdated}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Chart 1: Grafik Tekanan Air
                item {
                    DistributionTrendChart(
                        title = "Grafik Riwayat Tekanan Air (bar)",
                        unit = "bar",
                        points = point.historicalPressure,
                        primaryColor = Color(0xFF0284C7),
                        secondaryColor = Color(0xFF38BDF8)
                    )
                }

                // Chart 2: Grafik Debit Air
                item {
                    DistributionTrendChart(
                        title = "Grafik Riwayat Debit Aliran (L/s)",
                        unit = "L/s",
                        points = point.historicalFlow,
                        primaryColor = SmartWaterPrimary,
                        secondaryColor = Color(0xFF60A5FA)
                    )
                }

                // Information Simulation Note
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Data grafik ditampilkan secara real-time berbasis simulasi log interval per jam untuk verifikasi distribusi air.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
