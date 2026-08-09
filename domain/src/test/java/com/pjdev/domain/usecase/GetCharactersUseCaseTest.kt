package com.pjdev.domain.usecase

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertSame
import org.junit.Test

class GetCharactersUseCaseTest {

    @Test
    fun getCharactersReturnsRepositoryFlow() {
        val characters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                imageUrl = "https://example.com/rick.jpg",
                episodeCount = 51,
            ),
        )

        val expectedFlow = flowOf(PagingData.from(characters))

        val repository = object : CharacterRepository {
            override fun getCharacters(
                name: String?,
            ): Flow<PagingData<Character>> = expectedFlow

            override suspend fun getCharacterDetail(
                id: Int,
            ): CharacterDetail {
                error("Not required for this test")
            }
        }

        val useCase = GetCharactersUseCase(repository)

        val result = useCase(name = null)

        assertSame(expectedFlow, result)
    }
}
