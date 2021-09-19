package ru.gaket.themoviedb.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.gaket.themoviedb.BuildConfig
import ru.gaket.themoviedb.data.core.network.MoviesDbClient
import ru.gaket.themoviedb.data.core.network.MoviesDbClientImpl
import ru.gaket.themoviedb.data.genres.remote.GenresApi
import ru.gaket.themoviedb.data.movies.remote.MoviesApi
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
	
	@Binds
	abstract fun bindMovieDbClient(
		impl: MoviesDbClientImpl
	): MoviesDbClient
}

@Module
@InstallIn(SingletonComponent::class)
class ApiWrapperModule {

	@Provides
	fun provideMoviesApi(client: MoviesDbClient): MoviesApi = client.moviesApi()
	
	@Provides
	fun provideGenresApi(client: MoviesDbClient): GenresApi = client.genresApi()
}

@Module
@InstallIn(SingletonComponent::class)
class BaseUrlWrapperModule {

	@Provides
	@BaseUrlQualifier
	fun provideBaseUrl() = BuildConfig.BASE_URL
	
	@Provides
	@BaseImageUrlQualifier
	fun provideBaseImageUrl() = "https://image.tmdb.org/t/p/w300"
	
	@Provides
	@BrowseMovieBaseUrlQualifier
	fun provideBrowseMovieBaseUrl() = "https://www.themoviedb.org/movie/"
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrlQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseImageUrlQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BrowseMovieBaseUrlQualifier