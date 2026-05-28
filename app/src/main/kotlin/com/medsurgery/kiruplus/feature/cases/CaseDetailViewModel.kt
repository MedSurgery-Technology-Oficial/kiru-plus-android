package com.medsurgery.kiruplus.feature.cases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.app.nav.KiruRoute
import com.medsurgery.kiruplus.domain.cases.ClinicalCase
import com.medsurgery.kiruplus.domain.cases.ClinicalCasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaseDetailViewModel @Inject constructor(
    private val repository: ClinicalCasesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route: KiruRoute.CaseDetail = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(CaseDetailUiState(id = route.caseId))
    val state: StateFlow<CaseDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val case = repository.getCaseById(route.caseId)
            _state.update {
                if (case == null) {
                    it.copy(isLoading = false, errorMessage = "Caso clínico no encontrado.")
                } else {
                    it.copy(isLoading = false, clinicalCase = case)
                }
            }
        }
    }
}

data class CaseDetailUiState(
    val id: String,
    val isLoading: Boolean = true,
    val clinicalCase: ClinicalCase? = null,
    val errorMessage: String? = null,
)
