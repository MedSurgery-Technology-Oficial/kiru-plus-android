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
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class KapibayaChatViewModel @Inject constructor(
    private val repository: KapibayaRepository,
) : ViewModel() {

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
                streamingContent = "",
            )
        }

        viewModelScope.launch {
            val accumulated = StringBuilder()
            runCatching {
                repository.sendMessageStream(conversationId = conversationId, message = text)
                    .collect { chunk ->
                        accumulated.append(chunk)
                        _state.update { it.copy(streamingContent = accumulated.toString()) }
                    }
            }.onSuccess {
                val assistantTurn = KapibayaTurn(
                    role = KapibayaTurn.Role.ASSISTANT,
                    content = accumulated.toString().ifBlank { "…" },
                )
                _state.update {
                    it.copy(
                        turns = it.turns + assistantTurn,
                        isSending = false,
                        streamingContent = null,
                    )
                }
            }.onFailure { err ->
                Timber.w(err, "Kapibaya stream failed")
                _state.update {
                    it.copy(
                        isSending = false,
                        streamingContent = null,
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
    /** null = idle, "" = waiting for first chunk (show TypingBubble), non-blank = streaming text */
    val streamingContent: String? = null,
    @StringRes val errorRes: Int? = null,
)
