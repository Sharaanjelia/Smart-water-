package com.example.data.model

/**
 * Model Pengguna Smart Water yang disimpan di Cloud Firestore (koleksi "users")
 * zone: zona distribusi air pelanggan (DIST-A = "Sektor Utara", DIST-B = "Sektor Tengah", DIST-C = "Sektor Selatan")
 */
data class User(
    val uid: String = "",
    val customerId: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "customer", // "customer" atau "admin"
    val meterNumber: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val zone: String = "DIST-A", // DIST-A, DIST-B, DIST-C — zona distribusi air pelanggan
    val createdAt: Long = System.currentTimeMillis()
) {
    // Alias untuk kompatibilitas mapping data
    val name: String get() = fullName
    val meterId: String get() = meterNumber
    val phone: String get() = phoneNumber
}
