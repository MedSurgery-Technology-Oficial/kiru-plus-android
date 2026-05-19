package com.medsurgery.kiruplus.feature.store

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.store.StoreProduct
import com.medsurgery.kiruplus.domain.store.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StoreUiState())
    val state: StateFlow<StoreUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            repository.fetchVisibleProducts()
                .onSuccess { list ->
                    _state.update { it.copy(isLoading = false, products = list) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorRes = R.string.store_error_load,
                        )
                    }
                }
        }
    }
}

data class StoreUiState(
    val isLoading: Boolean = false,
    val products: List<StoreProduct> = emptyList(),
    @StringRes val errorRes: Int? = null,
)
