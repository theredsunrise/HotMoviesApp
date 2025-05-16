package com.example.hotmovies.presentation.movies.detail.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import kotlinx.coroutines.Dispatchers

@Suppress("UNCHECKED_CAST")
class MovieDetailsViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return MovieDetailsViewModel(
            savedStateHandle,
            MovieDetailsUseCase(
                diContainer.movieDataRepository,
                Dispatchers.IO
            )
        ) as T
    }
}