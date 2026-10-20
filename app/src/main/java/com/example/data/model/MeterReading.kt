package com.example.data.model

/**
 * Model Pembacaan Meter Air Smart Water di Cloud Firestore (koleksi "meter_readings")
 */
data class MeterReading(
    val readingId: String = "",
    val recordId: String = "",
    val userId: String = "",
    val customerId: String = "",
    val meterNumber: String = "",
    val meterId: String = "",
    val previousReading: Double = 0.0,
    val currentReading: Double = 0.0,
    val usage: Double = 0.0,
    val meterValue: String = "00000",
    val meterDeltaM3: Double = 0.0,
    val ocrValue: String = "",
    val photoUrl: String = "",
    val status: String = "MENUNGGU", // "MENUNGGU", "TERVERIFIKASI", "DITOLAK"
    val period: String = "",
    val zone: String = "DIST-A",
    val notes: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    // Getter praktis untuk id dan angka bacaan
    val effectiveRecordId: String get() = recordId.ifEmpty { readingId }
    val effectiveMeterId: String get() = meterId.ifEmpty { meterNumber }
    val effectiveUsage: Double get() = if (usage > 0.0) usage else meterDeltaM3
}
