package com.medsurgery.kiruplus.feature.kcortex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.domain.kcortex.KCortexAnalysis
import com.medsurgery.kiruplus.domain.kcortex.KCortexAnalysisType
import com.medsurgery.kiruplus.domain.kcortex.KCortexRepository
import com.medsurgery.kiruplus.domain.kcortex.KCortexRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI State ─────────────────────────────────────────────────────────────────

data class KCortexUiState(
    val selectedType: KCortexAnalysisType = KCortexAnalysisType.LABORATORIOS,
    val clinicalInput: String = "",
    val phase: KCortexPhase = KCortexPhase.Input,
    val result: KCortexAnalysis? = null,
    val errorMessage: String? = null,
)

sealed interface KCortexPhase {
    object Input : KCortexPhase
    object Analyzing : KCortexPhase
    object Result : KCortexPhase
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class KCortexViewModel @Inject constructor(
    private val repository: KCortexRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(KCortexUiState())
    val uiState: StateFlow<KCortexUiState> = _uiState.asStateFlow()

    fun onTypeSelected(type: KCortexAnalysisType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(clinicalInput = text, errorMessage = null) }
    }

    fun submitAnalysis() {
        val state = _uiState.value
        val input = state.clinicalInput.trim()

        if (input.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa los datos clínicos para analizar.") }
            return
        }

        _uiState.update { it.copy(phase = KCortexPhase.Analyzing, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.analyze(
                KCortexRequest(
                    analysisType = state.selectedType,
                    clinicalInput = input,
                )
            )

            result.fold(
                onSuccess = { analysis ->
                    _uiState.update {
                        it.copy(
                            phase = KCortexPhase.Result,
                            result = analysis,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            phase = KCortexPhase.Input,
                            errorMessage = friendlyError(error),
                        )
                    }
                }
            )
        }
    }

    fun reset() {
        _uiState.update {
            KCortexUiState(selectedType = it.selectedType)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun friendlyError(error: Throwable): String {
        val msg = error.message ?: ""
        return when {
            msg.contains("HTTP 429") -> "K-CORTEX recibió muchas solicitudes. Espera un momento."
            msg.contains("HTTP 5") -> "K-CORTEX está temporalmente fuera de servicio. Intenta más tarde."
            msg.contains("Unable to resolve host") ||
                msg.contains("timeout") ||
                msg.contains("connect") -> "Sin conexión a Internet. Verifica tu red e intenta de nuevo."
            else -> "K-CORTEX no pudo completar el análisis. Intenta de nuevo."
        }
    }
}
