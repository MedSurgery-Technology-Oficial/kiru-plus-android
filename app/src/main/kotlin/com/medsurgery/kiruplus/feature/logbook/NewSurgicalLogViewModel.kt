package com.medsurgery.kiruplus.feature.logbook

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.logbook.LogbookRepository
import com.medsurgery.kiruplus.domain.logbook.NewLogInput
import com.medsurgery.kiruplus.domain.logbook.Procedure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class NewSurgicalLogViewModel @Inject constructor(
    private val repository: LogbookRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        NewLogUiState(
            procedureDate = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date,
        ),
    )
    val state: StateFlow<NewLogUiState> = _state.asStateFlow()

    init {
        loadProcedures()
    }

    fun loadProcedures() {
        _state.update { it.copy(isLoadingProcedures = true) }
        viewModelScope.launch {
            repository.fetchProcedures()
                .onSuccess { list ->
                    _state.update { it.copy(isLoadingProcedures = false, procedures = list) }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoadingProcedures = false,
                            errorRes = R.string.logbook_error_procedures,
                        )
                    }
                }
        }
    }

    fun selectProcedure(p: Procedure) {
        _state.update { it.copy(selectedProcedure = p, errorRes = null) }
    }

    fun setProcedureDate(date: LocalDate) {
        _state.update { it.copy(procedureDate = date) }
    }

    fun setComplexity(value: String) {
        _state.update { it.copy(complexity = value) }
    }

    fun setOutcome(value: String) {
        _state.update { it.copy(outcome = value) }
    }

    fun setNotes(value: String) {
        _state.update { it.copy(notes = value) }
    }

    fun submit() {
        val s = _state.value
        val procedure = s.selectedProcedure ?: run {
            _state.update { it.copy(errorRes = R.string.logbook_error_pick_procedure) }
            return
        }
        if (s.isSubmitting || s.saved) return

        _state.update { it.copy(isSubmitting = true, errorRes = null) }
        viewModelScope.launch {
            val input = NewLogInput(
                procedureId = procedure.id,
                procedureDate = s.procedureDate,
                complexity = s.complexity,
                outcome = s.outcome,
                notes = s.notes,
            )
            repository.createLog(input)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false, saved = true) }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorRes = R.string.logbook_error_save,
                        )
                    }
                }
        }
    }
}

data class NewLogUiState(
    val isLoadingProcedures: Boolean = true,
    val procedures: List<Procedure> = emptyList(),
    val selectedProcedure: Procedure? = null,
    val procedureDate: LocalDate,
    val complexity: String = "",
    val outcome: String = "",
    val notes: String = "",
    val isSubmitting: Boolean = false,
    val saved: Boolean = false,
    @StringRes val errorRes: Int? = null,
)
