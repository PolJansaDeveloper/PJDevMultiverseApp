package com.pjdev.domain.usecase

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetCharacterDetailUseCaseTest {

    @Test
    fun getCharacterDetailReturnsSuccessWhenRepositorySucceeds() = runTest {
        val expectedCharacter = CharacterDetail(
            id = 1,
            name = "Rick Sanchez",
            imageUrl = "https://example.com/rick.jpg",
            status = "Alive",
            species = "Human",
            origin = "Earth",
            location = "Citadel of Ricks",
            episodes = emptyList(),
        )

        val repository = object : CharacterRepository {

            override fun getCharacters(
                name: String?,
            ): Flow<PagingData<Character>> {
                error("Not required for this test")
            }

            override suspend fun getCharacterDetail(
                id: Int,
            ): CharacterDetail {
                return expectedCharacter
            }
        }

        val useCase = GetCharacterDetailUseCase(repository)

        val result = useCase(1)

        assertTrue(result.isSuccess)
        assertEquals(expectedCharacter, result.getOrNull())
    }

    @Test
    fun getCharacterDetailReturnsFailureWhenRepositoryThrows() = runTest {
        val expectedException = IllegalStateException("Repository error")

        val repository = object : CharacterRepository {

            override fun getCharacters(
                name: String?,
            ): Flow<PagingData<Character>> {
                error("Not required for this test")
            }

            override suspend fun getCharacterDetail(
                id: Int,
            ): CharacterDetail {
                throw expectedException
            }
        }

        val useCase = GetCharacterDetailUseCase(repository)

        val result = useCase(1)

        assertTrue(result.isFailure)
        assertSame(expectedException, result.exceptionOrNull())
    }
}
