package ru.gaket.themoviedb.data.movies.remote

import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.util.OperationResult
import ru.gaket.themoviedb.util.doOnError
import ru.gaket.themoviedb.util.runOperationCatching
import timber.log.Timber
import javax.inject.Inject

//todo: revisit this class and if useless remove
interface MoviesRemoteDataSource {

    suspend fun searchMovies(query: String, page: Int): OperationResult<List<SearchMovieDto>, Throwable>

    suspend fun getMovieDetails(id: MovieId): OperationResult<DetailsMovieDto, Throwable>
}

//todo: revisit this class and if useless remove
class MoviesRemoteDataSourceImpl @Inject constructor(
    private val moviesApi: MoviesApi,
) : MoviesRemoteDataSource {

    override suspend fun searchMovies(query: String, page: Int): OperationResult<List<SearchMovieDto>, Throwable> =
        runOperationCatching { moviesApi.searchMovie(query, page).searchMovies }
            .doOnError { error -> Timber.e("Search movies from server error", error) }

    override suspend fun getMovieDetails(id: MovieId): OperationResult<DetailsMovieDto, Throwable> =
        runOperationCatching { moviesApi.getMovieDetails(id) }
            .doOnError { error -> Timber.e("getMovieDetails from server error", error) }
}