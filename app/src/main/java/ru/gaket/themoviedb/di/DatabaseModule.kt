package ru.gaket.themoviedb.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.gaket.themoviedb.data.core.db.MoviesDb
import ru.gaket.themoviedb.data.genres.local.GenresDao
import ru.gaket.themoviedb.data.movies.local.MoviesDao
import ru.gaket.themoviedb.data.review.local.MyReviewsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MoviesDb =
        MoviesDb.create(appContext)

    @Provides
    fun provideGenresDao(moviesDb: MoviesDb): GenresDao =
        moviesDb.genresDao()

    @Provides
    fun provideMoviesDao(moviesDb: MoviesDb): MoviesDao =
        moviesDb.moviesDao()

    @Provides
    fun provideMyReviewsDao(moviesDb: MoviesDb): MyReviewsDao =
        moviesDb.myReviewsDao()
}