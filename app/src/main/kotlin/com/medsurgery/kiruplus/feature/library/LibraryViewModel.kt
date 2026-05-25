package com.medsurgery.kiruplus.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.domain.library.CurriculumBlock
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

data class LibraryUiState(
    val isLoading: Boolean = true,
    val modules: List<LibraryModule> = emptyList(),
    val curriculum: List<CurriculumBlock> = emptyList(),
    val error: String? = null,
    val selectedTab: Int = 0,
) {
    val isEmpty: Boolean get() = !isLoading && modules.isEmpty() && curriculum.isEmpty() && error == null
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch { loadModules() }
        viewModelScope.launch { loadCurriculum() }
    }

    fun selectTab(index: Int) = _state.update { it.copy(selectedTab = index) }

    private suspend fun loadModules() {
        repository.getStudyModules().collect { result ->
            result.fold(
                onSuccess = { modules ->
                    _state.update { it.copy(modules = modules, isLoading = false) }
                    Timber.d("Library: loaded ${modules.size} study modules")
                },
                onFailure = { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                },
            )
        }
    }

    private suspend fun loadCurriculum() {
        repository.getCurriculumBlocks().collect { result ->
            result.fold(
                onSuccess = { blocks ->
                    _state.update { it.copy(curriculum = blocks, isLoading = false) }
                    Timber.d("Library: loaded ${blocks.size} curriculum blocks")
                },
                onFailure = { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                },
            )
        }
    }
}
