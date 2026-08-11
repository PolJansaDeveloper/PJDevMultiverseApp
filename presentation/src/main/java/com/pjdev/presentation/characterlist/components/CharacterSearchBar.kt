package com.pjdev.presentation.characterlist.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.pjdev.presentation.R
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme

@Composable
fun CharacterSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusManager: FocusManager = LocalFocusManager.current,
) {
    val clearSearchDescription = stringResource(
        R.string.accessibility_search_clear,
    )

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = stringResource(
                    R.string.character_search_hint,
                ),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                // The clear action is exposed explicitly to accessibility services
                // instead of relying on the visual symbol to communicate its purpose.
                IconButton(
                    onClick = {
                        onQueryChange("")
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = clearSearchDescription
                    },
                ) {
                    Text(
                        text = CLEAR_SEARCH_SYMBOL,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                // Search requests are already driven by the debounced query Flow;
                // the keyboard action only dismisses the keyboard.
                focusManager.clearFocus()
            },
        ),
    )
}

@Preview(
    name = "Search bar - Light",
    showBackground = true,
)
@Composable
private fun CharacterSearchBarLightPreview() {
    MultiverseTheme(
        darkTheme = false,
    ) {
        CharacterSearchBar(
            query = "Rick",
            onQueryChange = {},
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

@Preview(
    name = "Search bar - Dark",
    showBackground = true,
)
@Composable
private fun CharacterSearchBarDarkPreview() {
    MultiverseTheme(
        darkTheme = true,
    ) {
        CharacterSearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

private const val CLEAR_SEARCH_SYMBOL = "×"
