package com.medsurgery.kiruplus.feature.auth

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.core.auth.AuthError
import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.auth.SessionState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForgotPasswordViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>(relaxed = true) {
        coEvery { sessionState } returns MutableSharedFlow<SessionState>()
    }

    @Test
    fun `submit with invalid email blocks`() = runTest {
        val vm = ForgotPasswordViewModel(repo)
        vm.onEmailChange("not-email")

        vm.submit()

        assertEquals(R.string.auth_error_invalid_email, vm.state.value.errorRes)
        assertFalse(vm.state.value.sent)
    }

    @Test
    fun `submit success sets sent and clears error`() = runTest {
        coEvery { repo.resetPassword(any()) } returns Result.success(Unit)
        val vm = ForgotPasswordViewModel(repo)
        vm.onEmailChange("user@example.com")

        vm.submit()

        assertTrue(vm.state.value.sent)
        assertNull(vm.state.value.errorRes)
    }

    @Test
    fun `non-rate-limit non-network errors are silent (sent = true) to avoid enumeration`() = runTest {
        coEvery { repo.resetPassword(any()) } returns Result.failure(AuthError.InvalidEmail)
        val vm = ForgotPasswordViewModel(repo)
        vm.onEmailChange("user@example.com")

        vm.submit()

        // Para no filtrar si el email existe en la DB, los errores genéricos del
        // backend se tratan como éxito en UX.
        assertTrue(vm.state.value.sent)
        assertNull(vm.state.value.errorRes)
    }

    @Test
    fun `RateLimited surfaces real error`() = runTest {
        coEvery { repo.resetPassword(any()) } returns Result.failure(AuthError.RateLimited)
        val vm = ForgotPasswordViewModel(repo)
        vm.onEmailChange("user@example.com")

        vm.submit()

        assertFalse(vm.state.value.sent)
        assertEquals(R.string.auth_error_rate_limited, vm.state.value.errorRes)
    }

    @Test
    fun `Network error surfaces real error`() = runTest {
        coEvery { repo.resetPassword(any()) } returns Result.failure(AuthError.Network)
        val vm = ForgotPasswordViewModel(repo)
        vm.onEmailChange("user@example.com")

        vm.submit()

        assertFalse(vm.state.value.sent)
        assertEquals(R.string.auth_error_network, vm.state.value.errorRes)
    }
}
