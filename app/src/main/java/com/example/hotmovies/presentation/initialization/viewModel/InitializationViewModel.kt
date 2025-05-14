package com.example.hotmovies.presentation.initialization.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.login.SessionValidityUseCase
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModel.Intents.CheckSessionValidity
import com.example.hotmovies.presentation.initialization.viewModel.actions.SessionValidityAction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

class InitializationViewModel(useCase: SessionValidityUseCase) :
    ViewModel() {
    private val sessionValidityAction = SessionValidityAction(useCase)

    val state = sessionValidityAction.state.shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        1
    ).onStart {
        sendIntent(CheckSessionValidity)
    }

    sealed interface Intents {
        data object CheckSessionValidity : Intents
    }

    fun sendIntent(intent: Intents) {
        when (intent) {
            CheckSessionValidity -> sessionValidityAction.run(Unit)
        }
    }
}