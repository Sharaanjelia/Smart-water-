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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Speed
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
import com.example.data.repository.WaterMonitoringRepository
import com.example.ui.components.ReservoirTankVisual
import com.example.ui.theme.SmartWaterPrimary

/**
 * Halaman Detail Reservoir:
 * Menampilkan representasi visual tangki reservoir, level ketinggian air 78%,
 * statistik kapasitas (10.000 m³), volume saat ini (7.800 m³), debit masuk (inflow),
 * debit keluar (outflow), serta waktu update terakhir.
 */
@Composable
fun ReservoirDetailScreen(
    onNavigateBack: () -> Unit,
    repository: WaterMonitoringRepository = WaterMonitoringRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val reservoir by repository.reservoir.collectAsStateWithLifecycle()

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
                            text = "Detail Reservoir",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Kapasitas & volume air tersimpan",
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
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Tank Card
                item {
                    ReservoirTankVisual(
                        reservoir = reservoir,
                        isExpanded = true
                    )
                }

                // Grid Detail Parameter
                item {
                    Text(
                        text = "Parameter Reservoir",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailMetricCard(
                                icon = Icons.Default.Opacity,
                                label = "Volume Air Saat Ini",
                                value = "${"%,d".format(reservoir.currentVolumeM3.toLong())} m³",
                                subLabel = "78% dari kapasitas total",
                                iconTint = SmartWaterPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            DetailMetricCard(
                                icon = Icons.Default.Height,
                                label = "Tinggi Muka Air",
                                value = "${reservoir.waterLevelMeters} meter",
                                subLabel = "Maks. ${reservoir.maxHeightMeters} meter",
                                iconTint = Color(0xFF0284C7),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailMetricCard(
                                icon = Icons.Default.ArrowDownward,
                                label = "Inflow (Debit Masuk)",
                                value = "${reservoir.inflowRateLps} L/s",
                                subLabel = "Dari IPA Cibeunying",
                                iconTint = Color(0xFF16A34A),
                                modifier = Modifier.weight(1f)
                            )
                            DetailMetricCard(
                                icon = Icons.Default.ArrowUpward,
                                label = "Outflow (Debit Keluar)",
                                value = "${reservoir.outflowRateLps} L/s",
                                subLabel = "Ke Jaringan Distribusi",
                                iconTint = Color(0xFFEA580C),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Info Card: Neraca Air & Status
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Informasi Operasional",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            InfoRow(label = "Kapasitas Tampung Total", value = "${"%,d".format(reservoir.capacityM3.toLong())} m³")
                            InfoRow(label = "Volume Cadangan Air", value = "${"%,d".format(reservoir.currentVolumeM3.toLong())} m³")
                            InfoRow(label = "Keseimbangan Debit (Net Flow)", value = "+6.7 L/s (Stabil Menampung)", valueColor = Color(0xFF16A34A))
                            InfoRow(label = "Kondisi Struktur & Sensor", value = "Normal (Kalibrasi OK)", valueColor = Color(0xFF16A34A))
                            InfoRow(label = "Waktu Sinkronisasi Terakhir", value = "${reservoir.lastUpdated} (Simulasi)")
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
private fun DetailMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    subLabel: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subLabel,
                fontSize = 10.5.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF0F172A)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}
