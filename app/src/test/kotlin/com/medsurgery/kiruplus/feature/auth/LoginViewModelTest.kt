package com.medsurgery.kiruplus.feature.auth

import app.cash.turbine.test
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

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>(relaxed = true) {
        coEvery { sessionState } returns MutableSharedFlow<SessionState>()
    }

    @Test
    fun `submit does nothing when fields are blank`() = runTest {
        val vm = LoginViewModel(repo)
        var callbackInvoked = false

        vm.submit { callbackInvoked = true }

        assertFalse(vm.state.value.isSubmitting)
        assertFalse(callbackInvoked)
    }

    @Test
    fun `submit success calls onSuccess and clears submitting`() = runTest {
        coEvery { repo.signIn(any(), any()) } returns Result.success(Unit)

        val vm = LoginViewModel(repo)
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("Secret123!")
        var success = false

        vm.submit { success = true }

        assertTrue(success)
        assertFalse(vm.state.value.isSubmitting)
        assertNull(vm.state.value.errorRes)
    }

    @Test
    fun `submit failure maps AuthError to localized errorRes`() = runTest {
        coEvery { repo.signIn(any(), any()) } returns Result.failure(AuthError.InvalidCredentials)

        val vm = LoginViewModel(repo)
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("wrong")

        vm.state.test {
            // initial state
            skipItems(1)
            vm.submit {}
            // submitting=true (intermediate emissions may be coalesced by UnconfinedTestDispatcher)
            // final: not submitting + error mapped
            val final = expectMostRecentItem()
            assertFalse(final.isSubmitting)
            assertEquals(R.string.auth_error_invalid_credentials, final.errorRes)
        }
    }

    @Test
    fun `submit failure with non-AuthError falls back to generic`() = runTest {
        coEvery { repo.signIn(any(), any()) } returns Result.failure(RuntimeException("boom"))

        val vm = LoginViewModel(repo)
        vm.onEmailChange("user@example.com")
        vm.onPasswordChange("Secret123!")

        vm.submit {}
        assertEquals(R.string.auth_login_error_generic, vm.state.value.errorRes)
    }

    @Test
    fun `onEmailChange clears error`() = runTest {
        coEvery { repo.signIn(any(), any()) } returns Result.failure(AuthError.InvalidCredentials)

        val vm = LoginViewModel(repo)
        vm.onEmailChange("u@e.com")
        vm.onPasswordChange("x")
        vm.submit {}
        assertEquals(R.string.auth_error_invalid_credentials, vm.state.value.errorRes)

        vm.onEmailChange("new@e.com")
        assertNull(vm.state.value.errorRes)
    }
}
