package com.medsurgery.kiruplus.feature.settings

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

/**
 * Solicita exportación GDPR Art. 15 / LFPDPPP Art. 23.
 *
 * No requiere acknowledge porque NO es destructivo (a diferencia de
 * AccountDeletion). El user recibe el archivo por email — el botón
 * "Solicitar" llama directo al Edge Function `process_data_export`.
 */
@HiltViewModel
class DataExportViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DataExportUiState())
    val state: StateFlow<DataExportUiState> = _state.asStateFlow()

    fun submit() {
        if (_state.value.isSubmitting || _state.value.requested) return
        _state.update { it.copy(isSubmitting = true, errorRes = null) }

        viewModelScope.launch {
            authRepository.requestDataExport()
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false, requested = true) }
                }
                .onFailure { e ->
                    val res = (e as? AuthError)?.messageRes ?: R.string.error_generic
                    _state.update { it.copy(isSubmitting = false, errorRes = res) }
                }
        }
    }
}

data class DataExportUiState(
    val isSubmitting: Boolean = false,
    val requested: Boolean = false,
    @StringRes val errorRes: Int? = null,
)
