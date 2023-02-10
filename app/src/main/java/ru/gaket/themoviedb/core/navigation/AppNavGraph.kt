package ru.gaket.themoviedb.core.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        startDestination = MoviesScreen.registrationRoute,
        modifier = modifier
    ) {
        composable(MoviesScreen.registrationRoute) {
            MoviesView(
                onMovieClick = {
                    navController.navigate(
                        MovieDetailsScreen(movieId = it.id, title = it.title).route
                    )
                }
            )
        }

        composable(AuthScreen.registrationRoute) {
            AuthView(
                onAuthorized = navController::popBackStack
            )
        }

        composable(
            MovieDetailsScreen.registrationRoute,
            arguments = listOf(
                navArgument(MovieDetailsScreen.movieIdKey) { type = NavType.LongType },
                navArgument(MovieDetailsScreen.titleKey) { type = NavType.StringType }
            )
        ) { backStack ->
            val movieId = backStack.arguments?.getLong(MovieDetailsScreen.movieIdKey)
                ?: throw IllegalStateException()
            val title = backStack.arguments?.getString(MovieDetailsScreen.titleKey).orEmpty()

            MovieDetailsView(
                movieId = movieId,
                loadingTitle = title,
                onNavigateIntent = { screenToNavigate ->
                    navController.navigate(screenToNavigate.route)
                },
                onBackClick = navController::popBackStack,
                onWebSearchClick = {
                    webNavigator.navigateTo(movieId)
                }
            )
        }
    }
}