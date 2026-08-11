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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.presentation.R
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme
import androidx.compose.ui.text.style.TextAlign
import com.pjdev.presentation.common.components.CharacterPortrait

@Composable
fun CharacterDetailHeader(
    character: CharacterDetail,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
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
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CharacterDetailInfo(
    character: CharacterDetail,
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
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.medium),
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_status),
            value = character.status,
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_species),
            value = character.species,
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_origin),
            value = character.origin,
        )

        CharacterMetadata(
            label = stringResource(R.string.character_detail_location),
            value = character.location,
        )
    }
}

@Composable
private fun CharacterMetadata(
    label: String,
    value: String,
) {
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
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }

    Spacer(
        modifier = Modifier.height(MultiverseSpacing.small),
    )
}

@Preview(
    name = "Character detail header - Light",
    showBackground = true,
)
@Composable
private fun CharacterDetailHeaderLightPreview() {
    MultiverseTheme(
        darkTheme = false,
    ) {
        CharacterDetailHeader(
            character = previewCharacterDetail,
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

@Preview(
    name = "Character detail header - Dark",
    showBackground = true,
)
@Composable
private fun CharacterDetailHeaderDarkPreview() {
    MultiverseTheme(
        darkTheme = true,
    ) {
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

private val DETAIL_IMAGE_SIZE = 132.dp
