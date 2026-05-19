package com.medsurgery.kiruplus.feature.store

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.store.StoreProduct
import com.medsurgery.kiruplus.domain.store.StoreRepository
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

class StoreViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sampleProducts = listOf(
        StoreProduct(
            id = "scrub-antifluido-v1",
            title = "Uniforme Quirúrgico Antifluido",
            description = "Tela repelente",
            price = "1200.00",
            currency = "MXN",
            imageUrl = null,
            category = "scrubs",
            permalink = "https://medsurgery.shop/producto/uniforme-quirurgico-antifluido",
            stockStatus = "instock",
            stockQuantity = 50,
            isVisible = true,
            sortOrder = 1,
        ),
    )

    @Test
    fun `init load populates products on success`() = runTest {
        val repo: StoreRepository = mockk()
        coEvery { repo.fetchVisibleProducts() } returns Result.success(sampleProducts)

        val viewModel = StoreViewModel(repo)

        assertEquals(sampleProducts, viewModel.state.value.products)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorRes)
        coVerify(exactly = 1) { repo.fetchVisibleProducts() }
    }

    @Test
    fun `init load failure surfaces errorRes`() = runTest {
        val repo: StoreRepository = mockk()
        coEvery { repo.fetchVisibleProducts() } returns Result.failure(RuntimeException("boom"))

        val viewModel = StoreViewModel(repo)

        assertTrue(viewModel.state.value.products.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.store_error_load, viewModel.state.value.errorRes)
    }

    @Test
    fun `load() can be invoked again as retry and replaces state`() = runTest {
        val repo: StoreRepository = mockk()
        coEvery { repo.fetchVisibleProducts() } returnsMany listOf(
            Result.failure(RuntimeException("boom")),
            Result.success(sampleProducts),
        )

        val viewModel = StoreViewModel(repo)
        assertEquals(R.string.store_error_load, viewModel.state.value.errorRes)

        viewModel.load()

        assertEquals(sampleProducts, viewModel.state.value.products)
        assertNull(viewModel.state.value.errorRes)
        coVerify(exactly = 2) { repo.fetchVisibleProducts() }
    }
}
