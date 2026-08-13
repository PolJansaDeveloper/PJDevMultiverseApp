package com.pjdev.presentation.characterlist.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pjdev.domain.model.Character
import com.pjdev.presentation.R
import com.pjdev.presentation.common.components.CharacterPortrait
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme
import com.pjdev.presentation.theme.ThemePreviews

@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = CARD_BORDER_WIDTH,
            color = MaterialTheme.colorScheme.outline,
        ),
    ) {
        CharacterCardContent(
            character = character,
        )
    }
}

@Composable
private fun CharacterCardContent(
    character: Character,
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
            size = CHARACTER_IMAGE_SIZE,
        )

        Spacer(
            modifier = Modifier.width(MultiverseSpacing.medium),
        )

        CharacterInfo(
            character = character,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CharacterInfo(
    character: Character,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = character.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.small),
        )

        Text(
            text = stringResource(
                R.string.character_episodes_count,
                character.episodeCount,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(
            modifier = Modifier.height(MultiverseSpacing.medium),
        )

        CharacterDetailsHint()
    }
}

@Composable
private fun CharacterDetailsHint() {
    /*
     * This is only a visual affordance. The Card itself already exposes
     * its click action to accessibility services.
     */
    Row(
        modifier = Modifier.clearAndSetSemantics {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(MultiverseSpacing.small)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )

        Spacer(
            modifier = Modifier.width(MultiverseSpacing.small),
        )

        Text(
            text = stringResource(R.string.character_view_details),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@ThemePreviews
@Composable
private fun CharacterCardPreview() {
    MultiverseTheme {
        CharacterCard(
            character = previewCharacter,
            onClick = {},
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

private val previewCharacter = Character(
    id = 1,
    name = "Rick Sanchez",
    imageUrl = "",
    episodeCount = 51,
)

private val CHARACTER_IMAGE_SIZE = 112.dp
private val CARD_BORDER_WIDTH = 1.dp
