package com.example.hotmovies.presentation.initialization.viewModel.actions

import com.example.hotmovies.appplication.login.SessionValidityUseCase
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateEventViewModelAction
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionValidityAction(private val useCase: SessionValidityUseCase) :
    BaseResultStateEventViewModelAction<Unit, Boolean>(1) {

    override fun action(value: Unit): Flow<Event<ResultState<Boolean>>> {
        return useCase()
            .map { Event(it) }
    }
}

