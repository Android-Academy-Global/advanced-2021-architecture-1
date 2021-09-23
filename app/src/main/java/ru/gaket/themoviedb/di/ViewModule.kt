package ru.gaket.themoviedb.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.gaket.themoviedb.data.genres.GenresRepository
import ru.gaket.themoviedb.data.genres.GenresRepositoryImpl
import ru.gaket.themoviedb.data.genres.remote.GenresRemoteDataSource
import ru.gaket.themoviedb.data.genres.remote.GenresRemoteDataSourceImpl
import ru.gaket.themoviedb.data.movies.MoviesRepository
import ru.gaket.themoviedb.data.movies.MoviesRepositoryImpl
import ru.gaket.themoviedb.data.movies.remote.MoviesRemoteDataSource
import ru.gaket.themoviedb.data.movies.remote.MoviesRemoteDataSourceImpl
import ru.gaket.themoviedb.data.review.remote.ReviewsRemoteDataSource
import ru.gaket.themoviedb.data.review.remote.ReviewsRemoteDataSourceImpl
import ru.gaket.themoviedb.domain.SyncLocalStorageUseCaseImpl
import ru.gaket.themoviedb.domain.auth.AuthInteractor
import ru.gaket.themoviedb.domain.auth.AuthInteractorImpl
import ru.gaket.themoviedb.domain.auth.SyncLocalStorageUseCase
import ru.gaket.themoviedb.domain.movies.MoviesInteractor
import ru.gaket.themoviedb.domain.movies.MoviesInteractorImpl

//todo: ViewModelComponent correct for binded classes?
@Module
@InstallIn(ViewModelComponent::class)
interface ViewModule {

    @Binds
    fun bindMoviesRemoteDataSource(
        impl: MoviesRemoteDataSourceImpl,
    ): MoviesRemoteDataSource

    @Binds
    fun bindMoviesRepository(
        impl: MoviesRepositoryImpl,
    ): MoviesRepository

    @Binds
    fun bindMoviesInteractor(
        impl: MoviesInteractorImpl,
    ): MoviesInteractor

    @Binds
    fun bindGenresRemoteDataSource(
        impl: GenresRemoteDataSourceImpl,
    ): GenresRemoteDataSource

    @Binds
    fun bindGenresRepository(
        impl: GenresRepositoryImpl,
    ): GenresRepository

    @Binds
    fun bindReviewsRemoteDataSource(
        impl: ReviewsRemoteDataSourceImpl,
    ): ReviewsRemoteDataSource

    @Binds
    fun bindAuthInteractor(
        impl: AuthInteractorImpl,
    ): AuthInteractor

    @Binds
    fun bindSyncLocalStorageUseCase(
        impl: SyncLocalStorageUseCaseImpl,
    ): SyncLocalStorageUseCase
}