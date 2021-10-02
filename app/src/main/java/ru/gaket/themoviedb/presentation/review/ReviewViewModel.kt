package ru.gaket.themoviedb.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.review.model.ReviewFormModel
import ru.gaket.themoviedb.domain.review.repository.ReviewRepository
import ru.gaket.themoviedb.presentation.review.ReviewViewModel.ReviewState.END_STATE
import ru.gaket.themoviedb.presentation.review.ReviewViewModel.ReviewState.RATING
import ru.gaket.themoviedb.presentation.review.ReviewViewModel.ReviewState.WHAT_LIKED
import ru.gaket.themoviedb.presentation.review.ReviewViewModel.ReviewState.WHAT_NOT_LIKED
import ru.gaket.themoviedb.util.OperationResult.Error
import ru.gaket.themoviedb.util.OperationResult.Success
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val reviewRepository: ReviewRepository,
    savedState: SavedStateHandle,
) : ViewModel() {

    private val _reviewState = MutableStateFlow(WHAT_LIKED)

    val currentReview: LiveData<Pair<Movie, ReviewFormModel>?>

    //TODO Check screen rotation
    val reviewState: LiveData<ReviewState> get() = _reviewState.asLiveData(viewModelScope.coroutineContext)

    init {
        val movieId: MovieId = savedState.get<MovieId>(ARG_MOVIE_ID) ?: error("You need to provide $ARG_MOVIE_ID")

        viewModelScope.launch {
            reviewRepository.clearState()
            reviewRepository.setMovieId(movieId)
        }

        currentReview = flow {
            val moviePair = when (val movie = moviesRepository.getMovieDetails(movieId)) {
                is Success -> movie.result
                is Error -> null
            }
            emit(moviePair)
        }
            .combine(reviewRepository.reviewState) { movie: Movie?, review: ReviewFormModel -> movie?.let { movie to review } }
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun nextState() {
        _reviewState.value = when (_reviewState.value) {
            WHAT_LIKED -> WHAT_NOT_LIKED
            WHAT_NOT_LIKED -> RATING
            RATING -> END_STATE
            END_STATE -> throw IllegalStateException("You can't use goNext() at END_STATE")
        }
    }

    enum class ReviewState {
        WHAT_LIKED,
        WHAT_NOT_LIKED,
        RATING,
        END_STATE
    }

    companion object {

        const val ARG_MOVIE_ID = "ARG_MOVIE_ID"
    }
}