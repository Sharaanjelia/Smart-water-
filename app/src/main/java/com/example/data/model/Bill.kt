package com.example.data.model

/**
 * Model Tagihan Air Smart Water di Cloud Firestore (koleksi "bills")
 */
data class Bill(
    val billId: String = "",
    val userId: String = "",
    val customerId: String = "",
    val meterRecordId: String = "",
    val period: String = "", // e.g. "April 2025"
    val previousReading: String = "00000",
    val currentReading: String = "00000",
    val usageM3: Double = 0.0,
    val tariff: Long = 150L, // Nilai simulasi Rp 150/m3
    val waterTariff: Long = 0L,
    val adminFee: Long = 2500L,
    val totalAmount: Long = 0L,
    val status: String = "BELUM_BAYAR", // "BELUM_BAYAR", "LUNAS", "MENUNGGU_VERIFIKASI"
    val paymentStatus: String = "unpaid", // "unpaid", "pending", "paid"
    val dueDate: String = "",
    val paidAt: Long? = null,
    val paymentMethod: String = "",
    val transactionRef: String = "",
    val invoiceUrl: String = "",
    val xenditInvoiceId: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val billingPeriod: String get() = period
    val usage: Double get() = usageM3
    val isPaid: Boolean get() = status == "LUNAS" || paymentStatus == "paid"
}
