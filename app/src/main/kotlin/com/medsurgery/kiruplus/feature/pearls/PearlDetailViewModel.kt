package com.medsurgery.kiruplus.feature.pearls

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.app.nav.KiruRoute
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
class PearlDetailViewModel @Inject constructor(
    private val repository: PearlsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route: KiruRoute.PearlDetail = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(PearlDetailUiState(id = route.pearlId))
    val state: StateFlow<PearlDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            repository.fetchPearl(route.pearlId)
                .onSuccess { pearl ->
                    _state.update {
                        if (pearl == null) {
                            it.copy(isLoading = false, errorRes = R.string.pearls_not_found)
                        } else {
                            it.copy(isLoading = false, pearl = pearl)
                        }
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false, errorRes = R.string.pearls_error_load)
                    }
                }
        }
    }
}

data class PearlDetailUiState(
    val id: Int,
    val isLoading: Boolean = true,
    val pearl: Pearl? = null,
    @StringRes val errorRes: Int? = null,
)
