package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Wallet
import com.example.data.model.WithdrawalRequest
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WalletUiState {
    object Idle : WalletUiState
    object Loading : WalletUiState
    data class Success(val wallet: Wallet) : WalletUiState
    data class Error(val message: String) : WalletUiState
}

class WalletViewModel(
    private val repository: WalletRepository = WalletRepository.getInstance()
) : ViewModel() {

    val wallet: StateFlow<Wallet?> = repository.wallet
    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = repository.withdrawalRequests

    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Idle)
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    fun loadWallet(userId: String) {
        if (userId.isBlank()) return
        repository.loadWalletForUser(userId)
    }

    fun requestWithdrawal(
        userId: String,
        amount: Long,
        bankName: String,
        accountNumber: String,
        accountHolder: String,
        onSuccess: (WithdrawalRequest) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uiState.value = WalletUiState.Loading
            val result = repository.requestWithdrawal(userId, amount, bankName, accountNumber, accountHolder)
            result.onSuccess { request ->
                _uiState.value = WalletUiState.Success(repository.wallet.value ?: Wallet(userId = userId))
                onSuccess(request)
            }.onFailure { err ->
                _uiState.value = WalletUiState.Error(err.message ?: "Penarikan saldo gagal")
            }
        }
    }

    fun createWalletIfNeeded(userId: String, merchantName: String = "Operator Smart Water") {
        viewModelScope.launch {
            repository.getOrCreateWallet(userId, merchantName)
        }
    }
}
