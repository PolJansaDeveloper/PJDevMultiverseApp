package com.pjdev.presentation.characterdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjdev.domain.usecase.GetCharacterDetailUseCase
import com.pjdev.presentation.common.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)

    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private var currentCharacterId: Int? = null

    fun loadCharacter(characterId: Int) {
        currentCharacterId = characterId

        viewModelScope.launch {
            _uiState.value = CharacterDetailUiState.Loading

            // Result keeps operation failures explicit while the ViewModel
            // remains responsible for translating them into screen state.
            getCharacterDetailUseCase(characterId)
                .fold(
                    onSuccess = { character ->
                        _uiState.value = CharacterDetailUiState.Success(
                            character = character,
                        )
                    },
                    onFailure = { throwable ->
                        _uiState.value = CharacterDetailUiState.Error(
                            error = throwable.toUiError(),
                        )
                    },
                )
        }
    }

    fun retry() {
        currentCharacterId?.let(::loadCharacter)
    }
}
