package com.medsurgery.kiruplus.feature.settings

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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DataExportViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repo: AuthRepository = mockk(relaxed = true) {
        coEvery { sessionState } returns MutableSharedFlow<SessionState>()
    }

    private fun vm() = DataExportViewModel(repo)

    @Test
    fun `submit success flips requested true and clears error`() = runTest {
        coEvery { repo.requestDataExport() } returns Result.success(Unit)
        val viewModel = vm()

        viewModel.submit()

        assertTrue(viewModel.state.value.requested)
        assertFalse(viewModel.state.value.isSubmitting)
        assertNull(viewModel.state.value.errorRes)
        coVerify(exactly = 1) { repo.requestDataExport() }
    }

    @Test
    fun `submit AuthError maps to localized errorRes`() = runTest {
        coEvery { repo.requestDataExport() } returns Result.failure(AuthError.Network)
        val viewModel = vm()

        viewModel.submit()

        assertFalse(viewModel.state.value.requested)
        assertEquals(R.string.auth_error_network, viewModel.state.value.errorRes)
    }

    @Test
    fun `submit non-AuthError throws fall back to error_generic`() = runTest {
        coEvery { repo.requestDataExport() } returns Result.failure(RuntimeException("boom"))
        val viewModel = vm()

        viewModel.submit()

        assertFalse(viewModel.state.value.requested)
        assertEquals(R.string.error_generic, viewModel.state.value.errorRes)
    }

    @Test
    fun `repeat submit after success is a no-op (idempotent UI)`() = runTest {
        coEvery { repo.requestDataExport() } returns Result.success(Unit)
        val viewModel = vm()

        viewModel.submit()
        viewModel.submit() // segunda llamada con `requested = true`

        coVerify(exactly = 1) { repo.requestDataExport() }
    }
}
