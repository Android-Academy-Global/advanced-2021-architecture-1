package ru.gaket.themoviedb.presentation.moviedetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.AuthScreen
import ru.gaket.themoviedb.core.navigation.ReviewScreen
import ru.gaket.themoviedb.core.navigation.Screen
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.auth.AuthInteractor
import ru.gaket.themoviedb.domain.auth.isAuthorized
import ru.gaket.themoviedb.domain.auth.observeIsAuthorized
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.movies.models.MovieWithReviews
import ru.gaket.themoviedb.domain.review.models.MyReview
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.model.getCalendarYear
import ru.gaket.themoviedb.util.Result

class MovieDetailsViewModel @AssistedInject constructor(
    private val moviesRepository: MoviesRepository,
    private val authInteractor: AuthInteractor,
    @Assisted val movieId: MovieId,
    @Assisted private val title: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {

        fun create(movieId: MovieId, title: String): MovieDetailsViewModel
    }

    private val _movieDetailsState = MutableStateFlow(
        MovieDetailsState(
            screenToNavigate = getScreenToNavigateOnReviewClick(),
            loadingTitle = title,
            isMovieDetailsLoading = true,
        )
    )
    val movieDetailsState: StateFlow<MovieDetailsState> = _movieDetailsState.asStateFlow()

    init {
        viewModelScope.launch {
            authInteractor
                .observeIsAuthorized()
                .flatMapLatest { isAuthorized ->
                    moviesRepository
                        .observeMovieDetailsWithReviews(movieId)
                        .map { details -> details to isAuthorized }
                }
                .distinctUntilChanged()
                .collect { (detailsResult, isAuthorized) ->
                    handleLoadingMovieDetailsResult(detailsResult, isAuthorized)
                }
        }
    }

    private fun handleLoadingMovieDetailsResult(
        detailsResult: Result<MovieWithReviews, Throwable>,
        isAuthorized: Boolean,
    ) {
        when (detailsResult) {
            is Result.Success -> emitSuccessState(detailsResult.result, isAuthorized)
            is Result.Error -> emitErrorState(detailsResult)
        }
    }

    private fun emitSuccessState(
        details: MovieWithReviews,
        isAuthorized: Boolean,
    ) {
        val movieReviews = extractMovieReviewsFromMovieDetails(details, isAuthorized)
        val movie = details.movie

        _movieDetailsState.update { state ->
            state.copy(
                screenToNavigate = getScreenToNavigateOnReviewClick(),
                moviePosterUrl = movie.thumbnail,
                movieTitle = movie.title,
                movieYear = movie.releaseDate.getCalendarYear()?.toString().orEmpty(),
                movieGenres = movie.genres,
                movieRating = movie.rating,
                movieOverview = movie.overview,
                movieReviews = movieReviews,
                error = null,
                isMovieDetailsLoading = false,
            )
        }
    }

    private fun extractMovieReviewsFromMovieDetails(
        details: MovieWithReviews,
        isAuthorized: Boolean,
    ): List<MovieDetailsReview> {
        val myReviewOrAddButton = getReviewExistingOrAddButton(details.myReview, isAuthorized)
        val someoneReviews = details.someoneElseReviews.map { item ->
            MovieDetailsReview.Existing.Someone(item, item.author.value)
        }

        return mutableListOf<MovieDetailsReview>().apply {
            add(myReviewOrAddButton)
            addAll(someoneReviews)
        }
    }

    private fun getReviewExistingOrAddButton(
        myReview: MyReview?,
        isAuthorized: Boolean,
    ): MovieDetailsReview {
        val myReviewOrAddButton = if (myReview != null) {
            MovieDetailsReview.Existing.My(myReview.review, R.string.review_my_review)
        } else {
            MovieDetailsReview.Add(if (isAuthorized) R.string.review_add_label else R.string.authiorize_to_add_review_label)
        }

        return myReviewOrAddButton
    }

    private fun emitErrorState(detailsResult: Result.Error<Throwable>) {
        _movieDetailsState.update { state ->
            state.copy(
                screenToNavigate = getScreenToNavigateOnReviewClick(),
                moviePosterUrl = "",
                movieTitle = "",
                movieYear = "",
                movieGenres = "",
                movieRating = 0,
                movieOverview = "",
                movieReviews = emptyList(),
                error = detailsResult.result,
                isMovieDetailsLoading = false,
            )
        }
    }

    private fun getScreenToNavigateOnReviewClick(): Screen {
        return if (authInteractor.isAuthorized()) {
            ReviewScreen(movieId)
        } else {
            AuthScreen()
        }
    }

    fun toggleLikeState() {
        _movieDetailsState.update {
            it.copy(liked = !it.liked)
        }
    }

    fun startAnimation() {
        _movieDetailsState.update {
            it.copy(animationIsActive = true)
        }
    }

    fun stopAnimation() {
        _movieDetailsState.update {
            it.copy(animationIsActive = false)
        }
    }
}
