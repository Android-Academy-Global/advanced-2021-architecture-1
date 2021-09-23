package ru.gaket.themoviedb.data.genres.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * todo: handle column names as in [ru.gaket.themoviedb.data.review.local.MyReviewsDao]
 */
@Dao
interface GenresDao {

    @Query("SELECT COUNT(id) FROM genres")
    suspend fun genresCount(): Int

    @Query("SELECT * FROM genres")
    suspend fun getAll(): List<GenreEntity>

    @Query("SELECT * FROM genres WHERE id IN (:ids)")
    suspend fun getAllByIds(ids: List<Int>): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(genres: List<GenreEntity>)
}