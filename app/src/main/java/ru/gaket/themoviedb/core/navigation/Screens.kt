package ru.gaket.themoviedb.core.navigation

import androidx.compose.runtime.Immutable
import androidx.fragment.app.Fragment
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.presentation.auth.view.ComposeAuthFragment
import ru.gaket.themoviedb.presentation.moviedetails.view.ComposeMovieDetailsFragment
import ru.gaket.themoviedb.presentation.movies.view.ComposeMoviesFragment
import ru.gaket.themoviedb.presentation.movies.view.MoviesView
import ru.gaket.themoviedb.presentation.review.ReviewFragment

interface Screen {

    fun destination(): Fragment

    val tag: String? get() = null

    val route: String
}

@Immutable
class MoviesScreen : Screen {

    override fun destination(): Fragment = ComposeMoviesFragment.newInstance()

    override val route: String = "main"
}

data class MovieDetailsScreen(
    private val movieId: Long,
    private val title: String,
) : Screen {
    override fun destination(): Fragment = ComposeMovieDetailsFragment.newInstance(movieId, title)

    override val tag: String get() = TAG

    override val route: String = "details/$movieId/$title"

    companion object {

        val movieIdKey = "movieIdKey"
        val titleKey = "title"

        val registrationRoute: String = "details/{$movieIdKey}/{$titleKey}"

        const val TAG = "MovieDetailsScreen"
    }
}

@Immutable
class AuthScreen : Screen {

    override fun destination(): Fragment = ComposeAuthFragment.newInstance()
    override val route: String = "auth"
}

data class ReviewScreen(
    private val movieId: MovieId,
) : Screen {

    override fun destination(): Fragment = ReviewFragment.newInstance(movieId)

    override val route: String = "review/$movieId"
}
