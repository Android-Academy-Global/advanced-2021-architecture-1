package ru.gaket.themoviedb.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.gaket.themoviedb.core.navigation.screens.authScreen
import ru.gaket.themoviedb.core.navigation.screens.mainScreen
import ru.gaket.themoviedb.core.navigation.screens.mainScreenRoute
import ru.gaket.themoviedb.core.navigation.screens.movieDetailsScreen
import ru.gaket.themoviedb.core.navigation.screens.navigateToAuth
import ru.gaket.themoviedb.core.navigation.screens.navigateToMovieDetails

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    webNavigator: WebNavigator
) {
    NavHost(
        navController = navController,
        startDestination = mainScreenRoute,
        modifier = modifier
    ) {
        mainScreen(
            onMovieClick = {
                navController.navigateToMovieDetails(movieId = it.id, title = it.title)
            }
        )

        authScreen(onBack = navController::popBackStack)

        movieDetailsScreen(
            onNavigateToAuthScreen = {
                navController.navigateToAuth()
            },
            onNavigateToReviewScreen = {
                // далее навигация не сделана
            },
            onBackClick = navController::popBackStack,
            onWebSearchClick = { movieId ->
                webNavigator.navigateTo(movieId)
            }
        )
    }
}