package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Water
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MonitoringAlert
import com.example.data.repository.WaterMonitoringRepository
import com.example.ui.components.ReservoirTankVisual
import com.example.ui.components.WaterFlowPipelineVisual
import com.example.ui.theme.SmartWaterPrimary

/**
 * Layar Dashboard Utama Monitoring Air PDAM:
 * Menampilkan 4 KPI Cards (Level Reservoir, Debit Air, Tekanan, Status Reservoir),
 * Visual Aliran Air, Alert Peringatan, dan pintasan ke Detail Reservoir & Titik Distribusi.
 */
@Composable
fun WaterMonitoringDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReservoirDetail: () -> Unit,
    onNavigateToDistributionNetwork: () -> Unit,
    onNavigateToDistributionDetail: (String) -> Unit,
    repository: WaterMonitoringRepository = WaterMonitoringRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val reservoir by repository.reservoir.collectAsStateWithLifecycle()
    val distributionPoints by repository.distributionPoints.collectAsStateWithLifecycle()
    val alerts by repository.alerts.collectAsStateWithLifecycle()

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Monitoring Air",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Simulation Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0284C7))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Data Simulasi",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "Pantau kondisi reservoir dan distribusi air.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Live Status Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF16A34A).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Live SCADA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Notice: Data Monitoring & Simulasi
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF6FF))
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SmartWaterPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Data Monitoring • Prototipe Simulasi",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Text(
                                text = "Parameter level, tekanan, dan debit disimulasikan untuk evaluasi reservoir & distribusi.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }
                }

                // 4 KPI Cards Grid (2x2)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Card 1: Level Reservoir
                            KpiCard(
                                icon = Icons.Default.Opacity,
                                iconBg = Color(0xFFE0F2FE),
                                iconTint = Color(0xFF0284C7),
                                title = "Level Reservoir",
                                value = "${reservoir.percentage}%",
                                unit = "Kapasitas",
                                status = "Normal",
                                statusColor = Color(0xFF16A34A),
                                info = "${"%,d".format(reservoir.currentVolumeM3.toLong())} m³",
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToReservoirDetail
                            )

                            // Card 2: Debit Air
                            val avgFlow = distributionPoints.firstOrNull()?.flowRateLps ?: 12.5
                            KpiCard(
                                icon = Icons.Default.Waves,
                                iconBg = Color(0xFFDCFCE7),
                                iconTint = Color(0xFF16A34A),
                                title = "Debit Air",
                                value = "$avgFlow",
                                unit = "L/s",
                                status = "Stabil",
                                statusColor = Color(0xFF16A34A),
                                info = "Distribusi A (Utama)",
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigateToDistributionDetail("DIST-A") }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Card 3: Tekanan
                            val avgPressure = distributionPoints.firstOrNull()?.pressureBar ?: 2.8
                            KpiCard(
                                icon = Icons.Default.Speed,
                                iconBg = Color(0xFFF3E8FF),
                                iconTint = Color(0xFF9333EA),
                                title = "Tekanan Air",
                                value = "$avgPressure",
                                unit = "bar",
                                status = "Normal",
                                statusColor = Color(0xFF16A34A),
                                info = "Jaringan Distribusi A",
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToDistributionNetwork
                            )

                            // Card 4: Status Reservoir
                            KpiCard(
                                icon = Icons.Default.Factory,
                                iconBg = Color(0xFFFEF3C7),
                                iconTint = Color(0xFFD97706),
                                title = "Status Reservoir",
                                value = reservoir.status,
                                unit = "",
                                status = "Operasional",
                                statusColor = Color(0xFF16A34A),
                                info = "Inflow ${reservoir.inflowRateLps} L/s",
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToReservoirDetail
                            )
                        }
                    }
                }

                // Section 1: Visual Reservoir Tangki
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kondisi Reservoir",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Detail Reservoir ➔",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SmartWaterPrimary,
                                modifier = Modifier.clickable { onNavigateToReservoirDetail() }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        ReservoirTankVisual(reservoir = reservoir)
                    }
                }

                // Section 2: Visual Aliran Air (Pipeline)
                item {
                    WaterFlowPipelineVisual(
                        reservoir = reservoir,
                        distributionPoints = distributionPoints,
                        onSelectDistributionPoint = onNavigateToDistributionDetail
                    )
                }

                // Section 3: Monitoring Alert Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    Icon(
                                        imageVector = Icons.Default.CrisisAlert,
                                        contentDescription = null,
                                        tint = Color(0xFFEA580C),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Monitoring Alert",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${alerts.size} Peringatan",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                alerts.forEach { alert ->
                                    val isWarn = alert.type == "WARNING"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isWarn) Color(0xFFFFFBEB) else Color(0xFFF0FDF4))
                                            .border(
                                                1.dp,
                                                if (isWarn) Color(0xFFFDE68A) else Color(0xFFBBF7D0),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = if (isWarn) Icons.Default.Warning else Icons.Default.Info,
                                            contentDescription = null,
                                            tint = if (isWarn) Color(0xFFD97706) else Color(0xFF16A34A),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = alert.title,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isWarn) Color(0xFF92400E) else Color(0xFF166534)
                                                )
                                                Text(
                                                    text = alert.timestamp,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = alert.message,
                                                fontSize = 11.sp,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 4: Jaringan Distribusi Action Link
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDistributionNetwork() },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Jaringan Distribusi Pipa Air",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Periksa tekanan & debit pada 3 titik distribusi (A, B, C)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    value: String,
    unit: String,
    status: String,
    statusColor: Color,
    info: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = info,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
