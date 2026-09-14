package com.example.data.model

/**
 * Model Notifikasi Pengguna Smart Water (koleksi "notifications")
 */
data class AppNotification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "INFO", // "TAGIHAN", "METER", "INFO", "PEMBAYARAN"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
