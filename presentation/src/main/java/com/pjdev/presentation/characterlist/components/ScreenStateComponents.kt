package com.pjdev.presentation.characterlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.pjdev.presentation.R
import com.pjdev.presentation.common.error.UiError
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
) {
    // Announce loading state changes without interrupting the current
    // accessibility announcement.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.large)
            .semantics {
                liveRegion = LiveRegionMode.Polite
            },
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
            text = stringResource(R.string.loading_characters),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.empty_characters_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.small),
        )

        Text(
            text = stringResource(R.string.empty_characters_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ErrorState(
    error: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when (error) {
        UiError.Network -> stringResource(R.string.error_network_title)
        UiError.NotFound -> stringResource(R.string.error_not_found_title)
        UiError.RateLimited -> stringResource(R.string.error_rate_limited_title)
        UiError.Server -> stringResource(R.string.error_server_title)
        UiError.Unknown -> stringResource(R.string.error_unknown_title)
    }

    val message = when (error) {
        UiError.Network -> stringResource(R.string.error_network_message)
        UiError.NotFound -> stringResource(R.string.error_not_found_message)
        UiError.RateLimited -> stringResource(R.string.error_rate_limited_message)
        UiError.Server -> stringResource(R.string.error_server_message)
        UiError.Unknown -> stringResource(R.string.error_unknown_message)
    }

    // The UI only understands presentation errors; infrastructure exceptions
    // such as Retrofit or IO failures never reach this composable.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.large)
            .semantics {
                liveRegion = LiveRegionMode.Polite
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.small),
        )

        Text(
            text = message,
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
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Preview(
    name = "Loading state",
    showBackground = true,
)
@Composable
private fun LoadingStatePreview() {
    MultiverseTheme {
        LoadingState()
    }
}

@Preview(
    name = "Empty state",
    showBackground = true,
)
@Composable
private fun EmptyStatePreview() {
    MultiverseTheme {
        EmptyState()
    }
}

@Preview(
    name = "Error state",
    showBackground = true,
)
@Composable
private fun ErrorStatePreview() {
    MultiverseTheme {
        ErrorState(
            error = UiError.Network,
            onRetry = {},
        )
    }
}
