package ru.gaket.themoviedb.data.genres

import ru.gaket.themoviedb.data.genres.local.GenreEntity
import ru.gaket.themoviedb.data.genres.local.GenresDao
import ru.gaket.themoviedb.data.genres.remote.GenreDto
import ru.gaket.themoviedb.data.genres.remote.GenresRemoteDataSource
import javax.inject.Inject

//todo: class unused
// todo: [Sergey] use domain model instead of data model
interface GenresRepository {

    /**
     * @return List of [GenreEntity], null as error, or empty list.
     * No Throwable.
     */
    suspend fun getGenres(forceRefresh: Boolean): List<GenreEntity>?

    /**
     * @return List of [GenreEntity], null as error, or empty list.
     * No Throwable.
     */
    suspend fun getGenresByIds(forceRefresh: Boolean, ids: List<Int>): List<GenreEntity>?
}

class GenresRepositoryImpl @Inject constructor(
    private val remoteDataSource: GenresRemoteDataSource,
    private val genresDao: GenresDao,
) : GenresRepository {

    override suspend fun getGenres(forceRefresh: Boolean): List<GenreEntity>? {
        if (canLoadFromLocalCache(forceRefresh)) {
            return genresDao.getAll()
        }

        return syncFromServer()?.run { genresDao.getAll() }
    }

    // todo: [Sergey] use value class for Int
    override suspend fun getGenresByIds(forceRefresh: Boolean, ids: List<Int>): List<GenreEntity>? {
        if (canLoadFromLocalCache(forceRefresh)) {
            return genresDao.getAllByIds(ids)
        }

        return syncFromServer()?.run { genresDao.getAllByIds(ids) }
    }

    private suspend fun canLoadFromLocalCache(forceRefresh: Boolean): Boolean {
        return !forceRefresh && isLocalCacheIsNotEmpty()
    }

    private suspend fun syncFromServer(): List<GenreEntity>? =
        remoteDataSource.getGenres()
            ?.map(GenreDto::toEntity)
            ?.apply { genresDao.insertAll(this) }

    private suspend fun isLocalCacheIsNotEmpty(): Boolean = (genresDao.genresCount() > 0)
}
