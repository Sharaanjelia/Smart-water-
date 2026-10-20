package com.example.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryAdminLoginTest {
    @Test
    fun adminIdentifierIsRecognized() {
        assertTrue(AuthRepository.isExplicitAdminIdentifier("admin"))
        assertTrue(AuthRepository.isExplicitAdminIdentifier("ADMIN"))
        assertTrue(AuthRepository.isExplicitAdminIdentifier("04"))
        assertTrue(AuthRepository.isExplicitAdminIdentifier("petugas"))
    }

    @Test
    fun adminFallbackCreatesAdminUser() {
        val user = AuthRepository.getInstance().loginAsAdmin("123456")

        assertEquals("admin", user.role)
        assertEquals("admin@smartwater.local", user.email)
        assertEquals("ADM-004", user.customerId)
    }
}
