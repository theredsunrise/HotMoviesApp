package com.example.hotmovies.presentation.movies.list.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.login.LogoutUserCase
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateViewModelAction
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LogoutAction(
    coroutineScope: CoroutineScope,
    diContainer: DIContainer
) :
    BaseResultStateViewModelAction<Unit, Unit>(coroutineScope) {

    private val settingsRepository = diContainer.settingsRepository
    private val loginRepository = diContainer.loginRepository

    override fun action(value: Unit): Flow<ResultState<Unit>> {
        return LogoutUserCase(loginRepository, settingsRepository)()
    }
}

