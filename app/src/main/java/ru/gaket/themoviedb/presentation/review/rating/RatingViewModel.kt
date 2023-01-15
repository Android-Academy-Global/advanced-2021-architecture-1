package ru.gaket.themoviedb.presentation.review.rating

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.data.auth.AuthRepository
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.auth.User
import ru.gaket.themoviedb.domain.review.models.Rating
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.util.Result
import ru.gaket.themoviedb.util.doOnSuccess
import ru.gaket.themoviedb.util.exhaustive
import ru.gaket.themoviedb.util.mapNestedSuccess

class RatingViewModel @AssistedInject constructor(
    @Assisted private val createReviewScopedRepository: CreateReviewScopedRepository,
    private val authRepository: AuthRepository,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val _reviewState = MutableStateFlow(State())
    val state: StateFlow<State> get() = _reviewState.asStateFlow()

    init {
        viewModelScope.launch {
            createReviewScopedRepository.observeState()
                .map { state -> state.form.rating?.starsCount ?: 0 }
                .collect { rating ->
                    _reviewState.update { value ->
                        value.copy(rating = rating)
                    }
                }
        }
    }

    fun onRatingChange(ratingNumber: Int) {
        viewModelScope.launch {
            val rating = Rating.mapToRating(ratingNumber)
            if (rating != null) {
                createReviewScopedRepository.setRating(rating)
            }
        }
    }

    fun submit() {
        viewModelScope.launch {
            updateError(null)

            if (_reviewState.value.rating == 0) {
                updateError(R.string.review_error_zero_rating)
            } else {
                submitReview()
            }
        }
    }

    private suspend fun submitReview() {
        val originalState = _reviewState.value
        if (!originalState.showProgress) {
            _reviewState.update { value ->
                value.copy(showProgress = true)
            }

            val currentUser = authRepository.currentUser
            if (currentUser == null) {
                updateError(R.string.review_error_unknown)
            } else when (submitReview(currentUser)) {
                is Result.Success -> Unit
                is Result.Error -> updateError(R.string.review_error_unknown)
            }.exhaustive

            _reviewState.update { value ->
                value.copy(showProgress = false)
            }
        }
    }

    private fun updateError(@StringRes error: Int?) {
        _reviewState.update { value ->
            value.copy(error = error)
        }
    }

    private suspend fun submitReview(currentUser: User): Result<Unit, Throwable> =
        createReviewScopedRepository.buildDraft()
            .mapNestedSuccess { draft ->
                moviesRepository.addReview(
                    draft = draft,
                    authorId = currentUser.id,
                    authorEmail = currentUser.email
                )
            }
            .doOnSuccess { createReviewScopedRepository.markAsFinished() }

    data class State(
        val rating: Int = 0,
        val showProgress: Boolean = false,
        @StringRes val error: Int? = null,
    )

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): RatingViewModel
    }
}