package ru.gaket.themoviedb.data.movies.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DB class of detailed Movie stored in room
 */
@Entity(tableName = "movies")
data class MovieEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "imdb_id")
    val imdbId: String = "",

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "overview")
    val overview: String = "",

    @ColumnInfo(name = "allowed_age")
    val allowedAge: String = "",

    @ColumnInfo(name = "rating")
    val rating: Int = 0,

    @ColumnInfo(name = "reviews_counter")
    val reviewsCounter: Int = 0,

    @ColumnInfo(name = "popularity")
    val popularity: Float = 0.00f,

    @ColumnInfo(name = "release_date")
    val releaseDate: String = "",

    @ColumnInfo(name = "duration")
    val duration: Int = 0,

    @ColumnInfo(name = "budget")
    val budget: Int = 0,

    @ColumnInfo(name = "revenue")
    val revenue: Int = 0,

    @ColumnInfo(name = "status")
    val status: String = "Released",

    @ColumnInfo(name = "genres")
    val genres: String = "",

    @ColumnInfo(name = "homepage")
    val homepage: String = "",

    @ColumnInfo(name = "thumbnail")
    val thumbnail: String = "",

    @ColumnInfo(name = "reviewed")
    val hasReview: Boolean = false,

    @ColumnInfo(name = "review_id")
    val reviewId: Int = 0,

    @ColumnInfo(name = "updated_from_server")
    val isUpdatedFromServer: Boolean = false,
)