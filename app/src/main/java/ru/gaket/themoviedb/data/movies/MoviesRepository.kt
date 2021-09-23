package ru.gaket.themoviedb.data.movies

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.gaket.themoviedb.data.movies.local.MovieEntity
import ru.gaket.themoviedb.data.movies.local.MoviesDao
import ru.gaket.themoviedb.data.movies.local.SearchMovieEntity
import ru.gaket.themoviedb.data.movies.remote.MoviesRemoteDataSource
import ru.gaket.themoviedb.data.review.local.MyReviewsDao
import ru.gaket.themoviedb.data.review.local.toEntity
import ru.gaket.themoviedb.data.review.local.toModel
import ru.gaket.themoviedb.data.review.remote.ReviewsRemoteDataSource
import ru.gaket.themoviedb.di.BaseImageUrlQualifier
import ru.gaket.themoviedb.domain.auth.User
import ru.gaket.themoviedb.domain.auth.User.Email
import ru.gaket.themoviedb.domain.auth.User.Id
import ru.gaket.themoviedb.domain.movies.models.Movie
import ru.gaket.themoviedb.domain.movies.models.MovieId
import ru.gaket.themoviedb.domain.movies.models.MovieWithReviews
import ru.gaket.themoviedb.domain.movies.models.SearchMovie
import ru.gaket.themoviedb.domain.movies.models.SearchMovieWithMyReview
import ru.gaket.themoviedb.domain.movies.toModel
import ru.gaket.themoviedb.domain.review.AddReviewRequest
import ru.gaket.themoviedb.domain.review.MyReview
import ru.gaket.themoviedb.domain.review.SomeoneReview
import ru.gaket.themoviedb.util.OperationResult
import ru.gaket.themoviedb.util.VoidOperationResult
import ru.gaket.themoviedb.util.doOnSuccess
import ru.gaket.themoviedb.util.mapNestedSuccess
import ru.gaket.themoviedb.util.mapSuccess
import ru.gaket.themoviedb.util.toSuccessOrErrorList
import timber.log.Timber
import javax.inject.Inject

interface MoviesRepository {

    /**
     * Search [SearchMovieEntity]s for the given [query] string
     *
     * @return List of [SearchMovieEntity], null as error, or empty list.
     * No Throwable.
     */
    suspend fun searchMoviesWithReviews(
        query: String,
        page: Int,
    ): OperationResult<List<SearchMovieWithMyReview>, Throwable>

    /**
     * Get [MovieEntity] by movieId
     *
     * @return List of [SearchMovieEntity], null as error, or empty list.
     * No Throwable.
     */
    suspend fun getMovieDetails(id: MovieId): OperationResult<Movie, Throwable>

    suspend fun getMovieDetailsWithReviews(id: MovieId): OperationResult<MovieWithReviews, Throwable>

    suspend fun addReview(
        request: AddReviewRequest,
        authorId: User.Id,
        authorEmail: User.Email,
    ): VoidOperationResult<Throwable>

    //todo: think about naming
    suspend fun sync(userId: User.Id)

    //todo: think about naming
    suspend fun unSync()
}

private suspend fun MoviesRepository.getMovieDetailsList(ids: Set<MovieId>): OperationResult<List<Movie>, List<Throwable>> =
    coroutineScope {
        val asyncCalls = ids.map { singleId ->
            async { getMovieDetails(singleId) }
        }

        asyncCalls.awaitAll()
            .toSuccessOrErrorList()
    }

/**
 * Repository providing data about [MovieEntity], [SearchMovieEntity]
 */
