package com.pjdev.presentation.characterlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pjdev.domain.model.Character
import com.pjdev.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class,
)
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Debouncing avoids unnecessary API requests while the user is still typing,
    // while flatMapLatest keeps only the most recent search active.
    val characters: Flow<PagingData<Character>> = searchQuery
        .debounce(SEARCH_DEBOUNCE_MILLIS)
        .map { query -> query.trim() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getCharactersUseCase(
                name = query.takeIf { it.isNotBlank() },
            )
        }
        // Keep loaded pages available while this ViewModel remains alive.
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 350L
    }
}
