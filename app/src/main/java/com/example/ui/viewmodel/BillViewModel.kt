package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Bill
import com.example.data.repository.BillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PaymentState {
    object Idle : PaymentState
    object Processing : PaymentState
    data class Success(val bill: Bill) : PaymentState
    data class Error(val message: String) : PaymentState
}

class BillViewModel(
    private val billRepository: BillRepository = BillRepository.getInstance()
) : ViewModel() {

    val activeBill: StateFlow<Bill?> = billRepository.activeBill
    val billsHistory: StateFlow<List<Bill>> = billRepository.billsHistory

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    fun payBill(billId: String, paymentMethod: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            val result = billRepository.payBill(billId, paymentMethod)
            result.onSuccess { bill ->
                _paymentState.value = PaymentState.Success(bill)
                onSuccess()
            }.onFailure { err ->
                _paymentState.value = PaymentState.Error(err.message ?: "Pembayaran gagal diproses")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
}
