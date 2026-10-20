package com.example.data.service

import android.util.Log
import com.example.data.model.XenditInvoiceRequest
import com.example.data.model.XenditInvoiceResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service untuk komunikasi langsung dengan Xendit Payment Gateway API (v2 Invoices)
 */
class XenditService private constructor() {

    companion object {
        private const val TAG = "XenditService"
        private const val BASE_URL = "https://api.xendit.co/v2/invoices"

        // Default Sandbox Key (User dapat meng-update di runtime atau via config)
        // Format sandbox key: xnd_development_...
        var apiKey: String = "xnd_development_mN9rP6Zg8X1jK4wL2qY7vT5bC3nM0sA9dF1hJ3kL5mN7pQ9rS1tU3vW5xY7zA1b"

        @Volatile
        private var instance: XenditService? = null

        fun getInstance(): XenditService {
            return instance ?: synchronized(this) {
                instance ?: XenditService().also { instance = it }
            }
        }
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Membuat Invoice baru di Xendit Checkout
     */
    suspend fun createInvoice(req: XenditInvoiceRequest): Result<XenditInvoiceResponse> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("external_id", req.externalId)
                put("amount", req.amount)
                put("description", req.description)
                put("invoice_duration", req.invoiceDuration)
                put("currency", "IDR")
                put("success_redirect_url", req.successRedirectUrl)
                put("failure_redirect_url", req.failureRedirectUrl)

                val customerObj = JSONObject().apply {
                    put("given_names", req.customerName)
                    if (req.customerEmail.isNotBlank()) put("email", req.customerEmail)
                    if (req.customerPhone.isNotBlank()) put("mobile_number", req.customerPhone)
                }
                put("customer", customerObj)

                val itemsArr = JSONArray().apply {
                    put(JSONObject().apply {
                        put("name", req.description)
                        put("quantity", 1)
                        put("price", req.amount)
                        put("category", "Water Utility")
                    })
                }
                put("items", itemsArr)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toString().toRequestBody(mediaType)
            val credential = Credentials.basic(apiKey.trim(), "")

            val request = Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", credential)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gagal create invoice: code=${response.code}, body=$responseBody")
                return@withContext Result.failure(
                    Exception("Xendit Error (${response.code}): $responseBody")
                )
            }

            val resJson = JSONObject(responseBody)
            val parsed = XenditInvoiceResponse(
                id = resJson.optString("id"),
                externalId = resJson.optString("external_id"),
                status = resJson.optString("status", "PENDING"),
                amount = resJson.optLong("amount", req.amount),
                invoiceUrl = resJson.optString("invoice_url"),
                expiryDate = resJson.optString("expiry_date"),
                paymentMethod = resJson.optString("payment_method"),
                paymentChannel = resJson.optString("payment_channel"),
                paidAt = resJson.optString("paid_at"),
                description = resJson.optString("description")
            )

            Log.d(TAG, "Invoice Xendit berhasil dibuat: ${parsed.id}, url=${parsed.invoiceUrl}")
            Result.success(parsed)
        } catch (e: Exception) {
            Log.e(TAG, "Exception saat createInvoice: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Mengecek status Invoice dari Xendit berdasarkan invoiceId
     */
    suspend fun getInvoice(invoiceId: String): Result<XenditInvoiceResponse> = withContext(Dispatchers.IO) {
        try {
            val credential = Credentials.basic(apiKey.trim(), "")
            val request = Request.Builder()
                .url("$BASE_URL/$invoiceId")
                .addHeader("Authorization", credential)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gagal get invoice: code=${response.code}, body=$responseBody")
                return@withContext Result.failure(
                    Exception("Xendit Error (${response.code}): $responseBody")
                )
            }

            val resJson = JSONObject(responseBody)
            val parsed = XenditInvoiceResponse(
                id = resJson.optString("id"),
                externalId = resJson.optString("external_id"),
                status = resJson.optString("status", "PENDING"),
                amount = resJson.optLong("amount"),
                invoiceUrl = resJson.optString("invoice_url"),
                expiryDate = resJson.optString("expiry_date"),
                paymentMethod = resJson.optString("payment_method"),
                paymentChannel = resJson.optString("payment_channel"),
                paidAt = resJson.optString("paid_at"),
                description = resJson.optString("description")
            )

            Result.success(parsed)
        } catch (e: Exception) {
            Log.e(TAG, "Exception saat getInvoice: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Fitur khusus Sandbox: Mensimulasikan pembayaran lunas di Xendit
     */
    suspend fun simulatePaymentSandbox(invoiceId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Xendit sandbox simulation or check
            val credential = Credentials.basic(apiKey.trim(), "")
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = "{}".toRequestBody(mediaType)

            // Endpoint simulation Xendit sandbox
            val request = Request.Builder()
                .url("https://api.xendit.co/v2/invoices/$invoiceId/simulate_payment")
                .addHeader("Authorization", credential)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Log.d(TAG, "Sandbox payment simulated successfully for invoice $invoiceId")
                Result.success(true)
            } else {
                Log.w(TAG, "Simulate endpoint returned code: ${response.code}")
                // Bisa jadi invoice sudah lunas atau endpoint simulasi perlu parameter tertentu
                Result.success(false)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception saat simulate payment: ${e.message}")
            Result.failure(e)
        }
    }
}
