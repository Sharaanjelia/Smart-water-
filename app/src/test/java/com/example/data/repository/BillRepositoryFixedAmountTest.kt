package com.example.data.repository

import com.example.data.model.Bill
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BillRepositoryFixedAmountTest {

    @Test
    fun `payment amount must match bill total exactly`() = runBlocking {
        val repo = BillRepository.getInstance()
        val bill = Bill(
            billId = "TEST-BILL-001",
            userId = "u-01",
            customerId = "CUST-001",
            period = "Mei 2026",
            totalAmount = 100000L,
            status = "BELUM_BAYAR",
            paymentStatus = "unpaid"
        )

        repo.createBill(bill)

        val exactMatch = repo.payBill(bill.billId, "QRIS", bill.totalAmount)
        assertTrue(exactMatch.isSuccess)
        assertEquals("LUNAS", exactMatch.getOrThrow().status)

        val mismatch = repo.payBill(bill.billId, "QRIS", 99999L)
        assertTrue(mismatch.isFailure)
    }
}
