package com.example.hotmovies.presentation.initialization.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModel.Actions.CheckSessionValidity
import com.example.hotmovies.presentation.initialization.viewModel.actions.SessionValidityAction
import com.example.hotmovies.shared.asyncEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

class InitializationViewModel(diContainer: DIContainer) :
    ViewModel() {
    private val sessionValidityAction = SessionValidityAction(diContainer, viewModelScope)

    val state = sessionValidityAction.state.onStart {
        doAction(CheckSessionValidity)
    }.shareIn(viewModelScope, SharingStarted.Lazily)

    sealed interface Actions {
        data object CheckSessionValidity : Actions
    }

    fun doAction(action: Actions) {
        when (action) {
            CheckSessionValidity -> sessionValidityAction.run(Unit)
        }
    }
}