package com.medsurgery.kiruplus.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.core.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository,
    private val supabase: SupabaseClient,
) : ViewModel() {

    sealed interface Destination {
        data object Disclaimer : Destination
        data object Login : Destination
        data object Home : Destination
    }

    private val _destination = MutableStateFlow<Destination?>(null)
    val destination: StateFlow<Destination?> = _destination

    init {
        viewModelScope.launch {
            val prefs = prefsRepo.preferences.first()
            val hasSession = runCatching {
                supabase.auth.currentSessionOrNull() != null
            }.getOrDefault(false)

            _destination.value = when {
                !prefs.disclaimerAccepted -> Destination.Disclaimer
                !hasSession -> Destination.Login
                else -> Destination.Home
            }
        }
    }
}
