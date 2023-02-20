package ru.gaket.themoviedb.domain.review.models

import ru.gaket.themoviedb.domain.movies.models.MovieId

data class CreateReviewForm(
    val movieId: MovieId,
    val whatLiked: String?,
    val whatDidNotLike: String?,
    val rating: Rating?,
) {

    companion object {

        fun newEmptyModelInstance(movieId: MovieId): CreateReviewForm =
            CreateReviewForm(
                movieId = movieId,
                whatLiked = null,
                whatDidNotLike = null,
                rating = null
            )

        fun dummyReviewInstance(movieId: MovieId): CreateReviewForm =
            CreateReviewForm(
                movieId = movieId,
                whatLiked = "It was awesome!",
                whatDidNotLike = "Still not as good as Android Academy",
                rating = null
            )
    }
}
