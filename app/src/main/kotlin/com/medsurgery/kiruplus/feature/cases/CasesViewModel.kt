package com.medsurgery.kiruplus.feature.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class CasesViewModel @Inject constructor(
    private val repository: ClinicalCasesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CasesUiState())
    val state: StateFlow<CasesUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val cases = repository.getCases()
            _state.update {
                it.copy(
                    isLoading = false,
                    cases = cases,
                    filteredCases = applyFilters(cases, it.searchQuery, it.selectedCategory, it.selectedDifficulty),
                )
            }
        }
    }

    fun onSearch(query: String) {
        _state.update { current ->
            val filtered = applyFilters(current.cases, query, current.selectedCategory, current.selectedDifficulty)
            current.copy(searchQuery = query, filteredCases = filtered)
        }
    }

    fun onCategoryFilter(category: String?) {
        _state.update { current ->
            val filtered = applyFilters(current.cases, current.searchQuery, category, current.selectedDifficulty)
            current.copy(selectedCategory = category, filteredCases = filtered)
        }
    }

    fun onDifficultyFilter(difficulty: String?) {
        _state.update { current ->
            val filtered = applyFilters(current.cases, current.searchQuery, current.selectedCategory, difficulty)
            current.copy(selectedDifficulty = difficulty, filteredCases = filtered)
        }
    }

    private fun applyFilters(
        cases: List<ClinicalCase>,
        query: String,
        category: String?,
        difficulty: String?,
    ): List<ClinicalCase> {
        var result = cases
        if (category != null) {
            result = result.filter { it.category == category }
        }
        if (difficulty != null) {
            result = result.filter { it.difficulty == difficulty }
        }
        return repository.searchCases(query, result)
    }
}

data class CasesUiState(
    val isLoading: Boolean = true,
    val cases: List<ClinicalCase> = emptyList(),
    val filteredCases: List<ClinicalCase> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val selectedDifficulty: String? = null,
)
