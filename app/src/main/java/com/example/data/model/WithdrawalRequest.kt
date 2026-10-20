package com.example.data.model

/**
 * Permintaan tarik saldo dari wallet operator ke rekening bank.
 * Koleksi Firestore: withdrawals/{requestId}
 */
data class WithdrawalRequest(
    val id: String = "",
    val userId: String = "",
    val amount: Long = 0L,
    val bankName: String = "",
    val accountNumber: String = "",
    val accountHolder: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val notes: String = ""
)
