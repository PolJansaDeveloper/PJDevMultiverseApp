package com.pjdev.pjdevmultiverseapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
        startDestination = AppRoute.CHARACTER_LIST,
        modifier = modifier,
    ) {
        composable(
            route = AppRoute.CHARACTER_LIST,
        ) {
            val viewModel: CharacterListViewModel = hiltViewModel()

            CharacterListRoute(
                viewModel = viewModel,
                onCharacterClick = { characterId ->
                    navController.navigate(
                        AppRoute.characterDetail(characterId),
                    )
                },
            )
        }

        composable(
            route = AppRoute.CHARACTER_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(AppRoute.CHARACTER_ID_ARGUMENT) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val characterId = requireNotNull(
                backStackEntry.arguments?.getInt(
                    AppRoute.CHARACTER_ID_ARGUMENT,
                ),
            )

            val viewModel: CharacterDetailViewModel = hiltViewModel()

            // Only the character ID is passed between destinations.
            // The detail screen loads its own fresh data through its ViewModel.
            CharacterDetailRoute(
                characterId = characterId,
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
