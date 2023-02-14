package ru.gaket.themoviedb.core.navigation.screens

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.gaket.themoviedb.core.navigation.MovieDetailsScreen
import ru.gaket.themoviedb.core.navigation.Screen
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.movies.models.SearchMovie
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsView
import ru.gaket.themoviedb.presentation.movies.view.MoviesView
import java.lang.IllegalStateException

private const val movieIdKey = "movieId"
private const val titleKey = "title"

private const val registrationRoute: String = "details/{$movieIdKey}/{$titleKey}"

fun NavGraphBuilder.movieDetailsScreen(
    onNavigateToAuthScreen: () -> Unit,
    onNavigateToReviewScreen: (MovieId) -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: (MovieId) -> Unit
) {
    composable(
        registrationRoute,
        arguments = listOf(
            navArgument(movieIdKey) { type = NavType.LongType },
            navArgument(titleKey) { type = NavType.StringType }
        )
    ) { backStack ->
        val movieId = backStack.arguments?.getLong(movieIdKey)
            ?: throw IllegalStateException()
        val title = backStack.arguments?.getString(titleKey).orEmpty()

        MovieDetailsView(
            movieId = movieId,
            loadingTitle = title,
            onNavigateToAuthScreen = onNavigateToAuthScreen,
            onNavigateToReviewScreen = onNavigateToReviewScreen,
            onBackClick = onBackClick,
            onWebSearchClick = {
                onWebSearchClick(movieId)
            }
        )
    }
}

fun NavHostController.navigateToMovieDetails(movieId: Long, title: String) {
    navigate("details/$movieId/${title}")
}