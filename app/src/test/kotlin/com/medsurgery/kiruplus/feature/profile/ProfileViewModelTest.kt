package com.medsurgery.kiruplus.feature.profile

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.profile.Profile
import com.medsurgery.kiruplus.domain.profile.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val sample = Profile(
        id = "00000000-0000-0000-0000-000000000001",
        fullName = "Dr. Huerta",
        specialty = "General Surgery",
        role = "attending",
    )

    @Test
    fun `init load success populates profile`() = runTest {
        val repo: ProfileRepository = mockk()
        coEvery { repo.fetchCurrentProfile() } returns Result.success(sample)

        val viewModel = ProfileViewModel(repo)

        assertEquals(sample, viewModel.state.value.profile)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorRes)
    }

    @Test
    fun `init load failure surfaces errorRes`() = runTest {
        val repo: ProfileRepository = mockk()
        coEvery { repo.fetchCurrentProfile() } returns Result.failure(RuntimeException("boom"))

        val viewModel = ProfileViewModel(repo)

        assertNull(viewModel.state.value.profile)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(R.string.profile_error_load, viewModel.state.value.errorRes)
    }

    @Test
    fun `load() retry replaces error state with new fetch`() = runTest {
        val repo: ProfileRepository = mockk()
        coEvery { repo.fetchCurrentProfile() } returnsMany listOf(
            Result.failure(RuntimeException("boom")),
            Result.success(sample),
        )

        val viewModel = ProfileViewModel(repo)
        assertEquals(R.string.profile_error_load, viewModel.state.value.errorRes)

        viewModel.load()

        assertEquals(sample, viewModel.state.value.profile)
        assertNull(viewModel.state.value.errorRes)
        coVerify(exactly = 2) { repo.fetchCurrentProfile() }
    }

    @Test
    fun `null profile (no row) sets profile = null without error`() = runTest {
        val repo: ProfileRepository = mockk()
        coEvery { repo.fetchCurrentProfile() } returns Result.success(null)

        val viewModel = ProfileViewModel(repo)

        assertNull(viewModel.state.value.profile)
        assertNull(viewModel.state.value.errorRes)
        assertFalse(viewModel.state.value.isLoading)
    }
}
