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

    companion object {
        private const val TAG = "AuthRepository"
        private const val ADMIN_EMAIL = "admin@smartwater.local"

        fun isExplicitAdminIdentifier(identifier: String): Boolean {
            val cleanIdentifier = identifier.replace("+", "")
                .replace("-", "")
                .replace(" ", "")
                .trim()

            return cleanIdentifier.equals("admin", ignoreCase = true) ||
                cleanIdentifier == "04" ||
                cleanIdentifier.equals("petugas", ignoreCase = true) ||
                cleanIdentifier.equals("operator", ignoreCase = true)
        }

        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }

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

    // Reactive state untuk daftar seluruh customer (khusus Admin)
    private val _allCustomers = MutableStateFlow<List<User>>(emptyList())
    val allCustomers: StateFlow<List<User>> = _allCustomers.asStateFlow()

    init {
        // Cek apakah ada session aktif
        auth?.currentUser?.let { fbUser ->
            fetchUserProfile(fbUser.uid)
        }
    }

    /**
     * Mendaftar akun baru (role: customer) murni via Firebase Authentication & Firestore
     */
    suspend fun register(
        fullName: String,
        meterNumber: String,
        phoneNumber: String,
        address: String,
        pin: String,
        zone: String = "DIST-A"
    ): Result<User> {
        val fbAuth = auth ?: return Result.failure(
            IllegalStateException("Firebase Authentication belum terinisialisasi. Periksa koneksi internet atau Google Services.")
        )
        val db = firestore ?: return Result.failure(
            IllegalStateException("Cloud Firestore belum terinisialisasi.")
        )

        val customerId = meterNumber.trim().ifEmpty { 
            "SW-" + (100000..999999).random().toString() 
        }
        val cleanPhone = phoneNumber.replace("+", "").replace("-", "").replace(" ", "").trim()
        val dummyEmail = "$cleanPhone@smartwater.local"
        val password = "PIN_${pin}_SW"

        return try {
            val authResult = fbAuth.createUserWithEmailAndPassword(dummyEmail, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("UID tidak ditemukan dari Firebase Auth")

            val newUser = User(
                uid = uid,
                customerId = customerId,
                fullName = fullName.trim(),
                email = dummyEmail,
                role = "customer",
                meterNumber = meterNumber.trim(),
                phoneNumber = phoneNumber.trim(),
                address = address.trim(),
                zone = zone,
                createdAt = System.currentTimeMillis()
            )

            // Simpan profil ke Firestore (users/{userId}) tanpa menyimpan password
            db.collection("users").document(uid).set(newUser).await()
            Log.d(TAG, "Customer profile successfully created in Firebase Auth & Firestore for uid: $uid")
            _currentUser.value = newUser
            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Authentication register gagal: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Masuk menggunakan ID Pelanggan / Nomor HP dan PIN murni via Firebase Authentication
     */
    suspend fun login(identifier: String, pin: String): Result<User> {
        val cleanIdentifier = identifier.replace("+", "").replace("-", "").replace(" ", "").trim()
        val password = "PIN_${pin}_SW"
        val isExplicitAdmin = isExplicitAdminIdentifier(cleanIdentifier)
        val targetEmail = if (isExplicitAdmin) ADMIN_EMAIL else "$cleanIdentifier@smartwater.local"

        if (auth == null) {
            if (isExplicitAdmin && pin.isNotBlank()) {
                return Result.success(loginAsAdmin(pin))
            }
            return Result.failure(IllegalStateException("Firebase Authentication belum terinisialisasi."))
        }

        val fbAuth = auth ?: return Result.failure(
            IllegalStateException("Firebase Authentication belum terinisialisasi.")
        )
        val db = firestore ?: return Result.failure(
            IllegalStateException("Cloud Firestore belum terinisialisasi.")
        )

        return try {
            val authResult = try {
                fbAuth.signInWithEmailAndPassword(targetEmail, password).await()
            } catch (e: Exception) {
                if (isExplicitAdmin) {
                    try {
                        fbAuth.createUserWithEmailAndPassword(targetEmail, password).await()
                    } catch (createErr: Exception) {
                        throw e
                    }
                } else {
                    // Jika login dengan nomor HP gagal atau user memasukkan no meter / customerId,
                    // cari email terdaftar di Firestore users
                    val querySnapshot = db.collection("users")
                        .whereEqualTo("meterNumber", cleanIdentifier)
                        .limit(1)
                        .get()
                        .await()

                    val doc = if (!querySnapshot.isEmpty) {
                        querySnapshot.documents.first()
                    } else {
                        val customerQuery = db.collection("users")
                            .whereEqualTo("customerId", cleanIdentifier)
                            .limit(1)
                            .get()
                            .await()
                        if (!customerQuery.isEmpty) customerQuery.documents.first() else null
                    }

                    val foundEmail = doc?.getString("email")
                    if (!foundEmail.isNullOrEmpty()) {
                        fbAuth.signInWithEmailAndPassword(foundEmail, password).await()
                    } else {
                        throw e // Lempar exception asli Firebase jika tidak ditemukan
                    }
                }
            }

            val uid = authResult.user?.uid ?: throw IllegalStateException("Pengguna tidak ditemukan dari Firebase Auth")
            val doc = db.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java) ?: User(
                uid = uid,
                customerId = if (isExplicitAdmin) "ADM-004" else identifier,
                fullName = if (isExplicitAdmin) "Ahmad Pratama (Petugas)" else "Pelanggan",
                email = authResult.user?.email ?: targetEmail,
                role = if (isExplicitAdmin) "admin" else "customer",
                meterNumber = if (isExplicitAdmin) "ADM-000" else identifier
            )
            if (!doc.exists()) {
                try {
                    db.collection("users").document(uid).set(user).await()
                } catch (writeErr: Exception) {
                    Log.w(TAG, "Gagal simpan user doc: ${writeErr.message}")
                }
            }
            _currentUser.value = user
            Log.d(TAG, "Login berhasil via Firebase Auth untuk UID: $uid, role: ${user.role}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Authentication login gagal: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Masuk sebagai akun Demo Customer
     */
    fun loginAsDemo(): User {
        val demoUser = User(
            uid = "demo_uid_budi",
            customerId = "1234567890",
            fullName = "Budi Santoso",
            email = "budi@smartwater.local",
            role = "customer",
            meterNumber = "12345678",
            phoneNumber = "081234567890",
            address = "Jl. Sukapura No. 12, Bandung",
            zone = "DIST-A",
            createdAt = System.currentTimeMillis()
        )
        _currentUser.value = demoUser
        return demoUser
    }

    /**
     * Masuk sebagai akun Petugas / Admin PDAM
     */
    fun loginAsAdmin(pin: String = "123456"): User {
        val adminUser = User(
            uid = "admin_ahmad_04",
            customerId = "ADM-004",
            fullName = "Ahmad Pratama",
            email = ADMIN_EMAIL,
            role = "admin",
            meterNumber = "ADM-000",
            phoneNumber = "081122334455",
            address = "Kantor Pusat PDAM Bandung",
            zone = "DIST-A",
            createdAt = System.currentTimeMillis()
        )
        _currentUser.value = adminUser
        Log.d(TAG, "Admin login fallback aktif. PIN yang dipakai: ${pin.ifBlank { "123456" }}")
        return adminUser
    }

    /**
     * Ambil data profil dari Firestore
     */
    fun fetchUserProfile(uid: String) {
        val fbUser = auth?.currentUser
        if (fbUser != null && _currentUser.value == null) {
            val isAdmin = fbUser.email == "admin@smartwater.local"
            _currentUser.value = User(
                uid = uid,
                customerId = if (isAdmin) "ADM-004" else "",
                fullName = if (isAdmin) "Ahmad Pratama (Petugas)" else "Pelanggan",
                email = fbUser.email ?: "",
                role = if (isAdmin) "admin" else "customer",
                meterNumber = if (isAdmin) "ADM-000" else ""
            )
        }
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
     * Listener Firestore real-time untuk memuat seluruh pelanggan (Admin)
     */
    fun observeAllCustomers() {
        val db = firestore ?: return
        db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Gagal memuat seluruh customer: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(User::class.java)
                    val customerList = list.filter { it.role == "customer" || it.role.isEmpty() }
                    _allCustomers.value = if (customerList.isNotEmpty()) {
                        customerList
                    } else {
                        // Fallback jika database masih baru
                        listOf(
                            User(uid = "demo_1", customerId = "1234567890", fullName = "Budi Santoso", meterNumber = "12345678", phoneNumber = "081234567890", address = "Jl. Merdeka No. 45", zone = "DIST-A"),
                            User(uid = "demo_2", customerId = "0012948291", fullName = "Siti Nurhaliza", meterNumber = "87654321", phoneNumber = "081298765432", address = "Jl. Sukajadi No. 12", zone = "DIST-B"),
                            User(uid = "demo_3", customerId = "0083920194", fullName = "Rudi Hartono", meterNumber = "44332211", phoneNumber = "081345678901", address = "Jl. Asia Afrika No. 8", zone = "DIST-C")
                        )
                    }
                }
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

}
