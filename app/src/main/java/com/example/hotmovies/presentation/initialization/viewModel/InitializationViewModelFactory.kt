package com.example.hotmovies.presentation.initialization.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.login.SessionValidityUseCase
import kotlinx.coroutines.Dispatchers

class InitializationViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InitializationViewModel(
            SessionValidityUseCase(
                diContainer.loginRepository,
                diContainer.settingsRepository,
                Dispatchers.IO
            )
        ) as T
    }
}
