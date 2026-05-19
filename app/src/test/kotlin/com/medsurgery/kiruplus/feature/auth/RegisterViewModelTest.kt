package com.medsurgery.kiruplus.feature.auth

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.core.auth.AuthError
import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.auth.SessionState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>(relaxed = true) {
        coEvery { sessionState } returns MutableSharedFlow<SessionState>()
    }

    private fun vm() = RegisterViewModel(repo)

    @Test
    fun `submit with invalid email blocks before hitting repo`() = runTest {
        val vm = vm()
        vm.onEmailChange("not-an-email")
        vm.onPasswordChange("Strong1!")
        vm.onConfirmChange("Strong1!")

        vm.submit()

        assertEquals(R.string.auth_error_invalid_email, vm.state.value.errorRes)
        coVerify(exactly = 0) { repo.signUp(any(), any()) }
    }

    @Test
    fun `submit with weak password blocks before hitting repo`() = runTest {
        val vm = vm()
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("password") // no upper / no digit / no symbol
        vm.onConfirmChange("password")

        vm.submit()

        assertEquals(R.string.auth_error_weak_password, vm.state.value.errorRes)
        coVerify(exactly = 0) { repo.signUp(any(), any()) }
    }

    @Test
    fun `submit with mismatched confirm blocks before hitting repo`() = runTest {
        val vm = vm()
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("Strong1!")
        vm.onConfirmChange("Mismatched1!")

        vm.submit()

        assertEquals(R.string.auth_error_passwords_dont_match, vm.state.value.errorRes)
        coVerify(exactly = 0) { repo.signUp(any(), any()) }
    }

    @Test
    fun `submit success flips registered true`() = runTest {
        coEvery { repo.signUp(any(), any()) } returns Result.success(Unit)
        val vm = vm()
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("Strong1!")
        vm.onConfirmChange("Strong1!")

        vm.submit()

        assertTrue(vm.state.value.registered)
        assertFalse(vm.state.value.isSubmitting)
        coVerify(exactly = 1) { repo.signUp("user@example.com", "Strong1!") }
    }

    @Test
    fun `submit backend AuthError maps to localized errorRes`() = runTest {
        coEvery { repo.signUp(any(), any()) } returns Result.failure(AuthError.EmailAlreadyInUse)
        val vm = vm()
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("Strong1!")
        vm.onConfirmChange("Strong1!")

        vm.submit()

        assertEquals(R.string.auth_error_email_in_use, vm.state.value.errorRes)
        assertFalse(vm.state.value.registered)
    }
}
