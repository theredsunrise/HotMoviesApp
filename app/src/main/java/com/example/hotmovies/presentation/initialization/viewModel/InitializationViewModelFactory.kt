package com.example.hotmovies.presentation.initialization.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.login.SessionValidityUseCase
import javax.inject.Inject

class InitializationViewModelFactory @Inject constructor(
    private val useCase: SessionValidityUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InitializationViewModel(useCase) as T
    }
}
