package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MonitoringAlert
import com.example.data.repository.WaterMonitoringRepository
import com.example.ui.theme.SmartWaterPrimary

/**
 * Screen: Kondisi Layanan Air (Customer / Pelanggan)
 *
 * Menampilkan kondisi sistem distribusi air PDAM secara sederhana untuk pelanggan:
 *  1. Status keseluruhan sistem (Normal / Perhatian / Gangguan)
 *  2. Kondisi Reservoir: level air & kapasitas
 *  3. Kondisi Tekanan Air: status zona distribusi pelanggan
 *  4. Kondisi Debit Air: kelancaran aliran zona pelanggan
 *  5. Jaringan Distribusi: status semua zona (highlight zona pelanggan)
 *  6. Peringatan & Gangguan: jika ada gangguan di sistem
 *
 * Data bersumber dari WaterMonitoringRepository (mock simulasi IoT).
 * Screen ini READ-ONLY — pelanggan hanya bisa melihat, tidak mengubah data.
 */
@Composable
fun CustomerWaterConditionScreen(
    onNavigateBack: () -> Unit,
    customerZone: String = "DIST-A",
    waterRepository: WaterMonitoringRepository = WaterMonitoringRepository.getInstance(),
    modifier: Modifier = Modifier
) {
    val reservoir by waterRepository.reservoir.collectAsStateWithLifecycle()
    val distributionPoints by waterRepository.distributionPoints.collectAsStateWithLifecycle()
    val alerts by waterRepository.alerts.collectAsStateWithLifecycle()

    // Hitung status keseluruhan sistem
    val criticalAlerts = alerts.filter { it.type == "CRITICAL" }
    val warningAlerts = alerts.filter { it.type == "WARNING" || it.type == "WASPADA" }
    val systemStatus = when {
        criticalAlerts.isNotEmpty() -> SystemStatus.GANGGUAN
        warningAlerts.isNotEmpty() -> SystemStatus.PERHATIAN
        else -> SystemStatus.NORMAL
    }

    // Nama zona distribusi pelanggan
    val customerZoneLabel = when (customerZone) {
        "DIST-B" -> "Sektor Tengah"
        "DIST-C" -> "Sektor Selatan"
        else -> "Sektor Utara"
    }

    // Ambil data titik distribusi zona pelanggan
    val zonePoint = distributionPoints.find { it.id == customerZone }
        ?: distributionPoints.firstOrNull()

    // Reservoir utama — langsung dari StateFlow singular
    val mainReservoir = reservoir

    // Animasi pulse untuk indikator live/simulasi
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("customer_water_condition_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header Bar ─────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFF1565C0), SmartWaterPrimary)
                        )
                    )
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("back_button_water_condition")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kondisi Layanan Air",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.testTag("water_condition_title")
                            )
                            Text(
                                text = "Wilayah Anda: $customerZoneLabel",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }

                        // Badge Data Simulasi
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4ADE80).copy(alpha = pulseAlpha))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Simulasi",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Overall Status Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(systemStatus.bgColor.copy(alpha = 0.22f))
                            .border(
                                1.dp,
                                systemStatus.bgColor.copy(alpha = 0.4f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .testTag("system_status_banner")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = systemStatus.icon,
                                contentDescription = null,
                                tint = systemStatus.bgColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = systemStatus.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = systemStatus.description,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // ── Konten LazyColumn ──────────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // 1. Kartu Pasokan / Reservoir ─────────────────────────────────────
                item {
                    val reservoirStatus = mainReservoir.status
                    SectionCard(
                        icon = Icons.Default.Water,
                        iconBg = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF1565C0),
                        title = "Pasokan Air Bersih",
                        badgeText = when (reservoirStatus) {
                            "NORMAL" -> "Aman"
                            "WARNING", "WASPADA" -> "Perhatian"
                            else -> "Kritis"
                        },
                        badgeBg = when (reservoirStatus) {
                            "NORMAL" -> Color(0xFFD1FAE5)
                            "WARNING", "WASPADA" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        },
                        badgeTextColor = when (reservoirStatus) {
                            "NORMAL" -> Color(0xFF065F46)
                            "WARNING", "WASPADA" -> Color(0xFF92400E)
                            else -> Color(0xFF991B1B)
                        },
                        testTag = "section_reservoir"
                    ) {
                        if (true) {
                            val level = (mainReservoir.percentage / 100f).coerceIn(0f, 1f)
                            val barColor = when {
                                level >= 0.5f -> Color(0xFF22C55E)
                                level >= 0.3f -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }

                            // Label persentase
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Kapasitas Reservoir",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${mainReservoir.percentage.toInt()}% terisi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = barColor
                                )
                            }
                            Spacer(modifier = Modifier.height(7.dp))

                            // Progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(level)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(barColor)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Pesan sederhana untuk pelanggan
                            Text(
                                text = when {
                                    level >= 0.6f ->
                                        "\u2705 Pasokan air di reservoir dalam kondisi aman. Air tersedia cukup untuk kebutuhan Anda."
                                    level >= 0.35f ->
                                        "\u26A0\uFE0F Kapasitas reservoir mulai berkurang. Tim kami memantau dan akan segera mengisi ulang."
                                    else ->
                                        "\uD83D\uDD34 Kapasitas reservoir rendah. Tim kami sedang menangani situasi ini."
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF475569),
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // 2. Kartu Tekanan Air ──────────────────────────────────────────────
                item {
                    val pressureStatus = zonePoint?.status ?: "NORMAL"
                    SectionCard(
                        icon = Icons.Default.Speed,
                        iconBg = Color(0xFFF3E8FF),
                        iconTint = Color(0xFF7C3AED),
                        title = "Tekanan Air di Rumah Anda",
                        badgeText = when (pressureStatus) {
                            "NORMAL" -> "Normal"
                            "WARNING", "WASPADA" -> "Lemah"
                            else -> "Kritis"
                        },
                        badgeBg = when (pressureStatus) {
                            "NORMAL" -> Color(0xFFD1FAE5)
                            "WARNING", "WASPADA" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        },
                        badgeTextColor = when (pressureStatus) {
                            "NORMAL" -> Color(0xFF065F46)
                            "WARNING", "WASPADA" -> Color(0xFF92400E)
                            else -> Color(0xFF991B1B)
                        },
                        testTag = "section_pressure"
                    ) {
                        if (zonePoint != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${zonePoint.pressureBar} bar",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Tekanan di $customerZoneLabel",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Standar: 2.0 bar",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = if (zonePoint.pressureBar >= 2.0) "\u2713 Sesuai standar"
                                        else "\u2193 Di bawah standar",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (zonePoint.pressureBar >= 2.0) Color(0xFF16A34A)
                                        else Color(0xFFD97706)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = when {
                                    zonePoint.pressureBar >= 2.5 ->
                                        "\u2705 Tekanan air sangat baik. Air mengalir deras dan lancar ke rumah Anda."
                                    zonePoint.pressureBar >= 2.0 ->
                                        "\u2705 Tekanan air normal. Air mengalir dengan baik sesuai standar."
                                    zonePoint.pressureBar >= 1.5 ->
                                        "\u26A0\uFE0F Tekanan air sedikit rendah. Anda mungkin merasakan aliran lebih lambat dari biasanya."
                                    else ->
                                        "\uD83D\uDD34 Tekanan air rendah. Tim kami sedang berupaya memperbaiki kondisi ini."
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF475569),
                                lineHeight = 17.sp
                            )
                        } else {
                            Text(
                                text = "Data tekanan tidak tersedia",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // 3. Kartu Debit / Kelancaran Aliran ───────────────────────────────
                item {
                    val flowStatus = zonePoint?.let {
                        when {
                            it.flowRateLps >= 5.0 -> "LANCAR"
                            it.flowRateLps >= 3.0 -> "CUKUP"
                            else -> "TERBATAS"
                        }
                    } ?: "LANCAR"

                    SectionCard(
                        icon = Icons.Default.Waves,
                        iconBg = Color(0xFFE0F2FE),
                        iconTint = Color(0xFF0284C7),
                        title = "Kelancaran Aliran Air",
                        badgeText = when (flowStatus) {
                            "LANCAR" -> "Lancar"
                            "CUKUP" -> "Cukup"
                            else -> "Terbatas"
                        },
                        badgeBg = when (flowStatus) {
                            "LANCAR" -> Color(0xFFD1FAE5)
                            "CUKUP" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        },
                        badgeTextColor = when (flowStatus) {
                            "LANCAR" -> Color(0xFF065F46)
                            "CUKUP" -> Color(0xFF92400E)
                            else -> Color(0xFF991B1B)
                        },
                        testTag = "section_flow"
                    ) {
                        if (zonePoint != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${zonePoint.flowRateLps} L/s",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "Debit distribusi zona Anda",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${zonePoint.targetCustomers} pelanggan",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "dilayani zona ini",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = when {
                                    zonePoint.flowRateLps >= 5.0 ->
                                        "\u2705 Aliran air di wilayah Anda sangat lancar dan stabil."
                                    zonePoint.flowRateLps >= 3.0 ->
                                        "\u2705 Aliran air cukup baik. Distribusi berjalan normal."
                                    else ->
                                        "\u26A0\uFE0F Aliran air sedikit berkurang. Tim kami sedang memantau kondisi ini."
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF475569),
                                lineHeight = 17.sp
                            )
                        } else {
                            Text(
                                text = "Data debit tidak tersedia",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // 4. Jaringan Distribusi — Semua Zona ─────────────────────────────
                if (distributionPoints.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(16.dp))
                                .testTag("section_distribution"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Header kartu distribusi
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE0FDF4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Opacity,
                                            contentDescription = null,
                                            tint = Color(0xFF0D9488),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Jaringan Distribusi",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Status semua wilayah layanan air",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(12.dp))

                                // Setiap zona
                                distributionPoints.forEachIndexed { idx, point ->
                                    val isCustomerZone = point.id == customerZone
                                    val zoneName = when (point.id) {
                                        "DIST-A" -> "Sektor Utara"
                                        "DIST-B" -> "Sektor Tengah"
                                        "DIST-C" -> "Sektor Selatan"
                                        else -> point.name
                                    }
                                    val statusColor = when (point.status) {
                                        "NORMAL" -> Color(0xFF16A34A)
                                        "WARNING", "WASPADA" -> Color(0xFFD97706)
                                        else -> Color(0xFFDC2626)
                                    }
                                    val statusBg = when (point.status) {
                                        "NORMAL" -> Color(0xFFDCFCE7)
                                        "WARNING", "WASPADA" -> Color(0xFFFEF3C7)
                                        else -> Color(0xFFFEE2E2)
                                    }
                                    val statusLabel = when (point.status) {
                                        "NORMAL" -> "Normal"
                                        "WARNING", "WASPADA" -> "Perhatian"
                                        else -> "Gangguan"
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isCustomerZone) Color(0xFFEFF6FF)
                                                else Color.Transparent
                                            )
                                            .then(
                                                if (isCustomerZone) Modifier.border(
                                                    1.dp,
                                                    Color(0xFFBFDBFE),
                                                    RoundedCornerShape(10.dp)
                                                ) else Modifier
                                            )
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(statusColor)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = zoneName,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF0F172A)
                                                    )
                                                    if (isCustomerZone) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(Color(0xFF1D4ED8))
                                                                .padding(
                                                                    horizontal = 5.dp,
                                                                    vertical = 1.dp
                                                                )
                                                        ) {
                                                            Text(
                                                                text = "Wilayah Anda",
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                                Text(
                                                    text = "${point.pressureBar} bar \u2022 ${point.flowRateLps} L/s",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF94A3B8)
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(statusBg)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = statusLabel,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }
                                    }

                                    if (idx < distributionPoints.lastIndex) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Peringatan & Gangguan ─────────────────────────────────────────
                if (alerts.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(16.dp))
                                .testTag("section_alerts"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CrisisAlert,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Informasi & Peringatan",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "Pembaruan kondisi layanan air",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(10.dp))

                                alerts.forEach { alert ->
                                    CustomerAlertItem(alert = alert)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                // 6. Footer Info ───────────────────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Data ini bersumber dari sistem simulasi IoT Smart Water. " +
                                    "Jika ada gangguan layanan, hubungi call center: 1500-888.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Composable: Section Card ──────────────────────────────────────────────────

@Composable
private fun SectionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    testTag: String = "",
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// ─── Composable: Alert Item untuk Pelanggan ────────────────────────────────────

@Composable
private fun CustomerAlertItem(alert: MonitoringAlert) {
    val bgColor = when (alert.type) {
        "CRITICAL" -> Color(0xFFFEE2E2)
        "WARNING" -> Color(0xFFFEF3C7)
        else -> Color(0xFFDCFCE7)
    }
    val iconColor = when (alert.type) {
        "CRITICAL" -> Color(0xFFDC2626)
        "WARNING" -> Color(0xFFD97706)
        else -> Color(0xFF16A34A)
    }
    val alertIcon = when (alert.type) {
        "CRITICAL" -> Icons.Default.CrisisAlert
        "WARNING" -> Icons.Default.Warning
        else -> Icons.Default.CheckCircle
    }

    // Terjemahkan ke bahasa pelanggan
    val (customerTitle, customerBody) = when (alert.type) {
        "CRITICAL" -> Pair(
            "\u26A0\uFE0F Ada Gangguan Layanan Air",
            "${alert.location.ifBlank { "Sistem" }}: ${alert.message.take(100)}"
        )
        "WARNING" -> Pair(
            "\uD83D\uDCCC Perhatian Layanan Air",
            "${alert.location.ifBlank { "Sistem" }}: ${alert.message.take(100)}"
        )
        else -> Pair(
            "\u2139\uFE0F ${alert.title}",
            alert.message.take(120)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = alertIcon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = customerTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = customerBody,
                fontSize = 11.sp,
                color = Color(0xFF475569),
                lineHeight = 16.sp
            )
            if (alert.timestamp.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.timestamp,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

// ─── Enum: Status Sistem Keseluruhan ──────────────────────────────────────────

private enum class SystemStatus(
    val label: String,
    val description: String,
    val icon: ImageVector,
    val bgColor: Color
) {
    NORMAL(
        label = "Sistem Berjalan Normal",
        description = "Layanan air di wilayah Anda berjalan dengan baik.",
        icon = Icons.Default.CheckCircle,
        bgColor = Color(0xFF4ADE80)
    ),
    PERHATIAN(
        label = "Ada Hal yang Perlu Diperhatikan",
        description = "Tim kami sedang memantau kondisi sistem.",
        icon = Icons.Default.Warning,
        bgColor = Color(0xFFFBBF24)
    ),
    GANGGUAN(
        label = "Ada Gangguan Layanan Air",
        description = "Tim kami sedang berupaya memulihkan layanan.",
        icon = Icons.Default.CrisisAlert,
        bgColor = Color(0xFFF87171)
    )
}
