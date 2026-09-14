package com.example.data.model

/**
 * Model Pengguna Smart Water yang disimpan di Cloud Firestore (koleksi "users")
 */
data class User(
    val uid: String = "",
    val customerId: String = "",
    val fullName: String = "",
    val meterNumber: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
