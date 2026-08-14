package com.pjdev.domain.usecase

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.repository.CharacterRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertSame
import org.junit.Test

class GetCharactersUseCaseTest {

    @Test
    fun getCharactersReturnsRepositoryFlow() {
        val repository = mockk<CharacterRepository>()

        val characters = listOf(
            Character(
                id = 1,
                name = "Rick Sanchez",
                imageUrl = "https://example.com/rick.jpg",
                episodeCount = 51,
            ),
        )

        val expectedFlow = flowOf(
            PagingData.from(characters),
        )

        every {
            repository.getCharacters(
                name = null,
            )
        } returns expectedFlow

        val useCase = GetCharactersUseCase(
            characterRepository = repository,
        )

        val result = useCase(
            name = null,
        )

        assertSame(
            expectedFlow,
            result,
        )

        verify(exactly = 1) {
            repository.getCharacters(
                name = null,
            )
        }
    }
}
