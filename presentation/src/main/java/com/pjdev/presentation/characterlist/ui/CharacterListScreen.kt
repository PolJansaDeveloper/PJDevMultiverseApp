package com.pjdev.presentation.characterlist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.pjdev.domain.model.Character
import com.pjdev.presentation.R
import com.pjdev.presentation.characterlist.components.CharacterCard
import com.pjdev.presentation.characterlist.components.CharacterSearchBar
import com.pjdev.presentation.characterlist.components.EmptyState
import com.pjdev.presentation.characterlist.components.ErrorState
import com.pjdev.presentation.characterlist.components.LoadingState
import com.pjdev.presentation.characterlist.viewmodel.CharacterListViewModel
import com.pjdev.presentation.common.error.UiError
import com.pjdev.presentation.common.error.toUiError
import com.pjdev.presentation.theme.MultiverseSpacing
import kotlinx.coroutines.launch

@Composable
fun CharacterListRoute(
    viewModel: CharacterListViewModel,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val characters = viewModel.characters.collectAsLazyPagingItems()

    CharacterListScreen(
        query = searchQuery,
        characters = characters,
        onQueryChange = viewModel::onSearchQueryChanged,
        onCharacterClick = onCharacterClick,
        modifier = modifier,
    )
}

@Composable
fun CharacterListScreen(
    query: String,
    characters: LazyPagingItems<Character>,
    onQueryChange: (String) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    val isListVisible =
        characters.loadState.refresh is LoadState.NotLoading &&
                characters.itemCount > 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ScrollToTopButton(
                listState = listState,
                isListVisible = isListVisible,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            CharacterListLayout(
                query = query,
                characters = characters,
                listState = listState,
                onQueryChange = onQueryChange,
                onCharacterClick = onCharacterClick,
            )
        }
    }
}

@Composable
private fun CharacterListLayout(
    query: String,
    characters: LazyPagingItems<Character>,
    listState: LazyListState,
    onQueryChange: (String) -> Unit,
    onCharacterClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = LIST_MAX_WIDTH)
            .padding(
                horizontal = MultiverseSpacing.screenHorizontal,
                vertical = MultiverseSpacing.screenVertical,
            ),
    ) {
        CharacterListHeader()

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.large),
        )

        CharacterSearchBar(
            query = query,
            onQueryChange = onQueryChange,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.large),
        )

        CharacterListContent(
            query = query,
            characters = characters,
            listState = listState,
            onCharacterClick = onCharacterClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CharacterListHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        )
        {
            Text(
                text = stringResource(R.string.character_list_title),
                modifier = Modifier.semantics {
                    heading()
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = stringResource(R.string.app_signature),
                modifier = Modifier.clearAndSetSemantics {},
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.small),
        )

        Text(
            text = stringResource(R.string.character_list_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CharacterListContent(
    query: String,
    characters: LazyPagingItems<Character>,
    listState: LazyListState,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = characters.loadState.refresh

    // Refresh belongs to the current Paging generation. Previous items must
    // not hide loading or error states from a new search.
    when {
        refreshState is LoadState.Loading -> {
            LoadingState(
                modifier = modifier,
            )
        }

        refreshState is LoadState.Error -> {
            CharacterListRefreshError(
                query = query,
                error = refreshState.error.toUiError(),
                onRetry = characters::retry,
                modifier = modifier,
            )
        }

        characters.itemCount == 0 -> {
            EmptyState(
                modifier = modifier,
            )
        }

        else -> {
            CharacterLazyList(
                characters = characters,
                listState = listState,
                onCharacterClick = onCharacterClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CharacterListRefreshError(
    query: String,
    error: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (error == UiError.NotFound && query.isNotBlank()) {
        EmptyState(
            modifier = modifier,
        )
    } else {
        ErrorState(
            error = error,
            onRetry = onRetry,
            modifier = modifier,
        )
    }
}

@Composable
private fun CharacterLazyList(
    characters: LazyPagingItems<Character>,
    listState: LazyListState,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(
            bottom = MultiverseSpacing.section,
        ),
        verticalArrangement = Arrangement.spacedBy(
            MultiverseSpacing.medium,
        ),
    ) {
        // Stable IDs preserve item identity while Paging updates the list.
        // contentType helps Compose reuse compatible item compositions.
        items(
            count = characters.itemCount,
            key = characters.itemKey { character ->
                character.id
            },
            contentType = characters.itemContentType {
                Character::class
            },
        ) { index ->
            characters[index]?.let { character ->
                CharacterCard(
                    character = character,
                    onClick = {
                        onCharacterClick(character.id)
                    },
                )
            }
        }

        when (val appendState = characters.loadState.append) {
            is LoadState.Loading -> {
                item {
                    AppendLoadingState()
                }
            }

            is LoadState.Error -> {
                item {
                    AppendErrorState(
                        error = appendState.error.toUiError(),
                        onRetry = characters::retry,
                    )
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun ScrollToTopButton(
    listState: LazyListState,
    isListVisible: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()

    // Scroll position changes continuously. derivedStateOf limits
    // recomposition to actual changes in the button visibility condition.
    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > SCROLL_TO_TOP_THRESHOLD
        }
    }

    AnimatedVisibility(
        visible = isListVisible && showScrollToTop,
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
        ) {
            Text(
                text = stringResource(R.string.scroll_to_top),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun AppendLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(MultiverseSpacing.large),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun AppendErrorState(
    error: UiError,
    onRetry: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = appendErrorMessage(error),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        TextButton(
            onClick = onRetry,
            modifier = Modifier.wrapContentWidth(),
        ) {
            Text(
                text = stringResource(R.string.retry),
            )
        }
    }
}

@Composable
private fun appendErrorMessage(
    error: UiError,
): String {
    return when (error) {
        UiError.Network -> stringResource(R.string.error_network_message)
        UiError.RateLimited -> stringResource(R.string.error_rate_limited_message)
        UiError.Server -> stringResource(R.string.error_server_message)

        UiError.NotFound,
        UiError.Unknown,
            -> stringResource(R.string.load_more_error)
    }
}

private val LIST_MAX_WIDTH = 760.dp

private const val SCROLL_TO_TOP_THRESHOLD = 2
