package com.medsurgery.kiruplus.feature.settings

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.locale.LocaleApplier
import com.medsurgery.kiruplus.core.prefs.AppLanguage
import com.medsurgery.kiruplus.core.prefs.AppTheme
import com.medsurgery.kiruplus.core.prefs.UserPreferences
import com.medsurgery.kiruplus.core.prefs.UserPreferencesRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val prefsFlow = MutableStateFlow(UserPreferences())

    private val prefsRepo: UserPreferencesRepository = mockk {
        every { preferences } returns prefsFlow
        coEvery { setLanguage(any()) } just Runs
        coEvery { setTheme(any()) } just Runs
        coEvery { setHapticsEnabled(any()) } just Runs
    }

    private val authRepo: AuthRepository = mockk(relaxed = true) {
        coEvery { signOut() } returns Result.success(Unit)
    }

    private val localeApplier: LocaleApplier = mockk(relaxed = true)

    private fun vm() = SettingsViewModel(prefsRepo, authRepo, localeApplier)

    @Test
    fun `setLanguage persists in DataStore and applies locale`() = runTest {
        val viewModel = vm()
        viewModel.setLanguage(AppLanguage.English)

        coVerify(exactly = 1) { prefsRepo.setLanguage(AppLanguage.English) }
        verify(exactly = 1) { localeApplier.apply(AppLanguage.English) }
    }

    @Test
    fun `setTheme persists in DataStore`() = runTest {
        val viewModel = vm()
        viewModel.setTheme(AppTheme.Dark)

        coVerify(exactly = 1) { prefsRepo.setTheme(AppTheme.Dark) }
    }

    @Test
    fun `setHapticsEnabled persists in DataStore`() = runTest {
        val viewModel = vm()
        viewModel.setHapticsEnabled(false)

        coVerify(exactly = 1) { prefsRepo.setHapticsEnabled(false) }
    }

    @Test
    fun `signOut calls authRepo and triggers onComplete`() = runTest {
        val viewModel = vm()
        var completed = false

        viewModel.signOut { completed = true }

        coVerify(exactly = 1) { authRepo.signOut() }
        assertTrue(completed)
    }
}
