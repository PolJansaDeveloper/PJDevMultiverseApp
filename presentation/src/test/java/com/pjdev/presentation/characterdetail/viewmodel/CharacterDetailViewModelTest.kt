package com.pjdev.presentation.characterdetail.viewmodel

import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.usecase.GetCharacterDetailUseCase
import com.pjdev.presentation.testutil.FakeCharacterRepository
import com.pjdev.presentation.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeCharacterRepository
    private lateinit var viewModel: CharacterDetailViewModel

    @Before
    fun setUp() {
        repository = FakeCharacterRepository()

        viewModel = CharacterDetailViewModel(
            getCharacterDetailUseCase = GetCharacterDetailUseCase(repository),
        )
    }

    @Test
    fun initialStateIsLoading() = runTest(mainDispatcherRule.testDispatcher) {
        assertEquals(
            CharacterDetailUiState.Loading,
            viewModel.uiState.value,
        )
    }

    @Test
    fun loadCharacterUpdatesStateToSuccess() =
        runTest(mainDispatcherRule.testDispatcher) {
            val character = createCharacterDetail()
            repository.characterDetail = character

            viewModel.loadCharacter(character.id)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state is CharacterDetailUiState.Success)
            assertEquals(
                character,
                (state as CharacterDetailUiState.Success).character,
            )
        }

    @Test
    fun loadCharacterUpdatesStateToError() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedException = IllegalStateException("Repository error")
            repository.detailException = expectedException

            viewModel.loadCharacter(1)
            advanceUntilIdle()

            val state = viewModel.uiState.value

            assertTrue(state is CharacterDetailUiState.Error)
            assertSame(
                expectedException,
                (state as CharacterDetailUiState.Error).throwable,
            )
        }

    @Test
    fun retryLoadsLastCharacterAgain() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.characterDetail = createCharacterDetail()

            viewModel.loadCharacter(1)
            advanceUntilIdle()

            viewModel.retry()
            advanceUntilIdle()

            assertEquals(
                listOf(1, 1),
                repository.requestedCharacterIds,
            )
        }

    private fun createCharacterDetail() = CharacterDetail(
        id = 1,
        name = "Rick Sanchez",
        imageUrl = "https://example.com/rick.jpg",
        status = "Alive",
        species = "Human",
        origin = "Earth",
        location = "Citadel of Ricks",
        episodes = emptyList(),
    )
}
