package com.example.hotmovies.presentation.movies.detail.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MovieDetailsViewModelFactory @Inject constructor(
    private val useCase: MovieDetailsUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return MovieDetailsViewModel(
            savedStateHandle,
            useCase
        ) as T
    }
}