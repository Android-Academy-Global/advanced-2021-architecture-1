package ru.gaket.themoviedb.presentation.review.rating

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _reviewEvent = MutableSharedFlow<Event>()
    val event: LiveData<Event>
        get() = _reviewEvent
            .asLiveData(viewModelScope.coroutineContext)

    private val _reviewState = MutableStateFlow(State(rating = null, showProgress = false))
    val state: StateFlow<State> get() = _reviewState.asStateFlow()

    init {
        viewModelScope.launch {
            createReviewScopedRepository.observeState()
                .map { it.form.rating }
                .collect { rating ->
                    _reviewState.update { it.copy(rating = rating) }
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
            if (_reviewState.value.rating == null) {
                _reviewEvent.emit(Event.ERROR_ZERO_RATING)
            } else {
                submitReview()
            }
        }
    }

    private suspend fun submitReview() {
        val originalState = _reviewState.value
        if (!originalState.showProgress) {
            _reviewState.update {
                it.copy(showProgress = true)
            }

            val currentUser = authRepository.currentUser
            if (currentUser == null) {
                _reviewEvent.emit(Event.ERROR_USER_NOT_SIGNED)
            } else when (submitReview(currentUser)) {
                is Result.Success -> Unit
                is Result.Error -> _reviewEvent.emit(Event.ERROR_UNKNOWN)
            }.exhaustive


            _reviewState.update {
                it.copy(showProgress = false)
            }
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
        val rating: Rating?,
        val showProgress: Boolean,
    )

    enum class Event {
        ERROR_ZERO_RATING,
        ERROR_UNKNOWN,
        ERROR_USER_NOT_SIGNED
    }

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): RatingViewModel
    }
}