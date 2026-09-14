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

    private val _scannedMeterValue = MutableStateFlow("01025")
    val scannedMeterValue: StateFlow<String> = _scannedMeterValue.asStateFlow()

    private val _submitState = MutableStateFlow<MeterSubmitState>(MeterSubmitState.Idle)
    val submitState: StateFlow<MeterSubmitState> = _submitState.asStateFlow()

    fun performScan() {
        val recognizedDigits = meterRepository.parseMeterDigitsFromScan()
        _scannedMeterValue.value = recognizedDigits
    }

    fun updateScannedValue(newValue: String) {
        _scannedMeterValue.value = newValue
    }

    fun submitReport(
        meterValue: String,
        notes: String = "",
        onSuccess: (String) -> Unit
    ) {
        val user = authRepository.currentUser.value
        val userId = user?.uid ?: "guest"
        val customerId = user?.customerId ?: "1234567890"
        val meterNumber = user?.meterNumber ?: "12345678"

        viewModelScope.launch {
            _submitState.value = MeterSubmitState.Submitting
            val result = meterRepository.submitMeterReading(
                userId = userId,
                customerId = customerId,
                meterNumber = meterNumber,
                newMeterValue = meterValue,
                notes = notes
            )
            result.onSuccess { reading ->
                _submitState.value = MeterSubmitState.Success(reading)
                onSuccess(reading.meterValue)
            }.onFailure { err ->
                _submitState.value = MeterSubmitState.Error(err.message ?: "Gagal mengirim laporan")
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = MeterSubmitState.Idle
    }
}
