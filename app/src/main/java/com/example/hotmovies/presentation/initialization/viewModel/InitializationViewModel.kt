package com.example.hotmovies.presentation.initialization.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModel.Actions.CheckSessionValidity
import com.example.hotmovies.presentation.initialization.viewModel.actions.SessionValidityAction

class InitializationViewModel(diContainer: DIContainer) :
    ViewModel() {
    private val sessionValidityAction = SessionValidityAction(viewModelScope, diContainer) {
        doAction(CheckSessionValidity)
    }

    val state = sessionValidityAction.state

    sealed interface Actions {
        data object CheckSessionValidity : Actions
    }

    fun doAction(action: Actions) {
        when (action) {
            CheckSessionValidity -> sessionValidityAction.run(Unit)
        }
    }
}