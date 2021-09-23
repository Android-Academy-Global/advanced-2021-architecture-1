package ru.gaket.themoviedb.data.movies.remote

import com.google.gson.annotations.SerializedName
import ru.gaket.themoviedb.data.genres.remote.GenreDto

/**
 * Class of detailed Movie coming from the api
 */
//todo: use MovieId for id
data class DetailsMovieDto(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("imdb_id")
    val imdbId: String? = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("overview")
    val overview: String = "",

    @SerializedName("adult")
    val isAdult: Boolean = false,

    @SerializedName("vote_average")
    val rating: Float = 0.0f,

    @SerializedName("vote_count")
    val reviewsCounter: Int = 0,

    @SerializedName("popularity")
    val popularity: Float = 0.0f,

    @SerializedName("release_date")
    val releaseDate: String = "",

    @SerializedName("runtime")
    val duration: Int = 0,

    @SerializedName("budget")
    val budget: Int = 0,

    @SerializedName("revenue")
    val revenue: Int = 0,

    @SerializedName("status")
    val status: String = "Released",

    @SerializedName("genres")
    val genres: List<GenreDto> = emptyList(),

    @SerializedName("homepage")
    val homepage: String = "",

    @SerializedName("backdrop_path")
    val posterPath: String? = "",
)
