package com.medsurgery.kiruplus.feature.auth

import android.util.Patterns
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
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorRes = null, sent = false) }
    }

    fun submit() {
        val email = _state.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { it.copy(errorRes = R.string.auth_error_invalid_email) }
            return
        }

        _state.update { it.copy(isSubmitting = true, errorRes = null) }

        viewModelScope.launch {
            authRepository.resetPassword(email)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false, sent = true) }
                }
                .onFailure { e ->
                    // Para no filtrar enumeración de emails, en caso de error tipo
                    // RateLimited o Network mostramos el mensaje real;
                    // para el resto, mostramos éxito (UX recomendada).
                    val mapped = e as? AuthError
                    if (mapped is AuthError.RateLimited || mapped is AuthError.Network) {
                        _state.update {
                            it.copy(isSubmitting = false, errorRes = mapped.messageRes)
                        }
                    } else {
                        _state.update { it.copy(isSubmitting = false, sent = true) }
                    }
                }
        }
    }
}

data class ForgotPasswordUiState(
    val email: String = "",
    val isSubmitting: Boolean = false,
    val sent: Boolean = false,
    @StringRes val errorRes: Int? = null,
)
