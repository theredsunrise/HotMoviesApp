package com.example.hotmovies.presentation.login.login.viewModel

import TestException
import com.example.hotmovies.appplication.login.LoginUserUseCase
import com.example.hotmovies.appplication.login.interfaces.LoginRepositoryInterface
import com.example.hotmovies.appplication.login.interfaces.SettingsRepositoryInterface
import com.example.hotmovies.domain.LoginPassword
import com.example.hotmovies.domain.LoginUserName
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel
import com.example.hotmovies.shared.ResultState
import common.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginViewModelTest {
    private val token = "129812398173"
    private val testException = TestException()

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    lateinit var loginRepository: LoginRepositoryInterface

    @MockK(relaxed = true)
    lateinit var settingsRepository: SettingsRepositoryInterface

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = spyk(
            LoginViewModel(
                LoginUserUseCase(
                    loginRepository,
                    settingsRepository,
                    mainDispatcherRule.testDispatcher
                )
            ), recordPrivateCalls = true
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login button disabled, when username is empty, succeeded()`() = runTest {
        val results = mutableListOf<LoginViewModel.UIState>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            loginViewModel.state.toList(results)
        }

        loginViewModel.userNameText.value = ""
        advanceUntilIdle()

        verify {
            loginViewModel.state
            loginViewModel.userNameText
        }
        assertFalse("Login button should not be enabled!", results[1].loginButton.isEnabled)
        confirmVerified(loginRepository, settingsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login button, is disabled when password is empty, succeeded()`() = runTest {
        val results = mutableListOf<LoginViewModel.UIState>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            loginViewModel.state.toList(results)
        }

        loginViewModel.passwordText.value = ""
        advanceUntilIdle()

        assertFalse(
            "Login button should not be enabled!",
            loginViewModel.state.value.loginButton.isEnabled
        )
        verifyAll {
            loginViewModel.state
            loginViewModel.passwordText
        }

        confirmVerified(loginRepository, settingsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `username text, text is too long, throws error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { loginRepository.login(any(), any()) } returns flowOf(token)

            val results = mutableListOf<LoginViewModel.UIState>()
            loginViewModel.userNameText.value = "121212981729817391741"
            loginViewModel.state.onEach { results.add(it) }.launchIn(backgroundScope)
            loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            advanceUntilIdle()

            verifyAll {
                loginViewModel.state
                loginViewModel.userNameText
                loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            }

            assertTrue(
                "Should be Progress",
                results[1].loginAction.getContentIfNotHandled() == ResultState.Progress
            )
            assertTrue(
                "Error should be InvalidInputException!",
                results[2].loginAction.getContentIfNotHandled()?.failure is LoginUserName.Exceptions.InvalidInputException
            )
            assertTrue(
                "Error should be InvalidInputException!",
                results[2].userNameText.exception is LoginUserName.Exceptions.InvalidInputException
            )
            confirmVerified(loginRepository, settingsRepository)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `password text, text is too long, throws error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { loginRepository.login(any(), any()) } returns flowOf(token)

            val results = mutableListOf<LoginViewModel.UIState>()
            loginViewModel.passwordText.value = "121212981729817391741"
            loginViewModel.state.onEach { results.add(it) }.launchIn(backgroundScope)
            loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            advanceUntilIdle()

            verifyAll {
                loginViewModel.state
                loginViewModel.passwordText
                loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            }

            assertTrue(
                "Should be Progress",
                results[1].loginAction.getContentIfNotHandled() == ResultState.Progress
            )

            assertTrue(
                "Error should be InvalidInputException!",
                results[2].loginAction.getContentIfNotHandled()?.failure is LoginPassword.Exceptions.InvalidInputException
            )

            assertTrue(
                "Error should be InvalidInputException!",
                results[2].passwordText.exception is LoginPassword.Exceptions.InvalidInputException
            )
            confirmVerified(loginRepository, settingsRepository)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login, logging in with failure, throws error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { loginRepository.login(any(), any()) } throws TestException()

            val results = mutableListOf<LoginViewModel.UIState>()
            loginViewModel.state.onEach { results.add(it) }.launchIn(backgroundScope)
            loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            advanceUntilIdle()

            verify {
                loginViewModel.state
                loginViewModel.sendIntent(LoginViewModel.Intents.Login)
                loginRepository.login(any(), any())
            }

            assertTrue(
                "Should be Progress",
                results[1].loginAction.getContentIfNotHandled() == ResultState.Progress
            )
            assertTrue(
                "Should be Failure",
                results[2].loginAction.getContentIfNotHandled()?.failure == testException
            )
            confirmVerified(settingsRepository)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login, login token generated and token storing successful, succeeded`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { loginRepository.login(any(), any()) } returns flowOf(token)
            coEvery { settingsRepository.store(any(), any()) } returns flowOf(Unit)

            val results = mutableListOf<LoginViewModel.UIState>()
            loginViewModel.state.onEach { results.add(it) }.launchIn(backgroundScope)
            loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            advanceUntilIdle()

            verify {
                loginViewModel.state
                loginViewModel.sendIntent(LoginViewModel.Intents.Login)
                loginRepository.login(any(), any())
                settingsRepository.store(any(), any())
            }

            assertTrue(
                "Should be Progress",
                results[1].loginAction.getContentIfNotHandled() == ResultState.Progress
            )
            assertTrue(
                "Should be Success",
                results[2].loginAction.getContentIfNotHandled()?.isSuccessTrue == true
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `login, login token generated, login token storing failed, throws error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { loginRepository.login(any(), any()) } returns flowOf(token)
            coEvery { settingsRepository.store(any(), any()) } throws testException

            val results = mutableListOf<LoginViewModel.UIState>()
            loginViewModel.state.onEach { results.add(it) }.launchIn(backgroundScope)
            loginViewModel.sendIntent(LoginViewModel.Intents.Login)
            advanceUntilIdle()

            verify {
                loginViewModel.state
                loginViewModel.sendIntent(LoginViewModel.Intents.Login)
                loginRepository.login(any(), any())
                settingsRepository.store(any(), any())
            }

            assertTrue(
                "Should be Progress",
                results[1].loginAction.getContentIfNotHandled() == ResultState.Progress
            )
            assertTrue(
                "Should be Failure",
                results[2].loginAction.getContentIfNotHandled()?.failure == testException
            )
        }
}