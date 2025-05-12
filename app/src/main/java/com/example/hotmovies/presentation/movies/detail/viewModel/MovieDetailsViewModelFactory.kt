package com.example.hotmovies.presentation.movies.detail.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import kotlinx.coroutines.Dispatchers

class MovieDetailsViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieDetailsViewModel(
            MovieDetailsUseCase(
                diContainer.movieDataRepository,
                Dispatchers.IO
            )
        ) as T
    }
}