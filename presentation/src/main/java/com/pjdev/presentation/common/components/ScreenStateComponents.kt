package com.pjdev.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.pjdev.presentation.R
import com.pjdev.presentation.common.error.UiError
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme
import com.pjdev.presentation.theme.ThemePreviews

@Composable
fun LoadingState(
    message: String,
    modifier: Modifier = Modifier,
) {
    /*
     * Loading state changes are announced politely so they do not
     * interrupt an accessibility announcement already in progress.
     */
    Column(
        modifier = modifier
            .fillMaxSize()
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
            text = message,
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
    val title = errorTitle(error)
    val message = errorMessage(error)

    /*
     * Infrastructure exceptions never reach the UI.
     * The composable only works with presentation-level errors.
     */
    Column(
        modifier = modifier
            .fillMaxSize()
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

@Composable
private fun errorTitle(
    error: UiError,
): String {
    return when (error) {
        UiError.Network ->
            stringResource(R.string.error_network_title)

        UiError.NotFound ->
            stringResource(R.string.error_not_found_title)

        UiError.RateLimited ->
            stringResource(R.string.error_rate_limited_title)

        UiError.Server ->
            stringResource(R.string.error_server_title)

        UiError.Unknown ->
            stringResource(R.string.error_unknown_title)
    }
}

@Composable
private fun errorMessage(
    error: UiError,
): String {
    return when (error) {
        UiError.Network ->
            stringResource(R.string.error_network_message)

        UiError.NotFound ->
            stringResource(R.string.error_not_found_message)

        UiError.RateLimited ->
            stringResource(R.string.error_rate_limited_message)

        UiError.Server ->
            stringResource(R.string.error_server_message)

        UiError.Unknown ->
            stringResource(R.string.error_unknown_message)
    }
}

@ThemePreviews
@Composable
private fun LoadingStatePreview() {
    MultiverseTheme {
        LoadingState(
            message = stringResource(R.string.loading),
        )
    }
}

@ThemePreviews
@Composable
private fun ErrorStatePreview() {
    MultiverseTheme {
        ErrorState(
            error = UiError.Network,
            onRetry = {},
        )
    }
}
