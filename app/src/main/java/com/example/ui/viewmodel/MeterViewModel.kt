package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.MeterReading
import com.example.data.repository.AuthRepository
import com.example.data.repository.MeterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MeterSubmitState {
    object Idle : MeterSubmitState
    object Submitting : MeterSubmitState
    data class Success(val reading: MeterReading) : MeterSubmitState
    data class Error(val message: String) : MeterSubmitState
}

class MeterViewModel(
    private val meterRepository: MeterRepository = MeterRepository.getInstance(),
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    val currentMeterValue: StateFlow<String> = meterRepository.latestMeterValue

    private val _scannedMeterValue = MutableStateFlow("00000")
    val scannedMeterValue: StateFlow<String> = _scannedMeterValue.asStateFlow()

    private val _capturedPhotoPath = MutableStateFlow<String?>(null)
    val capturedPhotoPath: StateFlow<String?> = _capturedPhotoPath.asStateFlow()

    private val _submitState = MutableStateFlow<MeterSubmitState>(MeterSubmitState.Idle)
    val submitState: StateFlow<MeterSubmitState> = _submitState.asStateFlow()

    fun performScan() {
        val recognizedDigits = meterRepository.parseMeterDigitsFromScan()
        _scannedMeterValue.value = recognizedDigits
    }

    fun setCapturedPhotoPath(path: String?) {
        _capturedPhotoPath.value = path
    }

    fun updateScannedValue(newValue: String) {
        _scannedMeterValue.value = newValue
    }

    fun submitReport(
        meterValue: String,
        previousValue: String = currentMeterValue.value,
        notes: String = "",
        photoPath: String? = _capturedPhotoPath.value,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val user = authRepository.currentUser.value
        val userId = user?.uid ?: "guest"
        val customerId = user?.customerId ?: "1234567890"
        val meterNumber = user?.meterNumber ?: "12345678"
        val zone = user?.zone ?: "DIST-A"

        viewModelScope.launch {
            _submitState.value = MeterSubmitState.Submitting
            val result = meterRepository.submitMeterReading(
                userId = userId,
                customerId = customerId,
                meterNumber = meterNumber,
                newMeterValue = meterValue,
                previousMeterValue = previousValue,
                zone = zone,
                photoFilePath = photoPath,
                notes = notes
            )
            result.onSuccess { reading ->
                _submitState.value = MeterSubmitState.Success(reading)
                onSuccess(reading.meterValue)
            }.onFailure { err ->
                val msg = err.message ?: "Gagal mengirim laporan"
                _submitState.value = MeterSubmitState.Error(msg)
                onError(msg)
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = MeterSubmitState.Idle
    }
}
