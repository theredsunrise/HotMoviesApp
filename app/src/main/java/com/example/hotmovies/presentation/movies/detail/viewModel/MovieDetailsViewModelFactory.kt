package com.example.hotmovies.presentation.movies.detail.viewModel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import kotlinx.coroutines.Dispatchers

@Suppress("UNCHECKED_CAST")
class MovieDetailsViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    private val diContainer: DIContainer
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MovieDetailsViewModel(
            handle,
            MovieDetailsUseCase(
                diContainer.movieDataRepository,
                Dispatchers.IO
            )
        ) as T
    }
}