package com.pjdev.presentation.characterlist.viewmodel

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.repository.CharacterRepository
import com.pjdev.domain.usecase.GetCharactersUseCase
import com.pjdev.presentation.testutil.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CharacterRepository
    private lateinit var viewModel: CharacterListViewModel

    @Before
    fun setUp() {
        repository = mockk()

        every {
            repository.getCharacters(any())
        } returns flowOf(
            PagingData.empty<Character>(),
        )

        viewModel = CharacterListViewModel(
            getCharactersUseCase = GetCharactersUseCase(
                characterRepository = repository,
            ),
        )
    }

    @Test
    fun `search query updates immediately`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel.onSearchQueryChanged("Rick")

            assertEquals(
                "Rick",
                viewModel.searchQuery.value,
            )
        }

    @Test
    fun `search is executed only after debounce`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.characters.collect {}
            }

            viewModel.onSearchQueryChanged("Rick")

            advanceTimeBy(349)
            runCurrent()

            verify(exactly = 0) {
                repository.getCharacters(any())
            }

            advanceTimeBy(1)
            runCurrent()

            verify(exactly = 1) {
                repository.getCharacters("Rick")
            }
        }

    @Test
    fun `search query is trimmed before request`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.characters.collect {}
            }

            viewModel.onSearchQueryChanged(
                "  Rick Sanchez  ",
            )

            advanceTimeBy(350)
            runCurrent()

            verify(exactly = 1) {
                repository.getCharacters(
                    "Rick Sanchez",
                )
            }
        }

    @Test
    fun `blank search uses null filter`() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.characters.collect {}
            }

            viewModel.onSearchQueryChanged("Rick")

            advanceTimeBy(350)
            runCurrent()

            viewModel.onSearchQueryChanged("   ")

            advanceTimeBy(350)
            runCurrent()

            verifySequence {
                repository.getCharacters("Rick")
                repository.getCharacters(null)
            }
        }
}