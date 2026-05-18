package com.medsurgery.kiruplus.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.core.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun submit(onSuccess: () -> Unit) {
        val current = _state.value
        if (current.email.isBlank() || current.password.isBlank()) return
        _state.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            val result = authRepository.signIn(current.email.trim(), current.password)
            result
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = e.message,
                        )
                    }
                }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)
