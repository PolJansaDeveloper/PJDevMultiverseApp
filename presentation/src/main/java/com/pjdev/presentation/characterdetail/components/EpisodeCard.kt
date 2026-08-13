package com.pjdev.presentation.characterdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import com.pjdev.domain.model.Episode
import com.pjdev.presentation.R
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme
import com.pjdev.presentation.theme.ThemePreviews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun EpisodeCard(
    episode: Episode,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]

    /*
     * Use the current device locale instead of forcing a fixed
     * date representation.
     */
    val formattedAirDate = remember(
        episode.airDate,
        locale,
    ) {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(locale)
            .format(episode.airDate)
    }

    val accessibilityDescription = stringResource(
        R.string.accessibility_episode_item,
        episode.name,
        episode.code,
        formattedAirDate,
    )

    /*
     * The episode is presented to accessibility services as one logical
     * element instead of exposing its visual children separately.
     */
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                contentDescription = accessibilityDescription
            },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MultiverseSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EpisodeCodeBadge(
                code = episode.code,
            )

            Spacer(
                modifier = Modifier.width(MultiverseSpacing.medium),
            )

            EpisodeInfo(
                episode = episode,
                formattedAirDate = formattedAirDate,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EpisodeCodeBadge(
    code: String,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Text(
            text = code,
            modifier = Modifier.padding(
                horizontal = MultiverseSpacing.small,
                vertical = MultiverseSpacing.extraSmall,
            ),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun EpisodeInfo(
    episode: Episode,
    formattedAirDate: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = episode.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = stringResource(
                R.string.episode_air_date,
                formattedAirDate,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@ThemePreviews
@Composable
private fun EpisodeCardPreview() {
    MultiverseTheme {
        EpisodeCard(
            episode = previewEpisode,
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

private val previewEpisode = Episode(
    id = 1,
    name = "Pilot",
    code = "S01E01",
    airDate = LocalDate.of(2013, 12, 2),
)
