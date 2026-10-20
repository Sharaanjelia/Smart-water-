package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Bill
import com.example.data.model.XenditInvoiceResponse
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

sealed interface XenditState {
    object Idle : XenditState
    data class Loading(val message: String = "Membuat Invoice Xendit...") : XenditState
    data class InvoiceCreated(val invoice: XenditInvoiceResponse) : XenditState
    data class Paid(val bill: Bill) : XenditState
    data class Error(val message: String) : XenditState
}

class BillViewModel(
    private val billRepository: BillRepository = BillRepository.getInstance()
) : ViewModel() {

    val activeBill: StateFlow<Bill?> = billRepository.activeBill
    val billsHistory: StateFlow<List<Bill>> = billRepository.billsHistory

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _xenditState = MutableStateFlow<XenditState>(XenditState.Idle)
    val xenditState: StateFlow<XenditState> = _xenditState.asStateFlow()

    fun payBill(billId: String, paymentMethod: String, expectedAmount: Long = -1L, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Processing
            val result = billRepository.payBill(billId, paymentMethod, expectedAmount)
            result.onSuccess { bill ->
                _paymentState.value = PaymentState.Success(bill)
                onSuccess()
            }.onFailure { err ->
                _paymentState.value = PaymentState.Error(err.message ?: "Pembayaran gagal diproses")
            }
        }
    }

    /**
     * Membuat Invoice Xendit dan memicu pembukaan halaman Checkout
     */
    fun createXenditInvoice(
        billId: String,
        customerName: String = "Pelanggan Smart Water",
        customerEmail: String = "pelanggan@smartwater.id",
        customerPhone: String = "+6281234567890",
        onInvoiceReady: (invoiceUrl: String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _xenditState.value = XenditState.Loading("Menghubungkan ke Xendit...")
            val result = billRepository.createXenditInvoice(
                billId = billId,
                customerName = customerName,
                customerEmail = customerEmail,
                customerPhone = customerPhone
            )
            result.onSuccess { invoice ->
                _xenditState.value = XenditState.InvoiceCreated(invoice)
                onInvoiceReady(invoice.invoiceUrl)
            }.onFailure { err ->
                _xenditState.value = XenditState.Error(err.message ?: "Gagal membuat invoice Xendit")
            }
        }
    }

    /**
     * Memeriksa status invoice langsung ke Xendit API
     */
    fun checkXenditInvoiceStatus(
        billId: String,
        invoiceId: String,
        onPaid: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _xenditState.value = XenditState.Loading("Memverifikasi pembayaran ke Xendit...")
            val result = billRepository.checkXenditInvoiceStatus(billId, invoiceId)
            result.onSuccess { bill ->
                if (bill.isPaid) {
                    _xenditState.value = XenditState.Paid(bill)
                    _paymentState.value = PaymentState.Success(bill)
                    onPaid()
                } else {
                    _xenditState.value = XenditState.Idle
                }
            }.onFailure { err ->
                _xenditState.value = XenditState.Error(err.message ?: "Gagal memeriksa status pembayaran")
            }
        }
    }

    /**
     * Mensimulasikan pembayaran lunas di Sandbox Xendit
     */
    fun simulateSandboxPayment(
        billId: String,
        invoiceId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _xenditState.value = XenditState.Loading("Mensimulasikan pembayaran Sandbox...")
            val result = billRepository.simulateXenditPayment(billId, invoiceId)
            result.onSuccess { bill ->
                _xenditState.value = XenditState.Paid(bill)
                _paymentState.value = PaymentState.Success(bill)
                onSuccess()
            }.onFailure { err ->
                _xenditState.value = XenditState.Error(err.message ?: "Gagal simulasi Sandbox")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
        _xenditState.value = XenditState.Idle
    }
}
