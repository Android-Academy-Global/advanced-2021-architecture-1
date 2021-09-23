package ru.gaket.themoviedb.data.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.gaket.themoviedb.data.genres.local.GenreEntity
import ru.gaket.themoviedb.data.genres.local.GenresDao
import ru.gaket.themoviedb.data.movies.local.MovieEntity
import ru.gaket.themoviedb.data.movies.local.MoviesDao
import ru.gaket.themoviedb.data.review.local.MyReviewEntity
import ru.gaket.themoviedb.data.review.local.MyReviewsDao
import ru.gaket.themoviedb.data.review.local.RatingDbConverter

@Database(
    entities = [
        GenreEntity::class,
        MovieEntity::class,
        MyReviewEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    RatingDbConverter::class
)
abstract class MoviesDb : RoomDatabase() {

    abstract fun genresDao(): GenresDao
    abstract fun moviesDao(): MoviesDao
    abstract fun myReviewsDao(): MyReviewsDao
}