package ru.gaket.themoviedb.data.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gaket.themoviedb.data.genres.remote.GenresApi
import ru.gaket.themoviedb.data.movies.remote.MoviesApi
import ru.gaket.themoviedb.di.BaseUrlQualifier
import ru.gaket.themoviedb.di.TheMovieDbApiKey
import javax.inject.Inject

//todo: maybe this class useless
interface MoviesHttpClient {

    // todo: [Sergey] migrate to property
    fun moviesApi(): MoviesApi
    fun genresApi(): GenresApi
}

class MoviesHttpClientImpl @Inject constructor(
    @TheMovieDbApiKey apiKey: String,
    @BaseUrlQualifier baseUrl: String,
) : MoviesHttpClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor(QueryInterceptor(hashMapOf("api_key" to apiKey)))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val moviesApi = retrofit.create(MoviesApi::class.java)
    private val genresApi = retrofit.create(GenresApi::class.java)

    override fun moviesApi(): MoviesApi = moviesApi
    override fun genresApi(): GenresApi = genresApi
}
