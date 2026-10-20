package com.example.data.model

/**
 * Model data untuk monitoring reservoir dan jaringan distribusi air PDAM Smart Water.
 * Seluruh data saat ini merupakan DATA SIMULASI (Mock Data) untuk keperluan prototipe.
 */
data class TrendPoint(
    val time: String,
    val value: Float
)

data class ReservoirData(
    val id: String = "RES-01",
    val name: String = "Reservoir Utama",
    val location: String = "Instalasi Pengolahan Air (IPA) Pusat",
    val capacityM3: Double = 10000.0,
    val currentVolumeM3: Double = 7800.0,
    val percentage: Int = 78,
    val waterLevelMeters: Double = 7.8,
    val maxHeightMeters: Double = 10.0,
    val status: String = "NORMAL", // NORMAL, WASPADA, KRITIS
    val inflowRateLps: Double = 45.2,
    val outflowRateLps: Double = 38.5,
    val lastUpdated: String = "09:41 WIB"
)

data class DistributionPoint(
    val id: String,
    val name: String,
    val zone: String,
    val pressureBar: Double,
    val flowRateLps: Double,
    val status: String, // NORMAL, WASPADA, KRITIS
    val lastUpdated: String = "09:41 WIB",
    val targetCustomers: Int = 1250,
    val pipeDiameterMm: Int = 200,
    val historicalPressure: List<TrendPoint> = emptyList(),
    val historicalFlow: List<TrendPoint> = emptyList()
)

data class MonitoringAlert(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // WARNING, INFO, CRITICAL
    val timestamp: String,
    val location: String
)
