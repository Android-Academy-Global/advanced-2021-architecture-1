package ru.gaket.themoviedb.core.navigation.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsView
import java.lang.IllegalStateException

private const val movieIdKey = "movieId"
private const val titleKey = "title"

private const val baseRoute = "details"

/**
 * Example of navigation via [NavBackStackEntry.savedStateHandle]
 */
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.movieDetailsScreenV3(
    onNavigateToAuthScreen: () -> Unit,
    onNavigateToReviewScreen: (MovieId) -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: (MovieId) -> Unit
) {
    composable(
        route = baseRoute,
    ) { backStackEntry ->
        val movieId =
            backStackEntry.savedStateHandle.getLiveData<Long>(movieIdKey).observeAsState().value
        val title = backStackEntry.savedStateHandle.get<String>(titleKey).orEmpty()

        if (movieId != null) {
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
}

fun NavHostController.navigateToMovieDetailsV3(movieId: Long, title: String? = null) {
    navigate(baseRoute)
    currentBackStackEntry?.let {
        it.savedStateHandle[movieIdKey] = movieId
        it.savedStateHandle[titleKey] = title
    }
}