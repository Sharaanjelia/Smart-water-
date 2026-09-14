package com.example.data.repository

import android.util.Log
import com.example.data.model.Bill
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk Tagihan Air & Pembayaran
 * Menggunakan Cloud Firestore koleksi "bills".
 */
class BillRepository private constructor() {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore tidak tersedia: ${e.message}")
            null
        }
    }

    // Tagihan aktif saat ini
    private val _activeBill = MutableStateFlow<Bill?>(
        Bill(
            billId = "BILL-202504-001",
            customerId = "1234567890",
            period = "April 2025",
            previousReading = "00981",
            currentReading = "01000",
            usageM3 = 19.0,
            waterTariff = 148300L,
            adminFee = 2500L,
            totalAmount = 150800L,
            status = "BELUM_BAYAR",
            dueDate = "20 April 2025"
        )
    )
    val activeBill: StateFlow<Bill?> = _activeBill.asStateFlow()

    // Riwayat seluruh tagihan
    private val _billsHistory = MutableStateFlow<List<Bill>>(
        listOf(
            Bill(
                billId = "BILL-202504-001",
                customerId = "1234567890",
                period = "April 2025",
                previousReading = "00981",
                currentReading = "01000",
                usageM3 = 19.0,
                waterTariff = 148300L,
                adminFee = 2500L,
                totalAmount = 150800L,
                status = "BELUM_BAYAR",
                dueDate = "20 April 2025"
            ),
            Bill(
                billId = "BILL-202503-082",
                customerId = "1234567890",
                period = "Maret 2025",
                previousReading = "00962",
                currentReading = "00981",
                usageM3 = 19.0,
                waterTariff = 140000L,
                adminFee = 2500L,
                totalAmount = 142500L,
                status = "LUNAS",
                dueDate = "20 Maret 2025",
                paidAt = 1710921600000L,
                paymentMethod = "QRIS",
                transactionRef = "TRX-20250320-991"
            ),
            Bill(
                billId = "BILL-202502-045",
                customerId = "1234567890",
                period = "Februari 2025",
                previousReading = "00945",
                currentReading = "00962",
                usageM3 = 17.0,
                waterTariff = 125000L,
                adminFee = 2500L,
                totalAmount = 127500L,
                status = "LUNAS",
                dueDate = "20 Februari 2025",
                paidAt = 1708329600000L,
                paymentMethod = "BCA Virtual Account",
                transactionRef = "TRX-20250219-412"
            )
        )
    )
    val billsHistory: StateFlow<List<Bill>> = _billsHistory.asStateFlow()

    /**
     * Memproses pembayaran tagihan
     */
    suspend fun payBill(billId: String, paymentMethod: String): Result<Bill> {
        val currentBills = _billsHistory.value
        val targetBill = currentBills.find { it.billId == billId }
            ?: _activeBill.value
            ?: return Result.failure(IllegalArgumentException("Tagihan tidak ditemukan"))

        val updatedBill = targetBill.copy(
            status = "LUNAS",
            paidAt = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            transactionRef = "TRX-${System.currentTimeMillis()}"
        )

        return try {
            firestore?.collection("bills")?.document(billId)?.set(updatedBill)?.await()

            // Update in-memory / local state
            _billsHistory.value = currentBills.map {
                if (it.billId == billId) updatedBill else it
            }
            if (_activeBill.value?.billId == billId) {
                _activeBill.value = updatedBill
            }
            Result.success(updatedBill)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memproses pembayaran: ${e.message}", e)
            _billsHistory.value = currentBills.map {
                if (it.billId == billId) updatedBill else it
            }
            if (_activeBill.value?.billId == billId) {
                _activeBill.value = updatedBill
            }
            Result.success(updatedBill)
        }
    }

    /**
     * Memuat tagihan dari Firestore untuk user tertentu
     */
    fun loadBillsForUser(userId: String) {
        val db = firestore ?: return
        db.collection("bills")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error load bills: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.toObjects(Bill::class.java)
                    _billsHistory.value = list
                    _activeBill.value = list.find { it.status == "BELUM_BAYAR" } ?: list.firstOrNull()
                }
            }
    }

    companion object {
        private const val TAG = "BillRepository"
        @Volatile
        private var instance: BillRepository? = null

        fun getInstance(): BillRepository {
            return instance ?: synchronized(this) {
                instance ?: BillRepository().also { instance = it }
            }
        }
    }
}
