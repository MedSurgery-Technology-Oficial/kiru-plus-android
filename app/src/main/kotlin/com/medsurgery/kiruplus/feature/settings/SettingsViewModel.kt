package com.medsurgery.kiruplus.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.locale.LocaleApplier
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
 * Idioma: persiste via DataStore + aplica via `LocaleApplier` (Android 13+
 * usa LocaleManager nativo; <13 polyfill de AppCompat). El cambio puede
 * requerir restart visual de la activity en algunos casos.
 *
 * Tema: persiste via DataStore; el cambio se aplica inmediato porque KiruTheme
 * está observando `preferences` flow desde la raíz de Compose.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val localeApplier: LocaleApplier,
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
            localeApplier.apply(language)
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

    fun setSentryEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepository.setSentryEnabled(enabled)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }
}
