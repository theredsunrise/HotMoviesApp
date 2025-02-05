package com.example.hotmovies.presentation.shared.transitions

import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.example.hotmovies.presentation.UserInteractionConfigurableComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile

class UserInteractionTransitionWrapper(
    private val userInteractionComponent: UserInteractionConfigurableComponent,
    transition: Transition
) :
    TransitionSet(),
    Transition.TransitionListener {

    private var isTransitionFinished = MutableStateFlow(true)
    var doOnEndOrCancel: (() -> Unit)? = null
        set(value) {
            field = value
            if (isTransitionFinished.value) {
                value?.invoke()
            }
        }

    init {
        addTransition(transition)
        addListener(this)
        duration = transition.duration
        startDelay = transition.startDelay
    }

    suspend fun doOnEndOrCancelAsync() {
        isTransitionFinished.onStart { delay(50) }.takeWhile {
            !it
        }.collect()
    }

    override fun onTransitionPause(transition: Transition) {
        isTransitionFinished.value = false
    }

    override fun onTransitionResume(transition: Transition) {
        isTransitionFinished.value = false
    }

    override fun onTransitionStart(transition: Transition) {
        userInteractionComponent.isEnabled = false
        isTransitionFinished.value = false
    }

    override fun onTransitionEnd(transition: Transition) {
        transition.removeListener(this)
        userInteractionComponent.isEnabled = true
        doOnEndOrCancel?.invoke()
        isTransitionFinished.value = true
    }

    override fun onTransitionCancel(transition: Transition) {
        transition.removeListener(this)
        userInteractionComponent.isEnabled = true
        doOnEndOrCancel?.invoke()
        isTransitionFinished.value = true
    }
}