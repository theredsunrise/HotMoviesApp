package com.example.hotmovies.presentation.login.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.login.LoginUserUseCase
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel
import kotlinx.coroutines.Dispatchers

class LoginViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(
            LoginUserUseCase(
                diContainer.loginRepository,
                diContainer.settingsRepository,
                Dispatchers.IO
            )
        ) as T
    }
}
