package ru.gaket.themoviedb.presentation.moviedetails.viewmodel

import ru.gaket.themoviedb.core.navigation.Screen
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview

data class MovieDetailsState(
    val loadingTitle: String,
    val isMovieDetailsLoading: Boolean = false,
    val screenToNavigate: Screen? = null,
    val moviePosterUrl: String = "",
    val movieTitle: String = "",
    val movieYear: String = "",
    val movieGenres: String = "",
    val movieRating: Int = 0,
    val movieOverview: String = "",
    val movieReviews: List<MovieDetailsReview> = emptyList(),
    val error: Throwable? = null,
)