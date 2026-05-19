package com.medsurgery.kiruplus.feature.settings

import androidx.core.os.LocaleListCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.prefs.AppLanguage
import com.medsurgery.kiruplus.core.prefs.AppTheme
import com.medsurgery.kiruplus.core.prefs.UserPreferences
import com.medsurgery.kiruplus.core.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel — espejo conceptual de iOS SettingsView.
 *
 * Idioma: persiste via DataStore + aplica `AppCompatDelegate.setApplicationLocales`
 * (per-app language API; nativo en Android 13+, polyfill en versiones anteriores
 * via `xml/locales_config.xml`). El cambio puede requerir restart visual de la
 * activity en algunos casos — se avisa via hint string.
 *
 * Tema: persiste via DataStore; el cambio se aplica inmediato porque KiruTheme
 * está observando `preferences` flow desde la raíz de Compose.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = prefsRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = UserPreferences(),
        )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            prefsRepository.setLanguage(language)
            applyAppLocale(language)
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            prefsRepository.setTheme(theme)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setHapticsEnabled(enabled)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }

    private fun applyAppLocale(language: AppLanguage) {
        val locales = when (language) {
            AppLanguage.System -> LocaleListCompat.getEmptyLocaleList()
            else -> LocaleListCompat.forLanguageTags(language.tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
