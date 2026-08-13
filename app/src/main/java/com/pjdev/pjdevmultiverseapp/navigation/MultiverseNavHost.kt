package com.pjdev.pjdevmultiverseapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pjdev.presentation.characterdetail.ui.CharacterDetailRoute
import com.pjdev.presentation.characterdetail.viewmodel.CharacterDetailViewModel
import com.pjdev.presentation.characterlist.ui.CharacterListRoute
import com.pjdev.presentation.characterlist.viewmodel.CharacterListViewModel

@Composable
fun MultiverseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = CharacterListDestination,
        modifier = modifier,
    ) {
        composable<CharacterListDestination> {
            val viewModel: CharacterListViewModel = hiltViewModel()

            CharacterListRoute(
                viewModel = viewModel,
                onCharacterClick = { characterId ->
                    navController.navigate(
                        CharacterDetailDestination(
                            characterId = characterId,
                        ),
                    )
                },
            )
        }

        composable<CharacterDetailDestination> { backStackEntry ->
            val destination =
                backStackEntry.toRoute<CharacterDetailDestination>()

            val viewModel: CharacterDetailViewModel = hiltViewModel()

            /*
             * Only the character ID travels between destinations.
             * Navigation Compose serializes and restores the typed argument.
             */
            CharacterDetailRoute(
                characterId = destination.characterId,
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
