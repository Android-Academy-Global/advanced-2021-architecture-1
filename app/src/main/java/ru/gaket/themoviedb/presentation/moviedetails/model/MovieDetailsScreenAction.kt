package ru.gaket.themoviedb.presentation.moviedetails.model

import ru.gaket.themoviedb.domain.movies.models.MovieId

sealed class  MovieDetailsScreenAction {
    object NavigateToAuthScreen : MovieDetailsScreenAction()
    class NavigateToReviewScreen(val movieId: MovieId) : MovieDetailsScreenAction()
}