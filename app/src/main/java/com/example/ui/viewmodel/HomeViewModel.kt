package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.Bill
import com.example.data.model.MeterReading
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import com.example.data.repository.BillRepository
import com.example.data.repository.MeterRepository
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val meterRepository: MeterRepository = MeterRepository.getInstance(),
    private val billRepository: BillRepository = BillRepository.getInstance()
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
    val currentMeterValue: StateFlow<String> = meterRepository.latestMeterValue
    val activeBill: StateFlow<Bill?> = billRepository.activeBill
    val readingsHistory: StateFlow<List<MeterReading>> = meterRepository.readingsHistory

    fun logout(onLoggedOut: () -> Unit) {
        authRepository.logout()
        onLoggedOut()
    }
}
