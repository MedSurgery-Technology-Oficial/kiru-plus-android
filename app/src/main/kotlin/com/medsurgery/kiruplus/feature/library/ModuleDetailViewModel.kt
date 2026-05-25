package com.medsurgery.kiruplus.feature.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.app.nav.KiruRoute
import com.medsurgery.kiruplus.domain.library.LibraryModule
import com.medsurgery.kiruplus.domain.library.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ModuleDetailUiState(
    val isLoading: Boolean = true,
    val module: LibraryModule? = null,
    val error: String? = null,
)

@HiltViewModel
class ModuleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LibraryRepository,
) : ViewModel() {

    private val moduleId: String = savedStateHandle.toRoute<KiruRoute.LibraryModuleDetail>().moduleId

    private val _state = MutableStateFlow(ModuleDetailUiState())
    val state: StateFlow<ModuleDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getStudyModule(moduleId).collect { result ->
                result.fold(
                    onSuccess = { module ->
                        if (module == null) {
                            Timber.w("Module not found: $moduleId")
                            _state.update { it.copy(isLoading = false, error = "module_not_found") }
                        } else {
                            _state.update { it.copy(isLoading = false, module = module) }
                        }
                    },
                    onFailure = { e ->
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    },
                )
            }
        }
    }
}
