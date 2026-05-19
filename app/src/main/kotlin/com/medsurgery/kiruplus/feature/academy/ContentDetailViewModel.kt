package com.medsurgery.kiruplus.feature.academy

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.app.nav.KiruRoute
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
class ContentDetailViewModel @Inject constructor(
    private val repository: ContentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route: KiruRoute.LessonDetail = savedStateHandle.toRoute()

    private val _state = MutableStateFlow(ContentDetailUiState(id = route.contentId))
    val state: StateFlow<ContentDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, errorRes = null) }
        viewModelScope.launch {
            repository.fetchById(route.contentId)
                .onSuccess { item ->
                    _state.update {
                        if (item == null) {
                            it.copy(isLoading = false, errorRes = R.string.academy_not_found)
                        } else {
                            it.copy(isLoading = false, item = item)
                        }
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false, errorRes = R.string.academy_error_load)
                    }
                }
        }
    }
}

data class ContentDetailUiState(
    val id: String,
    val isLoading: Boolean = true,
    val item: ContentItem? = null,
    @StringRes val errorRes: Int? = null,
)
