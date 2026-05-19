package com.medsurgery.kiruplus.feature.pearls

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.pearls.Pearl
import com.medsurgery.kiruplus.domain.pearls.PearlsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PearlsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sample = listOf(
        Pearl(
            id = 151,
            title = "Abdomen abierto",
            description = "Indicación precisa y cierre temprano.",
            category = "cat.cuidadosCriticosQuirurgicos",
        ),
        Pearl(
            id = 152,
            title = "Hipertensión intraabdominal",
            description = "Medir presión vesical.",
            category = "cat.cuidadosCriticosQuirurgicos",
        ),
    )

    @Test
    fun `init load success populates pearls`() = runTest {
        val repo: PearlsRepository = mockk()
        coEvery { repo.fetchAllPearls() } returns Result.success(sample)

        val viewModel = PearlsViewModel(repo)

        assertEquals(sample, viewModel.state.value.pearls)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorRes)
    }

    @Test
    fun `init load failure surfaces errorRes`() = runTest {
        val repo: PearlsRepository = mockk()
        coEvery { repo.fetchAllPearls() } returns Result.failure(RuntimeException("boom"))

        val viewModel = PearlsViewModel(repo)

        assertTrue(viewModel.state.value.pearls.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.pearls_error_load, viewModel.state.value.errorRes)
    }

    @Test
    fun `load() retry replaces error state with new fetch`() = runTest {
        val repo: PearlsRepository = mockk()
        coEvery { repo.fetchAllPearls() } returnsMany listOf(
            Result.failure(RuntimeException("boom")),
            Result.success(sample),
        )

        val viewModel = PearlsViewModel(repo)
        assertEquals(R.string.pearls_error_load, viewModel.state.value.errorRes)

        viewModel.load()

        assertEquals(sample, viewModel.state.value.pearls)
        assertNull(viewModel.state.value.errorRes)
        coVerify(exactly = 2) { repo.fetchAllPearls() }
    }
}
