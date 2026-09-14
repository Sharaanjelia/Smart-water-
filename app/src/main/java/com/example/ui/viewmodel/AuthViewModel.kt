package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(identifier: String, pin: String, onSuccess: () -> Unit) {
        if (identifier.isBlank() || pin.length < 4) {
            _uiState.value = AuthUiState.Error("Masukkan ID Pelanggan dan PIN valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(identifier, pin)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                onSuccess()
            }.onFailure { err ->
                _uiState.value = AuthUiState.Error(err.message ?: "Login gagal. Periksa kembali ID dan PIN Anda.")
            }
        }
    }

    fun register(
        fullName: String,
        meterNumber: String,
        phoneNumber: String,
        address: String,
        pin: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || meterNumber.isBlank() || phoneNumber.isBlank() || pin.length < 4) {
            _uiState.value = AuthUiState.Error("Lengkapi semua kolom formulir")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(
                fullName = fullName,
                meterNumber = meterNumber,
                phoneNumber = phoneNumber,
                address = address,
                pin = pin
            )
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                onSuccess()
            }.onFailure { err ->
                _uiState.value = AuthUiState.Error(err.message ?: "Pendaftaran gagal. Silakan coba lagi.")
            }
        }
    }

    fun loginAsDemo(onSuccess: () -> Unit) {
        val user = authRepository.loginAsDemo()
        _uiState.value = AuthUiState.Success(user)
        onSuccess()
    }

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        _uiState.value = AuthUiState.Idle
        onLoggedOut()
    }

    fun clearState() {
        _uiState.value = AuthUiState.Idle
    }
}
