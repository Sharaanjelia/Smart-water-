package com.example.data.model

/**
 * Saldo merchant/operator yang akan ditarik ke rekening bank.
 * Koleksi Firestore: wallets/{userId}
 */
data class Wallet(
    val userId: String = "",
    val balance: Long = 0L,
    val currency: String = "IDR",
    val merchantName: String = "Operator Smart Water",
    val bankName: String = "",
    val bankAccountNumber: String = "",
    val bankAccountHolder: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
