package com.pjdev.presentation.characterdetail.viewmodel

import com.pjdev.domain.error.DomainException
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import com.pjdev.domain.usecase.GetCharacterDetailUseCase
import com.pjdev.presentation.common.error.UiError
import com.pjdev.presentation.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CharacterRepository
    private lateinit var viewModel: CharacterDetailViewModel

    @Before
    fun setUp() {
        repository = mockk()

        viewModel = CharacterDetailViewModel(
            getCharacterDetailUseCase = GetCharacterDetailUseCase(
                characterRepository = repository,
            ),
        )
    }

    @Test
    fun `initial state is loading`() =
        runTest(mainDispatcherRule.testDispatcher) {
            assertEquals(
                CharacterDetailUiState.Loading,
                viewModel.uiState.value,
            )

            coVerify(exactly = 0) {
                repository.getCharacterDetail(any())
            }
        }

    @Test
    fun `load character updates state to success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val character = createCharacterDetail()

            coEvery {
                repository.getCharacterDetail(character.id)
            } returns character

            viewModel.loadCharacter(character.id)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(
                state is CharacterDetailUiState.Success,
            )

            assertEquals(
                character,
                (state as CharacterDetailUiState.Success).character,
            )

            coVerify(exactly = 1) {
                repository.getCharacterDetail(character.id)
            }
        }

    @Test
    fun `load character updates state to error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery {
                repository.getCharacterDetail(CHARACTER_ID)
            } throws DomainException.Network()

            viewModel.loadCharacter(CHARACTER_ID)

            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(
                state is CharacterDetailUiState.Error,
            )

            assertEquals(
                UiError.Network,
                (state as CharacterDetailUiState.Error).error,
            )

            coVerify(exactly = 1) {
                repository.getCharacterDetail(CHARACTER_ID)
            }
        }

    @Test
    fun `retry loads last character again`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val character = createCharacterDetail()

            coEvery {
                repository.getCharacterDetail(character.id)
            } returns character

            viewModel.loadCharacter(character.id)

            advanceUntilIdle()

            viewModel.retry()

            advanceUntilIdle()

            coVerify(exactly = 2) {
                repository.getCharacterDetail(character.id)
            }
        }

    private fun createCharacterDetail(): CharacterDetail {
        return CharacterDetail(
            id = CHARACTER_ID,
            name = "Rick Sanchez",
            imageUrl = "https://example.com/rick.jpg",
            status = "Alive",
            species = "Human",
            origin = "Earth",
            location = "Citadel of Ricks",
            episodes = emptyList(),
        )
    }

    private companion object {
        const val CHARACTER_ID = 1
    }
}
