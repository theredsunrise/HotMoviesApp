package com.example.hotmovies.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.hotmovies.BuildConfig
import com.example.hotmovies.appplication.login.interfaces.MovieImageRepositoryInterface
import com.example.hotmovies.appplication.movies.interfaces.MovieDataRepositoryInterface
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.infrastructure.dataRepository.mock.MockMovieDataRepository
import com.example.hotmovies.infrastructure.dataRepository.mock.MockMovieImageRepository
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieDataApiInterface
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieDataRepository
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiInterface
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageRepository
import com.example.hotmovies.presentation.shared.pagers.MoviePagingSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
class MoviesModule {

    private val mocked = false

    @Provides
    fun providePager(
        movieDataRepository: MovieDataRepositoryInterface,
        @IODispatcher dispatcher: CoroutineDispatcher
    ): Pager<Int, Movie> {
        val pagingConfig = PagingConfig(20, enablePlaceholders = false)
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                MoviePagingSource(
                    movieDataRepository,
                    dispatcher
                )
            }
        )
    }

    @SuppressLint("DiscouragedApi")
    @Singleton
    @Provides
    fun provideMovieImageRepository(
        context: Context,
        imageApiService: TmdbMovieImageApiInterface
    ): MovieImageRepositoryInterface {
        return if (BuildConfig.DEBUG && mocked) {
            @DrawableRes val avatarId =
                context.resources.getIdentifier("patrik", "drawable", context.packageName)
            MockMovieImageRepository(context, avatarId, false)
        } else
            TmdbMovieImageRepository(imageApiService)
    }

    @SuppressLint("DiscouragedApi")
    @Singleton
    @Provides
    fun provideMovieDataRepository(
        context: Context,
        dataApiService: TmdbMovieDataApiInterface,
        imageRepository: MovieImageRepositoryInterface
    ): MovieDataRepositoryInterface {
        return if (BuildConfig.DEBUG && mocked) {
            @DrawableRes val avatarId =
                context.resources.getIdentifier("patrik", "drawable", context.packageName)
            MockMovieDataRepository(context, avatarId)
        } else
            return TmdbMovieDataRepository(dataApiService, imageRepository)
    }
}