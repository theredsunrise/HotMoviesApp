package com.example.hotmovies.presentation.initialization.viewModel.actions

import com.example.hotmovies.appplication.login.SessionValidityUseCase
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateEventViewModelAction
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionValidityAction(
    coroutineScope: CoroutineScope,
    private val useCase: SessionValidityUseCase,
    onStart: (suspend () -> Unit)
) :
    BaseResultStateEventViewModelAction<Unit, Boolean>(coroutineScope, 1, onStart = onStart) {

    override fun action(value: Unit): Flow<Event<ResultState<Boolean>>> {
        return useCase()
            .map { Event(it) }
    }
}

