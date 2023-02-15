package ru.gaket.themoviedb.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.gaket.themoviedb.core.navigation.screens.authScreen
import ru.gaket.themoviedb.core.navigation.screens.mainScreen
import ru.gaket.themoviedb.core.navigation.screens.mainScreenRoute
import ru.gaket.themoviedb.core.navigation.screens.movieDetailsScreen
import ru.gaket.themoviedb.core.navigation.screens.navigateToAuth
import ru.gaket.themoviedb.core.navigation.screens.navigateToMovieDetails
import ru.gaket.themoviedb.presentation.auth.view.AuthView
import ru.gaket.themoviedb.presentation.moviedetails.view.ComposeMovieDetailsFragment
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsView
import ru.gaket.themoviedb.presentation.moviedetails.viewmodel.MovieDetailsViewModel
import ru.gaket.themoviedb.presentation.movies.view.MoviesView
import java.lang.IllegalStateException

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