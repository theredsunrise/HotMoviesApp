package com.example.hotmovies.presentation.login.viewModel.actions

import com.example.hotmovies.appplication.login.LoginUserUseCase
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateViewModelAction
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.flow.Flow

class LoginAction(private val useCase: LoginUserUseCase) :
    BaseResultStateViewModelAction<LoginUserUseCase.Credentials, Unit>() {

    override fun action(value: LoginUserUseCase.Credentials): Flow<ResultState<Unit>> {
        return useCase(value)
    }

    fun login(userName: String, password: String) {
        run(LoginUserUseCase.Credentials(userName, password))
    }
}

