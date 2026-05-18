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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorRes = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorRes = null) }
    }

    fun onConfirmChange(confirm: String) {
        _state.update { it.copy(passwordConfirm = confirm, errorRes = null) }
    }

    fun submit() {
        val current = _state.value
        val email = current.email.trim()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _state.update { it.copy(errorRes = R.string.auth_error_invalid_email) }
                return
            }
            !isStrongPassword(current.password) -> {
                _state.update { it.copy(errorRes = R.string.auth_error_weak_password) }
                return
            }
            current.password != current.passwordConfirm -> {
                _state.update { it.copy(errorRes = R.string.auth_error_passwords_dont_match) }
                return
            }
        }

        _state.update { it.copy(isSubmitting = true, errorRes = null) }

        viewModelScope.launch {
            authRepository.signUp(email, current.password)
                .onSuccess {
                    _state.update {
                        it.copy(isSubmitting = false, registered = true)
                    }
                }
                .onFailure { e ->
                    val res = (e as? AuthError)?.messageRes ?: R.string.error_generic
                    _state.update { it.copy(isSubmitting = false, errorRes = res) }
                }
        }
    }

    // Política de Supabase Auth: ≥8 chars + lowercase + uppercase + digit + symbol.
    private fun isStrongPassword(pw: String): Boolean {
        if (pw.length < MIN_PASSWORD_LENGTH) return false
        if (!pw.any { it.isLowerCase() }) return false
        if (!pw.any { it.isUpperCase() }) return false
        if (!pw.any { it.isDigit() }) return false
        if (!pw.any { !it.isLetterOrDigit() }) return false
        return true
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isSubmitting: Boolean = false,
    val registered: Boolean = false,
    @StringRes val errorRes: Int? = null,
)
