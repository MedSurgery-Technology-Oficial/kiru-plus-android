package com.medsurgery.kiruplus.feature.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.core.auth.AuthError
import com.medsurgery.kiruplus.core.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDeletionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountDeletionUiState())
    val state: StateFlow<AccountDeletionUiState> = _state.asStateFlow()

    fun onAcknowledgeChange(acknowledged: Boolean) {
        _state.update { it.copy(acknowledged = acknowledged, errorRes = null) }
    }

    fun submit() {
        if (!_state.value.acknowledged) return
        _state.update { it.copy(isSubmitting = true, errorRes = null) }

        viewModelScope.launch {
            authRepository.requestAccountDeletion()
                .onSuccess {
                    // Tras solicitar deletion, cerramos sesión local;
                    // el Edge Function se encarga del soft-delete + 48h grace.
                    authRepository.signOut()
                    _state.update { it.copy(isSubmitting = false, submitted = true) }
                }
                .onFailure { e ->
                    val res = (e as? AuthError)?.messageRes ?: R.string.error_generic
                    _state.update { it.copy(isSubmitting = false, errorRes = res) }
                }
        }
    }
}

data class AccountDeletionUiState(
    val acknowledged: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    @StringRes val errorRes: Int? = null,
)
