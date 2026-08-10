package com.pjdev.domain.usecase

import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.CancellationException

class GetCharacterDetailUseCase(
    private val characterRepository: CharacterRepository,
) {

    suspend operator fun invoke(
        id: Int,
    ): Result<CharacterDetail> {
        return runCatching {
            characterRepository.getCharacterDetail(id)
        }.onFailure { throwable ->
            if (throwable is CancellationException) {
                throw throwable
            }
        }
    }
}
