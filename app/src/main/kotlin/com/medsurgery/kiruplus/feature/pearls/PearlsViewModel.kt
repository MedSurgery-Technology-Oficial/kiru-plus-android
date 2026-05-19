package com.medsurgery.kiruplus.feature.pearls

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.pearls.Pearl
import com.medsurgery.kiruplus.domain.pearls.PearlsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PearlsViewModel @Inject constructor(
    private val repository: PearlsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PearlsUiState())
    val state: StateFlow<PearlsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            repository.fetchAllPearls()
                .onSuccess { list ->
                    _state.update { it.copy(isLoading = false, pearls = list) }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorRes = R.string.pearls_error_load,
                        )
                    }
                }
        }
    }
}

data class PearlsUiState(
    val isLoading: Boolean = true,
    val pearls: List<Pearl> = emptyList(),
    @StringRes val errorRes: Int? = null,
)
