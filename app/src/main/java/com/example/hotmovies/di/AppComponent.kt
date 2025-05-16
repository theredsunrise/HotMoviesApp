package com.example.hotmovies.di

import android.content.Context
import com.example.hotmovies.appplication.login.interfaces.MovieImageRepositoryInterface
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModelFactory
import com.example.hotmovies.presentation.login.viewModel.LoginViewModelFactory
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModelFactory
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        DispatchersModule::class,
        LoginModule::class,
        MoviesModule::class,
        NetworkModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun initializationViewModelFactory(): InitializationViewModelFactory
    fun loginViewModelFactory(): LoginViewModelFactory
    fun moviesViewModelFactory(): MoviesViewModelFactory
    fun movieDetailsViewModelFactory(): MovieDetailsViewModelFactory
    fun movieImageRepository(): MovieImageRepositoryInterface
}