class MoviesRepositoryImpl @Inject constructor(
    private val moviesRemoteDataSource: MoviesRemoteDataSource,
    private val moviesDao: MoviesDao,
    private val reviewsRemoteDataSource: ReviewsRemoteDataSource,
    private val myReviewsDao: MyReviewsDao,
    @BaseImageUrlQualifier private val baseImageUrl: String,
) : MoviesRepository {

    override suspend fun searchMoviesWithReviews(
        query: String,
        page: Int,
    ): OperationResult<List<SearchMovieWithMyReview>, Throwable> =
        searchMovies(query, page)
            .mapSuccess { movies ->
                val movieIds = movies.map { singleMovie -> singleMovie.id }

                val myReviews = getMyReviewsForMovies(movieIds)

                movies.map { singleMovie ->
                    SearchMovieWithMyReview(
                        movie = singleMovie,
                        myReview = myReviews[singleMovie.id]
                    )
                }
            }

    override suspend fun getMovieDetails(id: MovieId): OperationResult<Movie, Throwable> {
        val cachedMovie = moviesDao.getById(id)
        val entityResult = if (cachedMovie?.isUpdatedFromServer == true) {
            OperationResult.Success(cachedMovie)
        } else {
            moviesRemoteDataSource.getMovieDetails(id)
                .mapSuccess { dto ->
                    dto.toEntity(
                        baseImageUrl,
                        hasReview = cachedMovie?.hasReview == true,
                        reviewId = cachedMovie?.reviewId ?: 0
                    )
                }
                .doOnSuccess { entity -> moviesDao.insert(entity) }
        }

        return entityResult.mapSuccess { entity -> entity.toModel() }
    }

    override suspend fun getMovieDetailsWithReviews(id: MovieId): OperationResult<MovieWithReviews, Throwable> {
        val (details, allReviewsResult, myReview) = getMovieDetailsWithReviewsTriple(id)

        return details.mapNestedSuccess { movie ->
            allReviewsResult.mapSuccess { allReviews ->
                MovieWithReviews(
                    movie = movie,
                    someoneElseReviews = allReviews.filter { someoneReview -> someoneReview.review.id != myReview?.review?.id },
                    myReview = myReview
                )
            }
        }
    }

    override suspend fun addReview(
        request: AddReviewRequest,
        authorId: Id,
        authorEmail: Email,
    ): VoidOperationResult<Throwable> =
        reviewsRemoteDataSource.addReview(request, authorId, authorEmail)
            .mapSuccess { newReview ->
                val entity = newReview.toEntity()
                myReviewsDao.insert(entity)
            }

    override suspend fun sync(userId: Id) {
        when (val myReviewsResult = getAndStoreMyReviews(userId)) {
            is OperationResult.Success -> {
                getAndStoreMyReviedMovies(myReviewsResult.result)
            }
            is OperationResult.Error -> {
                Timber.e("sync Local storage error", myReviewsResult.result)
            }
        }
    }

    override suspend fun unSync() =
        myReviewsDao.deleteAll()

    private suspend fun getAndStoreMyReviews(userId: Id): OperationResult<List<MyReview>, Throwable> =
        reviewsRemoteDataSource.getMyReviews(userId)
            .doOnSuccess { myReviews ->
                val entities = myReviews.map { model -> model.toEntity() }
                myReviewsDao.insertAll(entities)
            }

    private suspend fun getMovieDetailsWithReviewsTriple(
        id: MovieId,
    ): Triple<OperationResult<Movie, Throwable>, OperationResult<List<SomeoneReview>, Throwable>, MyReview?> =
        coroutineScope {
            val allReviewsResultCall = async { reviewsRemoteDataSource.getMovieReviews(id) }
            val detailsCall = async { getMovieDetails(id) }
            val myReviewCall = async { myReviewsDao.getByMovieId(id)?.toModel() }

            Triple(
                detailsCall.await(),
                allReviewsResultCall.await(),
                myReviewCall.await()
            )
        }

    private suspend fun searchMovies(query: String, page: Int): OperationResult<List<SearchMovie>, Throwable> =
        moviesRemoteDataSource.searchMovies(query, page)
            .mapSuccess { dtos -> dtos.map { singleDto -> singleDto.toEntity(baseImageUrl) } }
            .doOnSuccess { movieEntities -> moviesDao.insertAll(movieEntities) }
            .mapSuccess { entries -> entries.map { movieEntity -> movieEntity.toModel() } }

    private suspend fun getMyReviewsForMovies(movieIds: List<MovieId>): Map<MovieId, MyReview> =
        myReviewsDao.getByMovieIds(movieIds)
            .map { myReviewEntity -> myReviewEntity.toModel() }
            .associateBy { myReview -> myReview.movieId }

    private suspend fun getAndStoreMyReviedMovies(result: List<MyReview>) {
        val myReviewedMovieIds = result
            .map { review -> review.movieId }
            .toSet()

        getMovieDetailsList(ids = myReviewedMovieIds)
    }
}