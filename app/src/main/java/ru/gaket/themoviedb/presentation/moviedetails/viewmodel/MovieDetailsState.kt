package ru.gaket.themoviedb.presentation.moviedetails.viewmodel

import ru.gaket.themoviedb.core.navigation.Screen
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview

sealed class MovieDetailsState {

    data class Loading(val title: String) : MovieDetailsState()

    data class Result(
        val movie: Movie,
        val allReviews: List<MovieDetailsReview>,
    ) : MovieDetailsState()

    object Error : MovieDetailsState()
}

sealed class MovieDetailsEvent {

    sealed class OpenScreen : MovieDetailsEvent() {

        object Review : OpenScreen()

        object Auth : OpenScreen()
    }
}

data class MovieDetailsStateV2(
    val loadingTitle: String,
    val isMovieDetailsLoading: Boolean = false,
    val screenToNavigate: Screen,
    val moviePosterUrl: String = "",
    val movieTitle: String = "",
    val movieYear: String = "",
    val movieGenres: String = "",
    val movieRating: Int = 0,
    val movieOverview: String = "",
    val movieReviews: List<MovieDetailsReview> = emptyList(),
    val error: Throwable? = null,
)