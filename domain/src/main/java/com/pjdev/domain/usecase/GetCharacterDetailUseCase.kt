package com.pjdev.domain.usecase

import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository

class GetCharacterDetailUseCase(
    private val characterRepository: CharacterRepository,
) {

    suspend operator fun invoke(
        id: Int,
    ): CharacterDetail {
        return characterRepository.getCharacterDetail(id)
    }
}
