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

class AccountDeletionViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repo: AuthRepository = mockk(relaxed = true) {
        coEvery { sessionState } returns MutableSharedFlow<SessionState>()
    }

    private fun vm() = AccountDeletionViewModel(repo)

    @Test
    fun `submit without acknowledge is a no-op`() = runTest {
        val viewModel = vm()
        viewModel.submit()

        assertFalse(viewModel.state.value.submitted)
        coVerify(exactly = 0) { repo.requestAccountDeletion() }
    }

    @Test
    fun `submit success calls requestAccountDeletion + signOut and flips submitted`() = runTest {
        coEvery { repo.requestAccountDeletion() } returns Result.success(Unit)
        coEvery { repo.signOut() } returns Result.success(Unit)
        val viewModel = vm()
        viewModel.onAcknowledgeChange(true)

        viewModel.submit()

        assertTrue(viewModel.state.value.submitted)
        coVerify(exactly = 1) { repo.requestAccountDeletion() }
        coVerify(exactly = 1) { repo.signOut() }
    }

    @Test
    fun `submit AuthError surfaces errorRes and does not flip submitted`() = runTest {
        coEvery { repo.requestAccountDeletion() } returns Result.failure(AuthError.Network)
        val viewModel = vm()
        viewModel.onAcknowledgeChange(true)

        viewModel.submit()

        assertFalse(viewModel.state.value.submitted)
        assertEquals(R.string.auth_error_network, viewModel.state.value.errorRes)
        coVerify(exactly = 0) { repo.signOut() } // no signOut si la deletion falla
    }

    @Test
    fun `submit non-AuthError throws fall back to error_generic`() = runTest {
        coEvery { repo.requestAccountDeletion() } returns Result.failure(RuntimeException("boom"))
        val viewModel = vm()
        viewModel.onAcknowledgeChange(true)

        viewModel.submit()

        assertFalse(viewModel.state.value.submitted)
        assertEquals(R.string.error_generic, viewModel.state.value.errorRes)
    }
}
