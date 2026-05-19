package com.medsurgery.kiruplus.feature.academy

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.academy.ContentItem
import com.medsurgery.kiruplus.domain.academy.ContentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AcademyViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sample = listOf(
        ContentItem(
            id = "62c75c05-1553-4317-b434-7c9f80001c35",
            title = "ATLS Chapter 1",
            content = "Initial assessment...",
            category = "atls_chapter",
            specialty = "General Surgery",
        ),
    )

    @Test
    fun `init load success populates lessons`() = runTest {
        val repo: ContentRepository = mockk()
        coEvery { repo.fetchLessons() } returns Result.success(sample)

        val viewModel = AcademyViewModel(repo)

        assertEquals(sample, viewModel.state.value.lessons)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorRes)
    }

    @Test
    fun `init load failure surfaces errorRes`() = runTest {
        val repo: ContentRepository = mockk()
        coEvery { repo.fetchLessons() } returns Result.failure(RuntimeException("boom"))

        val viewModel = AcademyViewModel(repo)

        assertTrue(viewModel.state.value.lessons.isEmpty())
        assertEquals(R.string.academy_error_load, viewModel.state.value.errorRes)
    }
}
