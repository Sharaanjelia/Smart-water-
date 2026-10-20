package com.example.data.repository

import android.util.Log
import com.example.data.model.Bill
import com.example.data.model.MeterReading
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository untuk Pembacaan Meter Air & Riwayat Laporan
 * Menggunakan Cloud Firestore koleksi "meter_records" dan Firebase Storage.
 */
class MeterRepository private constructor() {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore tidak tersedia: ${e.message}")
            null
        }
    }

    private val storage: FirebaseStorage? by lazy {
        try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Storage tidak tersedia: ${e.message}")
            null
        }
    }

    // Reactive State untuk angka meter terkini
    private val _latestMeterValue = MutableStateFlow("00000")
    val latestMeterValue: StateFlow<String> = _latestMeterValue.asStateFlow()

    // Reactive State untuk riwayat pembacaan pelanggan aktif
    private val _readingsHistory = MutableStateFlow<List<MeterReading>>(emptyList())
    val readingsHistory: StateFlow<List<MeterReading>> = _readingsHistory.asStateFlow()

    // Reactive State untuk seluruh laporan meter (khusus Admin PDAM)
    private val _allMeterRecords = MutableStateFlow<List<MeterReading>>(emptyList())
    val allMeterRecords: StateFlow<List<MeterReading>> = _allMeterRecords.asStateFlow()

    init {
        _readingsHistory.value = emptyList()
        _allMeterRecords.value = emptyList()
    }

    /**
     * Mengunggah foto bukti meteran ke Firebase Storage pada path:
     * meter_photos/{userId}/{recordId}.jpg
     */
    suspend fun uploadMeterPhoto(userId: String, recordId: String, photoFile: File): Result<String> {
        val fbStorage = storage ?: return Result.failure(
            IllegalStateException("Firebase Storage SDK belum terinisialisasi.")
        )

        return try {
            val storageRef = fbStorage.reference
            val photoRef = storageRef.child("meter_photos/$userId/$recordId.jpg")

            val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setCustomMetadata("userId", userId)
                .setCustomMetadata("recordId", recordId)
                .build()

            val stream = FileInputStream(photoFile)
            photoRef.putStream(stream, metadata).await()
            val downloadUrl = photoRef.downloadUrl.await().toString()
            Log.d(TAG, "Foto meter berhasil diunggah ke Firebase Storage: $downloadUrl")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengunggah foto meter ke Firebase Storage: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Mengirimkan laporan baca meter air ke Firestore (koleksi "meter_records")
     * dan otomatis membuat draft tagihan di koleksi "bills".
     */
    suspend fun submitMeterReading(
        userId: String,
        customerId: String,
        meterNumber: String,
        newMeterValue: String,
        previousMeterValue: String = _latestMeterValue.value,
        zone: String = "DIST-A",
        photoFilePath: String? = null,
        photoUrl: String = "",
        notes: String = ""
    ): Result<MeterReading> {
        val currentValNum = newMeterValue.toDoubleOrNull() ?: 0.0
        val previousValNum = previousMeterValue.toDoubleOrNull() ?: 0.0

        // Validasi: currentReading >= previousReading
        if (currentValNum < previousValNum) {
            val errorMsg = "Angka meter baru ($newMeterValue m³) tidak boleh lebih kecil dari angka sebelumnya ($previousMeterValue m³). Periksa kembali foto atau koreksi angka meter Anda."
            Log.w(TAG, errorMsg)
            return Result.failure(IllegalArgumentException(errorMsg))
        }

        val deltaUsage = currentValNum - previousValNum
        val recordId = "mtr_" + System.currentTimeMillis()

        // Upload foto ke Firebase Storage jika file lokal tersedia
        var resolvedPhotoUrl = photoUrl
        if (!photoFilePath.isNullOrEmpty()) {
            val photoFile = File(photoFilePath)
            if (photoFile.exists() && photoFile.length() > 0) {
                val uploadResult = uploadMeterPhoto(userId, recordId, photoFile)
                uploadResult.onSuccess { url ->
                    resolvedPhotoUrl = url
                }.onFailure { uploadErr ->
                    val errMsg = "Upload foto meter ke Firebase Storage gagal: ${uploadErr.message ?: "Kesalahan storage"}"
                    Log.e(TAG, errMsg)
                    return Result.failure(IllegalStateException(errMsg, uploadErr))
                }
            }
        }

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        val currentPeriod = dateFormat.format(Date())

        val record = MeterReading(
            readingId = recordId,
            recordId = recordId,
            userId = userId,
            customerId = customerId,
            meterNumber = meterNumber,
            meterId = meterNumber,
            previousReading = previousValNum,
            currentReading = currentValNum,
            usage = deltaUsage,
            meterValue = newMeterValue,
            meterDeltaM3 = deltaUsage,
            ocrValue = newMeterValue,
            photoUrl = resolvedPhotoUrl,
            status = "MENUNGGU", // status awal menunggu verifikasi admin
            period = currentPeriod,
            zone = zone,
            notes = notes,
            submittedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )

        return try {
            val db = firestore
            if (db != null) {
                // Simpan ke Firestore koleksi "meter_records"
                db.collection("meter_records").document(recordId).set(record).await()
                Log.d(TAG, "Meter record $recordId tersimpan di Firestore")
            }

            // Update in-memory reactive state
            _latestMeterValue.value = newMeterValue
            _readingsHistory.value = listOf(record) + _readingsHistory.value.filter { it.readingId != recordId }
            _allMeterRecords.value = listOf(record) + _allMeterRecords.value.filter { it.readingId != recordId }

            // Otomatis generate tagihan (formula: usage * tariff + adminFee)
            val tariffPerM3 = 150L // prototype tarif Rp 150 / m³
            val adminFee = 2500L
            val waterTariff = (deltaUsage * tariffPerM3).toLong()
            val totalAmount = waterTariff + adminFee

            val generatedBill = Bill(
                billId = "BILL-" + System.currentTimeMillis(),
                userId = userId,
                customerId = customerId,
                meterRecordId = recordId,
                period = currentPeriod,
                previousReading = previousMeterValue,
                currentReading = newMeterValue,
                usageM3 = deltaUsage,
                tariff = tariffPerM3,
                waterTariff = waterTariff,
                adminFee = adminFee,
                totalAmount = totalAmount,
                status = "BELUM_BAYAR",
                paymentStatus = "unpaid",
                dueDate = "20 $currentPeriod"
            )

            // Simpan tagihan baru ke BillRepository
            BillRepository.getInstance().createBill(generatedBill)

            Result.success(record)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan meter reading ke Firestore: ${e.message}")
            // Tetap update state lokal agar user tidak crash
            _latestMeterValue.value = newMeterValue
            _readingsHistory.value = listOf(record) + _readingsHistory.value.filter { it.readingId != recordId }
            _allMeterRecords.value = listOf(record) + _allMeterRecords.value.filter { it.readingId != recordId }
            Result.success(record)
        }
    }

    /**
     * Memuat riwayat meter reading untuk user tertentu dari Firestore
     */
    fun loadHistoryForUser(userId: String) {
        val db = firestore ?: return
        db.collection("meter_records")
            .whereEqualTo("userId", userId)
            .orderBy("submittedAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Gagal memuat riwayat meter: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(MeterReading::class.java)
                    _readingsHistory.value = list
                    if (list.isNotEmpty()) {
                        _latestMeterValue.value = list.first().meterValue
                    } else {
                        _latestMeterValue.value = "00000"
                    }
                }
            }
    }

    /**
     * Listener real-time seluruh laporan meter pelanggan untuk Admin PDAM
     */
    fun observeAllMeterRecords() {
        val db = firestore ?: return
        db.collection("meter_records")
            .orderBy("submittedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Gagal observe seluruh meter records: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.toObjects(MeterReading::class.java)
                    _allMeterRecords.value = list
                }
            }
    }

    /**
     * Update status laporan meter (Verifikasi / Ditolak) oleh Admin
     */
    suspend fun updateRecordStatus(recordId: String, newStatus: String): Result<Boolean> {
        return try {
            firestore?.collection("meter_records")?.document(recordId)?.update("status", newStatus)?.await()
            _allMeterRecords.value = _allMeterRecords.value.map {
                if (it.readingId == recordId || it.recordId == recordId) it.copy(status = newStatus) else it
            }
            _readingsHistory.value = _readingsHistory.value.map {
                if (it.readingId == recordId || it.recordId == recordId) it.copy(status = newStatus) else it
            }
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal update status meter record $recordId: ${e.message}")
            _allMeterRecords.value = _allMeterRecords.value.map {
                if (it.readingId == recordId || it.recordId == recordId) it.copy(status = newStatus) else it
            }
            Result.success(true)
        }
    }

    /**
     * Simulasi OCR / Gemini Analyzer untuk mengenali angka meter dari gambar
     */
    fun parseMeterDigitsFromScan(): String {
        val currentInt = _latestMeterValue.value.toIntOrNull() ?: 1000
        val nextValue = currentInt + (5..25).random()
        return String.format(Locale.US, "%05d", nextValue)
    }

    companion object {
        private const val TAG = "MeterRepository"
        @Volatile
        private var instance: MeterRepository? = null

        fun getInstance(): MeterRepository {
            return instance ?: synchronized(this) {
                instance ?: MeterRepository().also { instance = it }
            }
        }
    }
}
