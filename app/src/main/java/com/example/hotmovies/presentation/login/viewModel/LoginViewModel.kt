package com.example.hotmovies.presentation.login.viewModel.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.login.LoginUserUseCase
import com.example.hotmovies.domain.LoginPassword
import com.example.hotmovies.domain.LoginUserName
import com.example.hotmovies.presentation.shared.UIControlState
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.progressEvent
import com.example.hotmovies.shared.stateEvent
import com.example.hotmovies.shared.stateEventFailure
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(useCase: LoginUserUseCase) : ViewModel() {

    data class UIState(
        val userNameText: UIControlState,
        val passwordText: UIControlState,
        val isAnimation: Boolean,
        val loginAction: Event<ResultState<Boolean>>
    ) {
        companion object {
            fun defaultState() = UIState(
                UIControlState.enabled(),
                UIControlState.enabled(),
                false,
                false.stateEvent()
            )
        }

        val isScreenEnabled get() = !loginAction.content.isProgress && !isAnimation
        val loginButton: UIControlState
            get() =
                UIControlState(
                    false,
                    !userNameText.isEmpty && !passwordText.isEmpty && isScreenEnabled,
                    null
                )
    }

    sealed interface Intents {
        data object Login : Intents
        data class Animation(val isInProgress: Boolean) : Intents
    }

    val userNameText = MutableStateFlow("test123")
    val passwordText = MutableStateFlow("1234567890")

    private val animationAction = MutableStateFlow(false)
    private val loginAction = LoginAction(useCase)

    private var job: Job? = null
    private var state_ = MutableSharedFlow<UIState>()
    val state = state_
        .onStart {
            startActions()
        }
        .onCompletion {
            stopActions()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIState.defaultState())

    private fun startActions() {
        stopActions()
        println("***** Connected")
        job = viewModelScope.launch {
            loginAction.state.onEach {
                state_.emit(reduceLogin(state.value, it))
            }.launchIn(this)
            userNameText.onEach {
                state_.emit(reduceUsernameText(state.value, it))
            }.launchIn(this)
            passwordText.onEach {
                state_.emit(reducePasswordText(state.value, it))
            }.launchIn(this)
            animationAction.onEach {
                state_.emit(reduceAnimation(state.value, it))
            }.launchIn(this)
        }
    }

    private fun stopActions() {
        job?.cancel()
        job = null
        println("***** Disconnected")
    }

    fun sendIntent(intent: Intents) {
        when (intent) {
            is Intents.Login -> loginAction.login(userNameText.value, passwordText.value)
            is Intents.Animation -> animationAction.value = intent.isInProgress
        }
    }

    private fun reduceUsernameText(state: UIState, text: String): UIState {
        checkMainThread()
        val controlState = state.userNameText
        return state.copy(
            userNameText = controlState.copy(
                isEmpty = text.isEmpty(),
                isEnabled = state.isScreenEnabled,
                exception = null
            )
        )
    }

    private fun reducePasswordText(state: UIState, text: String): UIState {
        checkMainThread()
        val controlState = state.passwordText
        return state.copy(
            passwordText = controlState.copy(
                isEmpty = text.isEmpty(),
                isEnabled = state.isScreenEnabled,
                exception = null
            )
        )
    }

    private fun reduceAnimation(state: UIState, isInProgress: Boolean): UIState {
        checkMainThread()
        return state.copy(isAnimation = isInProgress)
    }

    private fun reduceLogin(state: UIState, result: ResultState<Unit>): UIState {
        checkMainThread()
        return when (result) {
            is ResultState.Progress -> state.copy(loginAction = progressEvent)
            is ResultState.Success -> state.copy(loginAction = true.stateEvent())

            is ResultState.Failure -> {
                val exception = result.exception
                var userNameText = state.userNameText
                var passwordText = state.passwordText
                when (exception) {
                    is LoginUserName.Exceptions.InvalidInputException ->
                        userNameText = userNameText.copy(exception = exception)

                    is LoginPassword.Exceptions.InvalidInputException ->
                        passwordText = passwordText.copy(exception = exception)

                    else ->
                        passwordText = passwordText.copy(exception = exception)
                }
                state.copy(
                    userNameText = userNameText,
                    passwordText = passwordText,
                    loginAction = exception.stateEventFailure()
                )
            }
        }
    }
}