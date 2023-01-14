package ru.gaket.themoviedb.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.domain.review.models.CreateReviewState
import ru.gaket.themoviedb.domain.review.repository.CreateReviewScopedRepository
import ru.gaket.themoviedb.util.Result
import ru.gaket.themoviedb.util.emitIfActive

class ReviewViewModel @AssistedInject constructor(
    @Assisted private val createReviewScopedRepository: CreateReviewScopedRepository,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    val state: StateFlow<State> = observeState()

    fun toPreviousStep() = createReviewScopedRepository.toPreviousStep()

    private fun observeState(): StateFlow<State> =
        combine(
            flow {
                val details = moviesRepository.getMovieDetails(createReviewScopedRepository.movieId)
                emitIfActive(details)
            },
            createReviewScopedRepository.observeState()
        ) { movieResult, createReviewState ->
            when (movieResult) {
                is Result.Success -> State(movieResult.result, createReviewState)
                is Result.Error -> State()
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, State())

    data class State(
        val movie: Movie? = null,
        val createState: CreateReviewState? = null,
    )

    @AssistedFactory
    interface Factory {

        fun create(createReviewScopedRepository: CreateReviewScopedRepository): ReviewViewModel
    }
}