package com.example.hotmovies.presentation.movies.list.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.DIContainer

class MoviesViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(
            diContainer.appContext.resources,
            diContainer.pager,
            diContainer.loginRepository,
            diContainer.settingsRepository,
            diContainer.movieDataRepository
        ) as T
    }
}

