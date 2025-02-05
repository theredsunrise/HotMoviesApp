package com.example.hotmovies.presentation.login.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.login.LoginUserUseCase
import com.example.hotmovies.presentation.shared.viewModels.BaseAsyncViewModelAction
import com.example.hotmovies.shared.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LoginAction(
    diContainer: DIContainer,
    coroutineScope: CoroutineScope
) :
    BaseAsyncViewModelAction<LoginUserUseCase.Credentials, Unit>(coroutineScope = coroutineScope) {

    private val settingsRepository = diContainer.settingsRepository
    private val loginRepository = diContainer.loginRepository

    override fun action(value: LoginUserUseCase.Credentials): Flow<Async<Unit>> {
        return LoginUserUseCase(loginRepository, settingsRepository)(value)
    }

    fun login(userName: String, password: String) {
        run(LoginUserUseCase.Credentials(userName, password))
    }
}

