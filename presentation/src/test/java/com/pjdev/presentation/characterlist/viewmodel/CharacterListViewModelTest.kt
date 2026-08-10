package com.pjdev.presentation.characterlist.viewmodel

import com.pjdev.domain.usecase.GetCharactersUseCase
import com.pjdev.presentation.testutil.FakeCharacterRepository
import com.pjdev.presentation.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeCharacterRepository
    private lateinit var viewModel: CharacterListViewModel

    @Before
    fun setUp() {
        repository = FakeCharacterRepository()

        viewModel = CharacterListViewModel(
            getCharactersUseCase = GetCharactersUseCase(repository),
        )
    }

    @Test
    fun searchQueryUpdatesImmediately() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel.onSearchQueryChanged("Rick")

            assertEquals(
                "Rick",
                viewModel.searchQuery.value,
            )
        }

    @Test
    fun searchIsExecutedOnlyAfterDebounce() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.characters.collect {}
            }

            viewModel.onSearchQueryChanged("Rick")

            advanceTimeBy(349)
            runCurrent()

            assertTrue(repository.requestedNames.isEmpty())

            advanceTimeBy(1)
            runCurrent()

            assertEquals(
                listOf("Rick"),
                repository.requestedNames,
            )
        }

    @Test
    fun searchQueryIsTrimmedBeforeRequest() =
        runTest(mainDispatcherRule.testDispatcher) {
            backgroundScope.launch {
                viewModel.characters.collect {}
            }

            viewModel.onSearchQueryChanged("  Rick Sanchez  ")

            advanceTimeBy(350)
            runCurrent()

            assertEquals(
                listOf("Rick Sanchez"),
                repository.requestedNames,
            )
        }

    @Test
    fun blankSearchUsesNullFilter() =
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

            assertEquals(
                listOf("Rick", null),
                repository.requestedNames,
            )
        }
}
