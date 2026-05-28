package com.medsurgery.kiruplus.feature.drugs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.domain.drugs.Drug
import com.medsurgery.kiruplus.domain.drugs.DrugsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrugsViewModel @Inject constructor(
    private val repository: DrugsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DrugsUiState())
    val state: StateFlow<DrugsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val drugs = repository.getDrugs()
            _state.update {
                it.copy(
                    isLoading = false,
                    drugs = drugs,
                    filteredDrugs = applyFilters(drugs, it.searchQuery, it.selectedCategory),
                )
            }
        }
    }

    fun onSearch(query: String) {
        _state.update { current ->
            val filtered = applyFilters(current.drugs, query, current.selectedCategory)
            current.copy(searchQuery = query, filteredDrugs = filtered)
        }
    }

    fun onCategoryFilter(category: String?) {
        _state.update { current ->
            val filtered = applyFilters(current.drugs, current.searchQuery, category)
            current.copy(selectedCategory = category, filteredDrugs = filtered)
        }
    }

    private fun applyFilters(
        drugs: List<Drug>,
        query: String,
        category: String?,
    ): List<Drug> {
        val byCategory = if (category != null) {
            drugs.filter { it.category == category }
        } else {
            drugs
        }
        return repository.searchDrugs(query, byCategory)
    }
}

data class DrugsUiState(
    val isLoading: Boolean = true,
    val drugs: List<Drug> = emptyList(),
    val filteredDrugs: List<Drug> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
)
