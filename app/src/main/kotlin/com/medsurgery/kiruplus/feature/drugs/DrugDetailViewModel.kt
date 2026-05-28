package com.medsurgery.kiruplus.feature.drugs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.app.nav.KiruRoute
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
class DrugDetailViewModel @Inject constructor(
    private val repository: DrugsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route: KiruRoute.DrugDetail = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(DrugDetailUiState(id = route.drugId))
    val state: StateFlow<DrugDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val drug = repository.getDrugById(route.drugId)
            _state.update {
                if (drug == null) {
                    it.copy(isLoading = false, errorMessage = "Fármaco no encontrado.")
                } else {
                    it.copy(isLoading = false, drug = drug)
                }
            }
        }
    }
}

data class DrugDetailUiState(
    val id: String,
    val isLoading: Boolean = true,
    val drug: Drug? = null,
    val errorMessage: String? = null,
)
