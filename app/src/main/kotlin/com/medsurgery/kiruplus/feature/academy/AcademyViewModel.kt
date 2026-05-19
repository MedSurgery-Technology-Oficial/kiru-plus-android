package com.medsurgery.kiruplus.feature.academy

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.academy.ContentItem
import com.medsurgery.kiruplus.domain.academy.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademyViewModel @Inject constructor(
    private val repository: ContentRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AcademyUiState())
    val state: StateFlow<AcademyUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            repository.fetchLessons()
                .onSuccess { list ->
                    _state.update { it.copy(isLoading = false, lessons = list) }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorRes = R.string.academy_error_load,
                        )
                    }
                }
        }
    }
}

data class AcademyUiState(
    val isLoading: Boolean = true,
    val lessons: List<ContentItem> = emptyList(),
    @StringRes val errorRes: Int? = null,
)
