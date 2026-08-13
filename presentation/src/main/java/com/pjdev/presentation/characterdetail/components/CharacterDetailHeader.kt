package com.pjdev.presentation.characterdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.presentation.R
import com.pjdev.presentation.common.components.CharacterPortrait
import com.pjdev.presentation.theme.FontScalePreviews
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme
import com.pjdev.presentation.theme.ThemePreviews

@Composable
fun CharacterDetailHeader(
    character: CharacterDetail,
    modifier: Modifier = Modifier,
) {
    val useLargeTextLayout =
        LocalDensity.current.fontScale >= LARGE_FONT_SCALE_THRESHOLD

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
    ) {
        if (useLargeTextLayout) {
            CharacterDetailLargeTextLayout(
                character = character,
            )
        } else {
            CharacterDetailDefaultLayout(
                character = character,
            )
        }
    }
}

@Composable
private fun CharacterDetailDefaultLayout(
    character: CharacterDetail,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharacterPortrait(
            name = character.name,
            imageUrl = character.imageUrl,
            size = DETAIL_IMAGE_SIZE,
        )

        Spacer(
            modifier = Modifier.width(MultiverseSpacing.medium),
        )

        CharacterDetailInfo(
            character = character,
            stackMetadata = false,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CharacterDetailLargeTextLayout(
    character: CharacterDetail,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MultiverseSpacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CharacterPortrait(
            name = character.name,
            imageUrl = character.imageUrl,
            size = DETAIL_IMAGE_SIZE,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.medium),
        )

        CharacterDetailInfo(
            character = character,
            stackMetadata = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CharacterDetailInfo(
    character: CharacterDetail,
    stackMetadata: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = character.name,
            modifier = Modifier.semantics {
                heading()
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.medium),
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_status),
            value = character.status,
            stackVertically = stackMetadata,
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_species),
            value = character.species,
            stackVertically = stackMetadata,
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_origin),
            value = character.origin,
            stackVertically = stackMetadata,
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_location),
            value = character.location,
            stackVertically = stackMetadata,
        )
    }
}

@Composable
private fun CharacterMetadata(
    label: String,
    value: String,
    stackVertically: Boolean,
) {
    if (stackVertically) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(
                modifier = Modifier.height(MultiverseSpacing.extraSmall),
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(
                modifier = Modifier.width(MultiverseSpacing.medium),
            )

            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
            )
        }
    }

    Spacer(
        modifier = Modifier.height(MultiverseSpacing.small),
    )
}

@ThemePreviews
@FontScalePreviews
@Composable
private fun CharacterDetailHeaderPreview() {
    MultiverseTheme {
        CharacterDetailHeader(
            character = previewCharacterDetail,
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

private val previewCharacterDetail = CharacterDetail(
    id = 1,
    name = "Rick Sanchez",
    imageUrl = "",
    status = "Alive",
    species = "Human",
    origin = "Earth (C-137)",
    location = "Citadel of Ricks",
    episodes = emptyList(),
)

private const val LARGE_FONT_SCALE_THRESHOLD = 1.3f

private val DETAIL_IMAGE_SIZE = 132.dp
