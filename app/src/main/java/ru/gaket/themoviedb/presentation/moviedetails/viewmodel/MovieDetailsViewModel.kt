package ru.gaket.themoviedb.presentation.moviedetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.gaket.themoviedb.R
import ru.gaket.themoviedb.core.navigation.WebNavigator
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.domain.auth.AuthInteractor
import ru.gaket.themoviedb.domain.auth.User
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.domain.review.models.MyReview
import ru.gaket.themoviedb.domain.review.models.SomeoneReview
import ru.gaket.themoviedb.domain.review.repository.ReviewRepository
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsEvent
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsEvent.ShowErrorEvent
import ru.gaket.themoviedb.presentation.moviedetails.model.MovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.model.toMovieDetailsReview
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsFragment.Companion.ARG_MOVIE_ID
import ru.gaket.themoviedb.presentation.moviedetails.view.MovieDetailsFragment.Companion.ARG_MOVIE_TITLE
import ru.gaket.themoviedb.util.get
import ru.gaket.themoviedb.util.mapSuccess
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val reviewRepository: ReviewRepository,
    private val webNavigator: WebNavigator,
    private val authInteractor: AuthInteractor,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val movieId = savedStateHandle.get<Long>(ARG_MOVIE_ID) ?: error("No movie id was passed!")
    private val title = savedStateHandle.get<String>(ARG_MOVIE_TITLE).orEmpty()

    private var _state = MutableLiveData(MovieDetailsState(title = title))
    val state: LiveData<MovieDetailsState> get() = _state

    private var _events = MutableSharedFlow<MovieDetailsEvent>()
    val events: SharedFlow<MovieDetailsEvent> get() = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            moviesRepository.getMovieDetails(movieId).get(
                onSuccess = ::handleMovie,
                onError = {
                    ShowErrorEvent(R.string.error_getting_movie_info).publish()
                    reduceState { copy(isLoadingInfo = false) }
                },
            )
        }

        viewModelScope.launch {
            reviewRepository.getSomeoneReviews(movieId)
                .mapSuccess(::mapOnlySomeoneReviews)
                .get(
                    onSuccess = ::handleSomeoneReviews,
                    onError = {
                        ShowErrorEvent(R.string.error_getting_movie_info).publish()
                        reduceState { copy(isLoadingReviews = false) }
                    },
                )
        }

        viewModelScope.launch {
            authInteractor.observeCurrentUser()
                .filterNotNull()
                .flatMapLatest { user -> reviewRepository.getMyReviews(movieId).map { review -> user to review } }
                .collect { (user, myReview) -> handleMyReview(user, myReview) }
        }

        viewModelScope.launch {
            authInteractor.observeCurrentUser()
                .filter { user -> user == null }
                .collect { handleAuthorization() }
        }
    }

    private fun mapOnlySomeoneReviews(reviews: List<SomeoneReview>): List<MovieDetailsReview.SomeoneReview> {
        val user = authInteractor.getCurrentUser()

        return reviews
            .filter { review -> review.author != user?.email }
            .map { review -> review.toMovieDetailsReview() }
    }

    fun onBrowseMovieClick() = webNavigator.navigateTo(movieId)

    fun onReviewClick() = Unit

    @Synchronized
    private fun reduceState(reducer: MovieDetailsState.() -> MovieDetailsState) {
        val value = _state.value
        requireNotNull(value)

        _state.value = value.reducer()
    }

    private fun handleMovie(movie: Movie) = reduceState {
        copy(
            isLoadingInfo = false,
            year = movie.releaseDate.toDate()?.getCalendarYear()?.toString().orEmpty(),
            overview = movie.overview,
            genres = movie.genres,
            rating = movie.rating.toString(),
            posterUrl = movie.thumbnail,
        )
    }

    private fun String.toDate(): Date? = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this)

    private fun Date.getCalendarYear(): Int = Calendar.getInstance().run {
        time = this@getCalendarYear
        get(Calendar.YEAR)
    }

    private fun handleSomeoneReviews(reviews: List<MovieDetailsReview.SomeoneReview>) = reduceState {
        copy(
            isLoadingReviews = false,
            someoneReviews = reviews,
        )
    }

    private fun handleMyReview(user: User, myReview: MyReview?) = reduceState {
        val review = if (myReview != null) {
            MovieDetailsReview.MyReview(myReview.review, isAuthorized = true)
        } else {
            MovieDetailsReview.MyReview(isAuthorized = true)
        }

        copy(
            myReview = review,
            someoneReviews = someoneReviews.filter { someoneReview ->
                user.email.value.startsWith(someoneReview.userName).not()
            },
        )
    }

    private fun handleAuthorization() = reduceState {
        copy(myReview = MovieDetailsReview.MyReview(isAuthorized = false))
    }

    private suspend fun MovieDetailsEvent.publish() = _events.emit(this@publish)
}
