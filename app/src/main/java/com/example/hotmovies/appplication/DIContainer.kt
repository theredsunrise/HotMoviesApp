package com.example.hotmovies.appplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.PagingConfig
import com.example.hotmovies.R
import com.example.hotmovies.appplication.login.interfaces.LoginRepositoryInterface
import com.example.hotmovies.appplication.login.interfaces.MovieDataPagingRepositoryInterface
import com.example.hotmovies.appplication.login.interfaces.MovieImageRepositoryInterface
import com.example.hotmovies.appplication.login.interfaces.SettingsRepositoryInterface
import com.example.hotmovies.infrastructure.LoginRepository
import com.example.hotmovies.infrastructure.SettingsRepository
import com.example.hotmovies.infrastructure.dataRepository.MovieDataPagingRepository
import com.example.hotmovies.infrastructure.dataRepository.mock.MockMovieDataRepository
import com.example.hotmovies.infrastructure.dataRepository.mock.MockMovieImageRepository
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieDataApiInterface
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieDataApiServiceFactory
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieDataRepository
import com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageRepository
import kotlinx.coroutines.Dispatchers

class DIContainer(val appContext: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val tmdbMovieDataApiService: TmdbMovieDataApiInterface by lazy {
        TmdbMovieDataApiServiceFactory.create()
    }

    private val tmdbMovieImageApiService: com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiInterface by lazy {
        com.example.hotmovies.infrastructure.dataRepository.tmdb.TmdbMovieImageApiFactory.create()
    }

    val loginRepository: LoginRepositoryInterface by lazy {
        LoginRepository(appContext)
    }

    val settingsRepository: SettingsRepositoryInterface by lazy {
        SettingsRepository(appContext.dataStore)
    }

    val tmdbMovieImageRepository: MovieImageRepositoryInterface by lazy {
        TmdbMovieImageRepository(tmdbMovieImageApiService)
    }

    val _tmdbMovieImageRepository: MovieImageRepositoryInterface by lazy {
        MockMovieImageRepository(appContext, R.drawable.vector_background)
    }

    val tmdbMovieDataRepository: MovieDataPagingRepositoryInterface by lazy {
        val tmdbMovieDataRepository =
            TmdbMovieDataRepository(tmdbMovieDataApiService, tmdbMovieImageRepository)
        MovieDataPagingRepository(
            PagingConfig(20, enablePlaceholders = false),
            tmdbMovieDataRepository,
            Dispatchers.IO
        )
    }

    val _tmdbMovieDataRepository: MovieDataPagingRepositoryInterface by lazy {
        val mockMovieDataRepository =
            MockMovieDataRepository(appContext, R.drawable.vector_background)
        MovieDataPagingRepository(
            PagingConfig(20, enablePlaceholders = false),
            mockMovieDataRepository,
            Dispatchers.IO
        )
    }
}