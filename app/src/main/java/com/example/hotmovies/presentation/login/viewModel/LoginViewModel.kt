package com.example.hotmovies.presentation.login.viewModel.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.domain.LoginPassword
import com.example.hotmovies.domain.LoginUserName
import com.example.hotmovies.presentation.shared.UIControlState
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.progressEvent
import com.example.hotmovies.shared.stateEvent
import com.example.hotmovies.shared.stateEventFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LoginViewModel(diContainer: DIContainer) : ViewModel() {

    data class UIState(
        val userNameText: UIControlState,
        val passwordText: UIControlState,
        val loginButton: UIControlState,
        val isScreenEnabled: Boolean,
        val loginAction: Event<ResultState<Boolean>>
    ) {
        companion object {
            fun defaultState() = UIState(
                UIControlState.enabled(),
                UIControlState.enabled(),
                UIControlState.disabled(),
                true,
                false.stateEvent()
            )
        }
    }

    sealed interface Actions {
        data object Login : Actions
        data class Animation(val isInProgress: Boolean) : Actions
    }

    val userNameText = MutableStateFlow("test123")
    val passwordText = MutableStateFlow("1234567890")

    private var _state = MutableStateFlow(UIState.defaultState())
    val state = _state.asStateFlow()

    private val loginAction = LoginAction(viewModelScope, diContainer)

    init {
        combine(userNameText, passwordText) { userName, password ->
            userName.isNotEmpty() && password.isNotEmpty()
        }.onEach { value ->
            processLoginButton(value)
        }.launchIn(viewModelScope)

        loginAction.state.onEach { value ->
            processLogin(value)
        }.launchIn(viewModelScope)
    }

    fun doAction(action: Actions) {
        when (action) {
            is Actions.Login -> loginAction.login(userNameText.value, passwordText.value)
            is Actions.Animation -> processAnimation(action.isInProgress)
        }
    }

    private fun processLoginButton(isEnabled: Boolean) {
        checkMainThread()
        _state.update {
            it.copy(
                userNameText = UIControlState.enabled(),
                passwordText = UIControlState.enabled(),
                loginButton = it.loginButton.copy(isEnabled = isEnabled),
                isScreenEnabled = true,
                loginAction = false.stateEvent(),
            )
        }
    }

    private fun processAnimation(isInProgress: Boolean) {
        checkMainThread()
        _state.update {
            it.copy(
                isScreenEnabled = !isInProgress
            )
        }
    }

    private fun processLogin(result: ResultState<Unit>) {
        checkMainThread()
        when (result) {
            is ResultState.Progress -> _state.update {
                it.copy(
                    isScreenEnabled = false,
                    loginAction = progressEvent
                )
            }

            is ResultState.Success -> _state.update {
                it.copy(
                    isScreenEnabled = true,
                    loginAction = true.stateEvent()
                )
            }

            is ResultState.Failure -> {
                val exception = result.exception
                var userNameText = _state.value.userNameText
                var passwordText = _state.value.passwordText
                when (exception) {
                    is LoginUserName.Exceptions.InvalidInputException ->
                        userNameText = userNameText.copy(exception = exception)

                    is LoginPassword.Exceptions.InvalidInputException ->
                        passwordText = passwordText.copy(exception = exception)

                    else ->
                        passwordText = passwordText.copy(exception = exception)
                }
                _state.update {
                    it.copy(
                        userNameText = userNameText,
                        passwordText = passwordText,
                        isScreenEnabled = true,
                        loginAction = exception.stateEventFailure()
                    )
                }
            }
        }
    }
}