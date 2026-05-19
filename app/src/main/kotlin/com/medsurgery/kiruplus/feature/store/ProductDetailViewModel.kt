package com.medsurgery.kiruplus.feature.store

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.app.nav.KiruRoute
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
class ProductDetailViewModel @Inject constructor(
    private val repository: StoreRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route: KiruRoute.ProductDetail = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(ProductDetailUiState(productId = route.productId))
    val state: StateFlow<ProductDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            repository.fetchProduct(route.productId)
                .onSuccess { product ->
                    if (product == null) {
                        _state.update { it.copy(isLoading = false, errorRes = R.string.store_product_not_found) }
                    } else {
                        _state.update { it.copy(isLoading = false, product = product) }
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, errorRes = R.string.store_error_load) }
                }
        }
    }
}

data class ProductDetailUiState(
    val productId: String,
    val isLoading: Boolean = true,
    val product: StoreProduct? = null,
    @StringRes val errorRes: Int? = null,
)
