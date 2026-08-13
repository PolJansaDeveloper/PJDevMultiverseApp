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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import com.pjdev.presentation.R
import com.pjdev.presentation.theme.MultiverseSpacing
import com.pjdev.presentation.theme.MultiverseTheme
import com.pjdev.presentation.theme.ThemePreviews

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
        label = {
            Text(
                text = stringResource(
                    R.string.character_search_hint,
                ),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = clearSearchDescription
                    },
                ) {
                    /*
                     * The symbol is purely visual. The IconButton exposes
                     * the meaningful accessibility description.
                     */
                    Text(
                        text = CLEAR_SEARCH_SYMBOL,
                        modifier = Modifier.clearAndSetSemantics {},
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
                /*
                 * Search requests are driven by the debounced query Flow,
                 * so this action only dismisses the keyboard.
                 */
                focusManager.clearFocus()
            },
        ),
    )
}

@ThemePreviews
@Composable
private fun CharacterSearchBarEmptyPreview() {
    MultiverseTheme {
        CharacterSearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

@ThemePreviews
@Composable
private fun CharacterSearchBarFilledPreview() {
    MultiverseTheme {
        CharacterSearchBar(
            query = "Rick",
            onQueryChange = {},
            modifier = Modifier.padding(MultiverseSpacing.medium),
        )
    }
}

private const val CLEAR_SEARCH_SYMBOL = "×"
