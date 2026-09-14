package com.example.data.repository

import android.util.Log
import com.example.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk Autentikasi Pengguna & Profil Pelanggan
 * Menggunakan Firebase Auth & Cloud Firestore dengan fallback offline otomatis.
 */
class AuthRepository private constructor() {

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth not available, running in local/demo mode: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore not available, running in local/demo mode: ${e.message}")
            null
        }
    }

    // Reactive current user state
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Cek apakah ada session aktif
        auth?.currentUser?.let { fbUser ->
            fetchUserProfile(fbUser.uid)
        }
    }

    /**
     * Mendaftar akun baru
     */
    suspend fun register(
        fullName: String,
        meterNumber: String,
        phoneNumber: String,
        address: String,
        pin: String
    ): Result<User> {
        val customerId = meterNumber.trim().ifEmpty { 
            "00" + (100000000..999999999).random().toString() 
        }
        val dummyEmail = "${phoneNumber.replace("+", "").trim()}@smartwater.local"
        val password = "PIN_${pin}_SW"

        return try {
            val fbAuth = auth
            val db = firestore

            if (fbAuth != null && db != null) {
                val authResult = fbAuth.createUserWithEmailAndPassword(dummyEmail, password).await()
                val uid = authResult.user?.uid ?: throw IllegalStateException("UID tidak ditemukan")

                val newUser = User(
                    uid = uid,
                    customerId = customerId,
                    fullName = fullName.trim(),
                    meterNumber = meterNumber.trim(),
                    phoneNumber = phoneNumber.trim(),
                    address = address.trim(),
                    createdAt = System.currentTimeMillis()
                )

                // Simpan profil ke Firestore
                db.collection("users").document(uid).set(newUser).await()
                _currentUser.value = newUser
                Result.success(newUser)
            } else {
                // Mode Lokal / Demo fallback
                val fallbackUser = User(
                    uid = "local_" + System.currentTimeMillis(),
                    customerId = customerId,
                    fullName = fullName.trim(),
                    meterNumber = meterNumber.trim(),
                    phoneNumber = phoneNumber.trim(),
                    address = address.trim()
                )
                _currentUser.value = fallbackUser
                Result.success(fallbackUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registrasi gagal: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Masuk menggunakan ID Pelanggan / Nomor HP dan PIN
     */
    suspend fun login(identifier: String, pin: String): Result<User> {
        val cleanIdentifier = identifier.replace("+", "").trim()
        val dummyEmail = "$cleanIdentifier@smartwater.local"
        val password = "PIN_${pin}_SW"

        return try {
            val fbAuth = auth
            val db = firestore

            if (fbAuth != null && db != null) {
                val authResult = fbAuth.signInWithEmailAndPassword(dummyEmail, password).await()
                val uid = authResult.user?.uid ?: throw IllegalStateException("Pengguna tidak ditemukan")

                val doc = db.collection("users").document(uid).get().await()
                val user = doc.toObject(User::class.java) ?: User(
                    uid = uid,
                    customerId = identifier,
                    fullName = "Pelanggan",
                    meterNumber = identifier
                )
                _currentUser.value = user
                Result.success(user)
            } else {
                // Fallback login lokal
                val localUser = User(
                    uid = "demo_user_1",
                    customerId = identifier,
                    fullName = if (identifier == "1234567890") "Budi Santoso" else "Pelanggan Smart Water",
                    meterNumber = identifier,
                    phoneNumber = "08123456789",
                    address = "Jl. Merdeka No. 45, Bandung"
                )
                _currentUser.value = localUser
                Result.success(localUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login gagal: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Masuk sebagai akun Demo
     */
    fun loginAsDemo(): User {
        val demoUser = User(
            uid = "demo_uid_budi",
            customerId = "1234567890",
            fullName = "Budi Santoso",
            meterNumber = "12345678",
            phoneNumber = "081234567890",
            address = "Jl. Sukapura No. 12, Bandung",
            createdAt = System.currentTimeMillis()
        )
        _currentUser.value = demoUser
        return demoUser
    }

    /**
     * Ambil data profil dari Firestore
     */
    fun fetchUserProfile(uid: String) {
        firestore?.collection("users")?.document(uid)?.get()
            ?.addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    _currentUser.value = doc.toObject(User::class.java)
                }
            }
            ?.addOnFailureListener {
                Log.w(TAG, "Gagal mengambil profil: ${it.message}")
            }
    }

    /**
     * Keluar dari akun
     */
    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Error saat signOut: ${e.message}")
        }
        _currentUser.value = null
    }

    companion object {
        private const val TAG = "AuthRepository"
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }
}
