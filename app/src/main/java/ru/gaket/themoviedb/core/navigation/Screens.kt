package ru.gaket.themoviedb.core.navigation

import androidx.fragment.app.Fragment
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.presentation.auth.view.ComposeAuthFragment
import ru.gaket.themoviedb.presentation.moviedetails.view.ComposeMovieDetailsFragment
import ru.gaket.themoviedb.presentation.movies.view.ComposeMoviesFragment
import ru.gaket.themoviedb.presentation.review.ReviewFragment

interface Screen {

    fun destination(): Fragment

    val tag: String? get() = null
}

class MoviesScreen : Screen {

    override fun destination(): Fragment = ComposeMoviesFragment.newInstance()
}

class MovieDetailsScreen(
    private val movieId: Long,
    private val title: String,
) : Screen {

    override fun destination(): Fragment = ComposeMovieDetailsFragment.newInstance(movieId, title)

    override val tag: String get() = TAG

    companion object {

        const val TAG = "MovieDetailsScreen"
    }
}

class AuthScreen : Screen {

    override fun destination(): Fragment = ComposeAuthFragment.newInstance()
}

data class ReviewScreen(
    private val movieId: MovieId,
) : Screen {

    override fun destination(): Fragment = ReviewFragment.newInstance(movieId)
}
