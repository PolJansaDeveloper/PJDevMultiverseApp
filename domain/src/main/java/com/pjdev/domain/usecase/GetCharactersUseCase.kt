package com.pjdev.domain.usecase

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCase(
    private val characterRepository: CharacterRepository,
) {

    operator fun invoke(
        name: String?,
    ): Flow<PagingData<Character>> {
        return characterRepository.getCharacters(name)
    }
}
