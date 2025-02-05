package com.example.hotmovies.presentation.login

import com.example.hotmovies.presentation.UserInteractionConfigurableComponent
import com.example.hotmovies.presentation.shared.layouts.CustomMotionLayout
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.transitionToEndAsync
import com.example.hotmovies.shared.transitionToStartAsync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginAnimator(
    private val userInteractionComponent: UserInteractionConfigurableComponent,
    private val motionLayout: CustomMotionLayout
) {
    enum class TransitionState {
        Start {
            override val isEnd: Boolean
                get() = false
        },
        End {
            override val isEnd: Boolean
                get() = true
        };

        abstract val isEnd: Boolean
    }

    private val _state =
        MutableStateFlow<Async<TransitionState>>(Async.Success(TransitionState.Start))
    val state = _state.asStateFlow()

    suspend fun transitionToStartAsync(immediately: Boolean) {
        _state.value = Async.Progress
        userInteractionComponent.isEnabled = false
        motionLayout.isEnabledLayoutTransition = false

        motionLayout.transitionToStartAsync(immediately)

        motionLayout.isEnabledLayoutTransition = true
        userInteractionComponent.isEnabled = true
        _state.value = Async.Success(TransitionState.Start)
    }

    suspend fun transitionToEndAsync(immediately: Boolean) {
        _state.value = Async.Progress
        userInteractionComponent.isEnabled = false
        motionLayout.isEnabledLayoutTransition = false

        motionLayout.transitionToEndAsync(immediately)

        motionLayout.isEnabledLayoutTransition = true
        userInteractionComponent.isEnabled = true
        _state.value = Async.Success(TransitionState.End)
    }
}