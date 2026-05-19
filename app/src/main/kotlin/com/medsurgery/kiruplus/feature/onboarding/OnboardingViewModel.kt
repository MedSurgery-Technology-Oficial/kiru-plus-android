package com.medsurgery.kiruplus.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.core.prefs.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository,
) : ViewModel() {

    fun acceptDisclaimer(onDone: () -> Unit) {
        viewModelScope.launch {
            prefsRepo.setDisclaimerAccepted(true)
            onDone()
        }
    }
}
