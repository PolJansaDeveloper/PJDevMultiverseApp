package com.pjdev.domain.usecase

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetCharacterDetailUseCaseTest {

    @Test
    fun getCharacterDetailReturnsRepositoryResult() = runTest {
        val expectedCharacter = CharacterDetail(
            id = 1,
            name = "Rick Sanchez",
            imageUrl = "https://example.com/rick.jpg",
            status = "Alive",
            species = "Human",
            origin = "Earth",
            location = "Earth",
            episodes = emptyList(),
        )

        val repository = object : CharacterRepository {
            override fun getCharacters(
                name: String?,
            ): Flow<PagingData<Character>> = emptyFlow()

            override suspend fun getCharacterDetail(
                id: Int,
            ): CharacterDetail = expectedCharacter
        }

        val useCase = GetCharacterDetailUseCase(repository)

        val result = useCase(id = 1)

        assertEquals(expectedCharacter, result)
    }
}
