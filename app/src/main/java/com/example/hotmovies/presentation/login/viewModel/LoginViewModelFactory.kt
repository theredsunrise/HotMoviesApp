package com.example.hotmovies.presentation.login.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel

class LoginViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(
            diContainer.loginRepository,
            diContainer.settingsRepository
        ) as T
    }
}
