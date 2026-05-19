package com.medsurgery.kiruplus.feature.kapibaya

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.kapibaya.KapibayaRepository
import com.medsurgery.kiruplus.domain.kapibaya.KapibayaTurn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class KapibayaChatViewModel @Inject constructor(
    private val repository: KapibayaRepository,
) : ViewModel() {

    /**
     * Una conversación nueva por mount del VM (sin persistencia local hoy).
     * E15.1 agregará persistencia en DataStore para continuidad entre sesiones.
     */
    private val conversationId: String = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(KapibayaChatUiState())
    val state: StateFlow<KapibayaChatUiState> = _state.asStateFlow()

    fun onInputChange(value: String) {
        _state.update { it.copy(input = value, errorRes = null) }
    }

    fun send() {
        val current = _state.value
        val text = current.input.trim()
        if (text.isEmpty() || current.isSending) return

        val userTurn = KapibayaTurn(role = KapibayaTurn.Role.USER, content = text)
        _state.update {
            it.copy(
                turns = it.turns + userTurn,
                input = "",
                isSending = true,
                errorRes = null,
            )
        }

        viewModelScope.launch {
            repository.sendMessage(conversationId = conversationId, message = text)
                .onSuccess { assistantContent ->
                    val assistantTurn = KapibayaTurn(
                        role = KapibayaTurn.Role.ASSISTANT,
                        content = assistantContent.ifBlank { "…" },
                    )
                    _state.update {
                        it.copy(
                            turns = it.turns + assistantTurn,
                            isSending = false,
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isSending = false,
                            errorRes = R.string.kapibaya_error_send,
                        )
                    }
                }
        }
    }
}

data class KapibayaChatUiState(
    val turns: List<KapibayaTurn> = emptyList(),
    val input: String = "",
    val isSending: Boolean = false,
    @StringRes val errorRes: Int? = null,
)
