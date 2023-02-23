package ru.gaket.themoviedb.core.navigation.screens

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsView
import java.lang.IllegalStateException

private const val movieIdKey = "movieId"
private const val titleKey = "title"

private const val baseRoute = "details"
private const val movieDetailsRoute: String = "$baseRoute/{$movieIdKey}?titleKey={$titleKey}"
private const val appLinkPath = "moviedb"

fun NavGraphBuilder.movieDetailsScreen(
    onNavigateToAuthScreen: () -> Unit,
    onNavigateToReviewScreen: (MovieId) -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: (MovieId) -> Unit
) {
    composable(
        route = movieDetailsRoute,
        arguments = listOf(
            navArgument(movieIdKey) { type = NavType.LongType },
            navArgument(titleKey) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        ),
        deepLinks = listOf(navDeepLink { uriPattern = "$appLinkPath://$movieDetailsRoute" })
    ) { backStackEntry ->
        val movieId = backStackEntry.arguments?.getLong(movieIdKey)
            ?: throw IllegalStateException()
        val title = backStackEntry.arguments?.getString(titleKey).orEmpty()

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

fun NavHostController.navigateToMovieDetails(movieId: Long, title: String? = null) {
    navigate("$baseRoute/$movieId?titleKey=${title}")
}