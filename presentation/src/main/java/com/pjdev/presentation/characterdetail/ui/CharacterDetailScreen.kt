package com.pjdev.presentation.characterdetail.ui

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.model.Episode
import com.pjdev.presentation.R
import com.pjdev.presentation.characterdetail.components.CharacterDetailHeader
import com.pjdev.presentation.characterdetail.components.EpisodeCard
import com.pjdev.presentation.characterdetail.viewmodel.CharacterDetailUiState
import com.pjdev.presentation.characterdetail.viewmodel.CharacterDetailViewModel
import com.pjdev.presentation.common.error.UiError
import com.pjdev.presentation.theme.MultiverseSpacing

@Composable
fun CharacterDetailRoute(
    characterId: Int,
    viewModel: CharacterDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // The request is triggered as a side effect of entering a character route.
    // It only runs again when the character ID changes.
    LaunchedEffect(characterId) {
        viewModel.loadCharacter(characterId)
    }

    CharacterDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CharacterDetailTopBar(
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            CharacterDetailContent(
                uiState = uiState,
                onRetry = onRetry,
            )
        }
    }
}

@Composable
private fun CharacterDetailContent(
    uiState: CharacterDetailUiState,
    onRetry: () -> Unit,
) {
    when (uiState) {
        CharacterDetailUiState.Loading -> {
            CharacterDetailLoadingState()
        }

        is CharacterDetailUiState.Success -> {
            CharacterDetailSuccessContent(
                character = uiState.character,
            )
        }

        is CharacterDetailUiState.Error -> {
            CharacterDetailErrorState(
                error = uiState.error,
                onRetry = onRetry,
            )
        }
    }
}

@Composable
private fun CharacterDetailSuccessContent(
    character: CharacterDetail,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = DETAIL_MAX_WIDTH)
            .padding(
                horizontal = MultiverseSpacing.screenHorizontal,
            ),
        contentPadding = PaddingValues(
            top = MultiverseSpacing.screenVertical,
            bottom = MultiverseSpacing.section,
        ),
        verticalArrangement = Arrangement.spacedBy(
            MultiverseSpacing.medium,
        ),
    ) {
        item {
            CharacterDetailHeader(
                character = character,
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(MultiverseSpacing.small),
            )

            Text(
                text = stringResource(R.string.character_detail_episodes),
                modifier = Modifier.semantics {
                    heading()
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // Stable episode IDs preserve item identity if the list changes.
        items(
            items = character.episodes,
            key = { episode ->
                episode.id
            },
            contentType = {
                Episode::class
            },
        ) { episode ->
            EpisodeCard(
                episode = episode,
            )
        }
    }
}

@Composable
private fun CharacterDetailTopBar(
    onBackClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MultiverseSpacing.screenHorizontal,
                    vertical = MultiverseSpacing.small,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onBackClick,
            ) {
                Text(
                    text = stringResource(R.string.character_detail_back),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun CharacterDetailLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MultiverseSpacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(
                modifier = Modifier.height(MultiverseSpacing.medium),
            )

            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CharacterDetailErrorState(
    error: UiError,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MultiverseSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = detailErrorTitle(error),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.small),
        )

        Text(
            text = detailErrorMessage(error),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.large),
        )

        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = stringResource(R.string.retry),
            )
        }
    }
}

@Composable
private fun detailErrorTitle(
    error: UiError,
): String {
    return when (error) {
        UiError.Network -> stringResource(R.string.error_network_title)
        UiError.NotFound -> stringResource(R.string.error_not_found_title)
        UiError.RateLimited -> stringResource(R.string.error_rate_limited_title)
        UiError.Server -> stringResource(R.string.error_server_title)
        UiError.Unknown -> stringResource(R.string.error_unknown_title)
    }
}

@Composable
private fun detailErrorMessage(
    error: UiError,
): String {
    return when (error) {
        UiError.Network -> stringResource(R.string.error_network_message)
        UiError.NotFound -> stringResource(R.string.error_not_found_message)
        UiError.RateLimited -> stringResource(R.string.error_rate_limited_message)
        UiError.Server -> stringResource(R.string.error_server_message)
        UiError.Unknown -> stringResource(R.string.error_unknown_message)
    }
}

private val DETAIL_MAX_WIDTH = 760.dp
