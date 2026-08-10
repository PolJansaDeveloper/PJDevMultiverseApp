package com.pjdev.presentation.characterdetail.viewmodel

import com.pjdev.domain.model.CharacterDetail
import com.pjdev.presentation.common.error.UiError

sealed interface CharacterDetailUiState {

    data object Loading : CharacterDetailUiState

    data class Success(
        val character: CharacterDetail,
    ) : CharacterDetailUiState

    data class Error(
        val error: UiError,
    ) : CharacterDetailUiState
}
