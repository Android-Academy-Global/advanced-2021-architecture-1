package ru.gaket.themoviedb.presentation.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.movies.models.SearchMovieWithMyReview
import ru.gaket.themoviedb.util.EMPTY
import ru.gaket.themoviedb.util.Result.Error
import ru.gaket.themoviedb.util.Result.Success
import javax.inject.Inject

private const val TEXT_ENTERED_DEBOUNCE_MILLIS = 500L

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
) : ViewModel() {

    private val queryFlow = MutableStateFlow(String.EMPTY)

    private val _searchResult = MutableStateFlow(MoviesResult())
    val searchResult: StateFlow<MoviesResult> = _searchResult
        .asStateFlow()

    init {
        viewModelScope.launch {
            queryFlow
                .sample(TEXT_ENTERED_DEBOUNCE_MILLIS)
                .mapLatest(::handleQuery)
                .collect()
        }
    }

    fun onNewQuery(query: String) {
        _searchResult.update { value ->
            value.copy(
                query = query,
                error = null,
            )
        }
        queryFlow.value = query
    }

    private suspend fun handleQuery(query: String) {
        emitShowOrHideProgress(query)

        if (query.isNotEmpty()) {
            handleSearchMovie(query)
        }
    }

    private fun emitShowOrHideProgress(query: String) {
        _searchResult.update { value ->
            if (query.isEmpty()) {
                value.copy(
                    isMoviesLoading = false,
                    movies = emptyList(),
                    resultPlaceholder = R.string.movies_placeholder,
                )
            } else {
                value.copy(
                    isMoviesLoading = true,
                )
            }
        }
    }

    private suspend fun handleSearchMovie(query: String) {
        // emulate a small delay in delivering the movies as it can be quite fast
        delay(1_000L)

        when (val moviesResult = moviesRepository.searchMoviesWithReviews(query)) {
            is Error -> emitErrorState()
            is Success -> emitSuccessState(moviesResult.result)
        }
    }

    private fun emitErrorState() {
        _searchResult.update { value ->
            value.copy(
                isMoviesLoading = false,
                movies = emptyList(),
                error = IllegalArgumentException("Search movies from server error!"),
                resultPlaceholder = R.string.search_error,
            )
        }
    }

    private fun emitSuccessState(movies: List<SearchMovieWithMyReview>) {
        _searchResult.update { value ->
            value.copy(
                isMoviesLoading = false,
                movies = movies,
                resultPlaceholder = if (movies.isEmpty()) R.string.empty_result else null,
            )
        }
    }
}
