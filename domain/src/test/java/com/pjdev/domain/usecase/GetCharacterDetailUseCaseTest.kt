package com.pjdev.domain.usecase

import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetCharacterDetailUseCaseTest {

    @Test
    fun getCharacterDetailReturnsSuccessWhenRepositorySucceeds() =
        runTest {
            val repository = mockk<CharacterRepository>()

            val expectedCharacter = CharacterDetail(
                id = CHARACTER_ID,
                name = "Rick Sanchez",
                imageUrl = "https://example.com/rick.jpg",
                status = "Alive",
                species = "Human",
                origin = "Earth",
                location = "Citadel of Ricks",
                episodes = emptyList(),
            )

            coEvery {
                repository.getCharacterDetail(
                    id = CHARACTER_ID,
                )
            } returns expectedCharacter

            val useCase = GetCharacterDetailUseCase(
                characterRepository = repository,
            )

            val result = useCase(
                id = CHARACTER_ID,
            )

            assertTrue(
                result.isSuccess,
            )

            assertEquals(
                expectedCharacter,
                result.getOrNull(),
            )

            coVerify(exactly = 1) {
                repository.getCharacterDetail(
                    id = CHARACTER_ID,
                )
            }
        }

    @Test
    fun getCharacterDetailReturnsFailureWhenRepositoryThrows() =
        runTest {
            val repository = mockk<CharacterRepository>()

            val expectedException =
                IllegalStateException("Repository error")

            coEvery {
                repository.getCharacterDetail(
                    id = CHARACTER_ID,
                )
            } throws expectedException

            val useCase = GetCharacterDetailUseCase(
                characterRepository = repository,
            )

            val result = useCase(
                id = CHARACTER_ID,
            )

            assertTrue(
                result.isFailure,
            )

            assertSame(
                expectedException,
                result.exceptionOrNull(),
            )

            coVerify(exactly = 1) {
                repository.getCharacterDetail(
                    id = CHARACTER_ID,
                )
            }
        }

    private companion object {
        const val CHARACTER_ID = 1
    }
}
