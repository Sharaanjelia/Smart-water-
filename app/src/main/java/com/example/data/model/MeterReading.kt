package com.example.data.model

/**
 * Model Pembacaan Meter Air Smart Water di Cloud Firestore (koleksi "meter_readings")
 */
data class MeterReading(
    val readingId: String = "",
    val userId: String = "",
    val customerId: String = "",
    val meterNumber: String = "",
    val meterValue: String = "00000",
    val meterDeltaM3: Double = 0.0,
    val photoUrl: String = "",
    val status: String = "TERVERIFIKASI", // "MENUNGGU", "TERVERIFIKASI", "DITOLAK"
    val period: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
