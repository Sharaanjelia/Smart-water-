package com.example.data.repository

import com.example.data.model.DistributionPoint
import com.example.data.model.MonitoringAlert
import com.example.data.model.ReservoirData
import com.example.data.model.TrendPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository data simulasi monitoring air untuk reservoir dan jaringan pipa distribusi.
 * Data bersifat mock/dummy realistis sesuai spesifikasi sistem PDAM Smart Water.
 */
class WaterMonitoringRepository private constructor() {

    private val _reservoir = MutableStateFlow(
        ReservoirData(
            id = "RES-01",
            name = "Reservoir Utama",
            location = "IPA Cibeunying, Bandung",
            capacityM3 = 10000.0,
            currentVolumeM3 = 7800.0,
            percentage = 78,
            waterLevelMeters = 7.8,
            maxHeightMeters = 10.0,
            status = "NORMAL",
            inflowRateLps = 45.2,
            outflowRateLps = 38.5,
            lastUpdated = "09:41 WIB"
        )
    )
    val reservoir: StateFlow<ReservoirData> = _reservoir.asStateFlow()

    private val _distributionPoints = MutableStateFlow(
        listOf(
            DistributionPoint(
                id = "DIST-A",
                name = "Distribusi A",
                zone = "Sektor Utara (Pemukiman & Komersial)",
                pressureBar = 2.8,
                flowRateLps = 12.5,
                status = "NORMAL",
                lastUpdated = "09:41 WIB",
                targetCustomers = 1640,
                pipeDiameterMm = 250,
                historicalPressure = listOf(
                    TrendPoint("05:00", 3.0f),
                    TrendPoint("06:00", 2.9f),
                    TrendPoint("07:00", 2.7f),
                    TrendPoint("08:00", 2.6f),
                    TrendPoint("09:00", 2.8f),
                    TrendPoint("09:41", 2.8f)
                ),
                historicalFlow = listOf(
                    TrendPoint("05:00", 8.4f),
                    TrendPoint("06:00", 11.2f),
                    TrendPoint("07:00", 14.6f),
                    TrendPoint("08:00", 13.8f),
                    TrendPoint("09:00", 12.2f),
                    TrendPoint("09:41", 12.5f)
                )
            ),
            DistributionPoint(
                id = "DIST-B",
                name = "Distribusi B",
                zone = "Sektor Tengah (Perkantoran & Fasum)",
                pressureBar = 2.4,
                flowRateLps = 10.8,
                status = "NORMAL",
                lastUpdated = "09:41 WIB",
                targetCustomers = 1280,
                pipeDiameterMm = 200,
                historicalPressure = listOf(
                    TrendPoint("05:00", 2.6f),
                    TrendPoint("06:00", 2.5f),
                    TrendPoint("07:00", 2.3f),
                    TrendPoint("08:00", 2.2f),
                    TrendPoint("09:00", 2.4f),
                    TrendPoint("09:41", 2.4f)
                ),
                historicalFlow = listOf(
                    TrendPoint("05:00", 6.8f),
                    TrendPoint("06:00", 9.4f),
                    TrendPoint("07:00", 11.8f),
                    TrendPoint("08:00", 11.0f),
                    TrendPoint("09:00", 10.5f),
                    TrendPoint("09:41", 10.8f)
                )
            ),
            DistributionPoint(
                id = "DIST-C",
                name = "Distribusi C",
                zone = "Sektor Selatan (Dataran Tinggi)",
                pressureBar = 1.5,
                flowRateLps = 6.2,
                status = "WASPADA",
                lastUpdated = "09:41 WIB",
                targetCustomers = 950,
                pipeDiameterMm = 150,
                historicalPressure = listOf(
                    TrendPoint("05:00", 2.0f),
                    TrendPoint("06:00", 1.8f),
                    TrendPoint("07:00", 1.4f),
                    TrendPoint("08:00", 1.3f),
                    TrendPoint("09:00", 1.5f),
                    TrendPoint("09:41", 1.5f)
                ),
                historicalFlow = listOf(
                    TrendPoint("05:00", 4.2f),
                    TrendPoint("06:00", 5.8f),
                    TrendPoint("07:00", 7.6f),
                    TrendPoint("08:00", 6.9f),
                    TrendPoint("09:00", 6.0f),
                    TrendPoint("09:41", 6.2f)
                )
            )
        )
    )
    val distributionPoints: StateFlow<List<DistributionPoint>> = _distributionPoints.asStateFlow()

    private val _alerts = MutableStateFlow(
        listOf(
            MonitoringAlert(
                id = "ALT-01",
                title = "Tekanan Rendah",
                message = "Tekanan pada Distribusi C perlu diperiksa (1.5 bar, di bawah ambang standar 2.0 bar).",
                type = "WARNING",
                timestamp = "09:35 WIB",
                location = "Distribusi C"
            ),
            MonitoringAlert(
                id = "ALT-02",
                title = "Level Reservoir Normal",
                message = "Level Reservoir Utama stabil pada 78% kapasitas operasional.",
                type = "INFO",
                timestamp = "09:30 WIB",
                location = "Reservoir Utama"
            )
        )
    )
    val alerts: StateFlow<List<MonitoringAlert>> = _alerts.asStateFlow()

    fun getDistributionPoint(id: String): DistributionPoint? {
        return _distributionPoints.value.find { it.id == id }
    }

    companion object {
        @Volatile
        private var instance: WaterMonitoringRepository? = null

        fun getInstance(): WaterMonitoringRepository {
            return instance ?: synchronized(this) {
                instance ?: WaterMonitoringRepository().also { instance = it }
            }
        }
    }
}
