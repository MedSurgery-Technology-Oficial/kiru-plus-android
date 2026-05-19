package com.medsurgery.kiruplus.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.core.prefs.UserPreferences
import com.medsurgery.kiruplus.core.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * App-level ViewModel: expone preferences para que la raíz de Compose
 * (App.kt) pueda recolocar el theme dinámicamente al cambiarlo desde Settings.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    repo: UserPreferencesRepository,
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = repo.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserPreferences(),
        )
}
