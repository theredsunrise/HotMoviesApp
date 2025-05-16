package com.example.hotmovies.presentation.login.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.login.LoginUserUseCase
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel
import javax.inject.Inject

class LoginViewModelFactory @Inject constructor(
    private val useCase: LoginUserUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(useCase) as T
    }
}
