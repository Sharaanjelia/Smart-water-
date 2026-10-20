package com.example.data.repository

import android.util.Log
import com.example.data.model.Wallet
import com.example.data.model.WithdrawalRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class WalletRepository private constructor() {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore wallet tidak tersedia: ${e.message}")
            null
        }
    }

    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet.asStateFlow()

    private val _withdrawalRequests = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = _withdrawalRequests.asStateFlow()

    private val _allWithdrawalRequests = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val allWithdrawalRequests: StateFlow<List<WithdrawalRequest>> = _allWithdrawalRequests.asStateFlow()

    fun loadWalletForUser(userId: String) {
        if (userId.isBlank()) {
            _wallet.value = null
            return
        }

        val db = firestore ?: run {
            _wallet.value = Wallet(userId = userId)
            return
        }

        db.collection("wallets")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Gagal memuat wallet: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    _wallet.value = snapshot.toObject(Wallet::class.java) ?: Wallet(userId = userId)
                } else {
                    _wallet.value = Wallet(userId = userId, merchantName = "Operator Smart Water")
                }
            }
    }

    fun loadWithdrawalRequestsForUser(userId: String) {
        if (userId.isBlank()) return

        val db = firestore ?: return
        db.collection("withdrawals")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Gagal memuat withdrawal: ${error.message}")
                    return@addSnapshotListener
                }
                _withdrawalRequests.value = snapshot?.toObjects(WithdrawalRequest::class.java) ?: emptyList()
            }
    }

    fun loadAllWithdrawalRequests() {
        val db = firestore ?: return
        db.collection("withdrawals")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Gagal memuat semua withdrawal: ${error.message}")
                    return@addSnapshotListener
                }
                _allWithdrawalRequests.value = snapshot?.toObjects(WithdrawalRequest::class.java) ?: emptyList()
            }
    }

    suspend fun getOrCreateWallet(userId: String, merchantName: String = "Operator Smart Water"): Result<Wallet> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("userId tidak boleh kosong"))
        }

        val db = firestore ?: return Result.success(Wallet(userId = userId, merchantName = merchantName))

        return try {
            val docRef = db.collection("wallets").document(userId)
            val snapshot = docRef.get().await()

            val existing = if (snapshot.exists()) {
                snapshot.toObject(Wallet::class.java)
            } else null

            val wallet = existing ?: Wallet(
                userId = userId,
                merchantName = merchantName,
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(wallet).await()
            _wallet.value = wallet
            Result.success(wallet)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyiapkan wallet: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun creditWallet(
        userId: String,
        amount: Long,
        note: String = "Pembayaran tagihan",
        referenceId: String = ""
    ): Result<Wallet> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("userId wallet tidak boleh kosong"))
        }
        if (amount <= 0L) {
            return Result.failure(IllegalArgumentException("Jumlah nominal harus lebih dari 0"))
        }

        val db = firestore ?: run {
            val current = _wallet.value ?: Wallet(userId = userId)
            val updated = current.copy(
                balance = current.balance + amount,
                updatedAt = System.currentTimeMillis()
            )
            _wallet.value = updated
            return Result.success(updated)
        }

        return try {
            val docRef = db.collection("wallets").document(userId)
            val snapshot = docRef.get().await()
            val currentWallet = snapshot.toObject(Wallet::class.java) ?: Wallet(userId = userId)
            val updatedWallet = currentWallet.copy(
                balance = currentWallet.balance + amount,
                updatedAt = System.currentTimeMillis()
            )

            docRef.set(updatedWallet).await()
            _wallet.value = updatedWallet

            val txId = referenceId.ifBlank { "TX-${System.currentTimeMillis()}" }
            db.collection("wallet_transactions")
                .document(txId)
                .set(mapOf(
                    "id" to txId,
                    "userId" to userId,
                    "type" to "credit",
                    "amount" to amount,
                    "note" to note,
                    "createdAt" to System.currentTimeMillis()
                )).await()

            Result.success(updatedWallet)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal kredit wallet: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun requestWithdrawal(
        userId: String,
        amount: Long,
        bankName: String,
        accountNumber: String,
        accountHolder: String
    ): Result<WithdrawalRequest> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("userId tidak boleh kosong"))
        }
        if (amount <= 0L) {
            return Result.failure(IllegalArgumentException("Nominal penarikan harus lebih dari 0"))
        }
        if (bankName.isBlank() || accountNumber.isBlank() || accountHolder.isBlank()) {
            return Result.failure(IllegalArgumentException("Data bank belum lengkap"))
        }

        val db = firestore ?: run {
            val request = WithdrawalRequest(
                id = "WD-${System.currentTimeMillis()}",
                userId = userId,
                amount = amount,
                bankName = bankName,
                accountNumber = accountNumber,
                accountHolder = accountHolder,
                status = "pending"
            )
            val list = _withdrawalRequests.value.toMutableList().apply { add(0, request) }
            _withdrawalRequests.value = list
            return Result.success(request)
        }

        return try {
            val wallet = _wallet.value ?: (db.collection("wallets").document(userId).get().await()
                .toObject(Wallet::class.java) ?: Wallet(userId = userId))

            if (wallet.balance < amount) {
                return Result.failure(IllegalStateException("Saldo tidak mencukupi untuk penarikan"))
            }

            val request = WithdrawalRequest(
                id = "WD-${System.currentTimeMillis()}",
                userId = userId,
                amount = amount,
                bankName = bankName,
                accountNumber = accountNumber,
                accountHolder = accountHolder,
                status = "pending",
                createdAt = System.currentTimeMillis()
            )

            db.collection("withdrawals").document(request.id).set(request).await()
            val list = _withdrawalRequests.value.toMutableList().apply { add(0, request) }
            _withdrawalRequests.value = list
            Result.success(request)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal membuat withdrawal: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateWithdrawalStatus(requestId: String, status: String, notes: String = ""): Result<WithdrawalRequest> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore belum siap"))

        return try {
            val snapshot = db.collection("withdrawals").document(requestId).get().await()
            val request = snapshot.toObject(WithdrawalRequest::class.java)
                ?: return Result.failure(IllegalArgumentException("Request tidak ditemukan"))

            val walletDoc = db.collection("wallets").document(request.userId).get().await()
            val wallet = walletDoc.toObject(Wallet::class.java) ?: Wallet(userId = request.userId)

            val adjustedWallet = if (status.equals("approved", ignoreCase = true) || status.equals("sent", ignoreCase = true)) {
                wallet.copy(
                    balance = (wallet.balance - request.amount).coerceAtLeast(0L),
                    updatedAt = System.currentTimeMillis()
                )
            } else wallet

            val updated = request.copy(
                status = status,
                processedAt = System.currentTimeMillis(),
                notes = notes
            )

            db.collection("withdrawals").document(requestId).set(updated).await()
            db.collection("wallets").document(request.userId).set(adjustedWallet).await()

            _withdrawalRequests.value = _withdrawalRequests.value.map {
                if (it.id == requestId) updated else it
            }
            _allWithdrawalRequests.value = _allWithdrawalRequests.value.map {
                if (it.id == requestId) updated else it
            }
            _wallet.value = adjustedWallet
            Result.success(updated)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal update withdrawal: ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "WalletRepository"

        @Volatile
        private var instance: WalletRepository? = null

        fun getInstance(): WalletRepository {
            return instance ?: synchronized(this) {
                instance ?: WalletRepository().also { instance = it }
            }
        }
    }
}
