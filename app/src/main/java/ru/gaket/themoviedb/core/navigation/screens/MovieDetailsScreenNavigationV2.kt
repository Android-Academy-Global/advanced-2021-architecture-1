package ru.gaket.themoviedb.core.navigation.screens

import android.util.Base64
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsView
import java.lang.IllegalStateException

private const val paramKey = "param"

private const val baseRoute = "details"
private const val movieDetailsRoute: String = "$baseRoute/{$paramKey}"

private data class MovieDetailsScreenParams(
    @SerializedName("id")
    val movieId: Long,

    @SerializedName("title")
    val title: String? = null
)

/**
 * Example of navigation to movie details screen by using complex param with Base64 and GSON
 */
fun NavGraphBuilder.movieDetailsScreenV2(
    onNavigateToAuthScreen: () -> Unit,
    onNavigateToReviewScreen: (MovieId) -> Unit,
    onBackClick: () -> Unit,
    onWebSearchClick: (MovieId) -> Unit
) {
    composable(
        route = movieDetailsRoute,
        arguments = listOf(
            navArgument(paramKey) {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val paramJson = Base64.decode(
            backStackEntry.arguments?.getString(paramKey),
            Base64.URL_SAFE
        ).toString(Charsets.UTF_8)

        val param = Gson().fromJson(
            paramJson,
            MovieDetailsScreenParams::class.java
        )
        val movieId = param.movieId
        val title = param.title.orEmpty()

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

fun NavHostController.navigateToMovieDetailsV2(movieId: Long, title: String? = null) {
    val screenParams = MovieDetailsScreenParams(movieId, title)
    val param: String = Gson().toJson(screenParams)
    val paramInBase64 = Base64.encodeToString(param.toByteArray(Charsets.UTF_8), Base64.URL_SAFE)
    navigate("$baseRoute/$paramInBase64")
}