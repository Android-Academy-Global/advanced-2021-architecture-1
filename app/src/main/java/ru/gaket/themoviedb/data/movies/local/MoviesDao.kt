package ru.gaket.themoviedb.data.movies.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.gaket.themoviedb.domain.movies.models.MovieId

/**
 * todo: handle column names as in [ru.gaket.themoviedb.data.review.local.MyReviewsDao]
 */
@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies WHERE id=:id LIMIT 1")
    suspend fun getById(id: MovieId): MovieEntity?

    @Transaction
    @Insert(entity = MovieEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(searchedMovies: List<SearchMovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Transaction
    @Query("DELETE FROM movies")
    suspend fun deleteAll()
}