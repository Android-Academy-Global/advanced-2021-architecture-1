package ru.gaket.themoviedb.presentation.movies.viewmodel

import ru.gaket.themoviedb.domain.movies.models.SearchMovie
import ru.gaket.themoviedb.domain.movies.models.SearchMovieWithMyReview

/**
 * Class containing the result of the [SearchMovie] request
 */
data class MoviesResult(
    val query: String = "",
    val isMoviesLoading: Boolean = false,
    val resultPlaceholder: Int? = null,
    val movies: List<SearchMovieWithMyReview> = emptyList(),
    val error: Throwable? = null,
)
