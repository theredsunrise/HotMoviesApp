package com.example.hotmovies.presentation.initialization.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.login.SessionValidityUseCase
import com.example.hotmovies.presentation.shared.viewModels.BaseAsyncEventViewModelAction
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionValidityAction(
    coroutineScope: CoroutineScope,
    diContainer: DIContainer,
    onStart: (suspend () -> Unit)
) :
    BaseAsyncEventViewModelAction<Unit, Boolean>(coroutineScope, 1, onStart = onStart) {

    private val loginRepository = diContainer.loginRepository
    private val settingsRepository = diContainer.settingsRepository

    override fun action(value: Unit): Flow<Event<Async<Boolean>>> {
        return SessionValidityUseCase(loginRepository, settingsRepository)()
            .map { Event(it) }
    }
}

