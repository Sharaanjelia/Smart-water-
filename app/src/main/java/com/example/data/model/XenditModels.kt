package com.example.data.model

/**
 * Data class untuk request pembuatan Invoice Xendit
 */
data class XenditInvoiceRequest(
    val externalId: String,
    val amount: Long,
    val description: String,
    val customerName: String = "Pelanggan Smart Water",
    val customerEmail: String = "pelanggan@smartwater.id",
    val customerPhone: String = "+6281234567890",
    val invoiceDuration: Long = 86400, // 24 jam dalam detik
    val successRedirectUrl: String = "smartwater://payment-return?status=success",
    val failureRedirectUrl: String = "smartwater://payment-return?status=failed"
)

/**
 * Response model dari Xendit Invoice API
 */
data class XenditInvoiceResponse(
    val id: String,
    val externalId: String,
    val status: String, // "PENDING", "PAID", "SETTLED", "EXPIRED"
    val amount: Long,
    val invoiceUrl: String,
    val expiryDate: String? = null,
    val paymentMethod: String? = null,
    val paymentChannel: String? = null,
    val paidAt: String? = null,
    val description: String? = null
) {
    val isPaid: Boolean
        get() = status.equals("PAID", ignoreCase = true) || status.equals("SETTLED", ignoreCase = true)

    val isPending: Boolean
        get() = status.equals("PENDING", ignoreCase = true)

    val isExpired: Boolean
        get() = status.equals("EXPIRED", ignoreCase = true)
}
