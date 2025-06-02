package com.example.hotmovies.presentation.login.initialization.viewModel

import TestException
import com.example.hotmovies.appplication.login.SessionValidityUseCase
import com.example.hotmovies.appplication.login.interfaces.LoginRepositoryInterface
import com.example.hotmovies.appplication.login.interfaces.SettingsRepositoryInterface
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModel
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import common.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import io.mockk.verifyAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class InitializationViewModelTest {
    private val token = "129812398173"
    private val testException = TestException()

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var loginRepository: LoginRepositoryInterface

    @MockK
    lateinit var settingsRepository: SettingsRepositoryInterface

    @Test
    fun `session validity - check if the login session is valid - succeeded`() = runTest {
        coEvery { loginRepository.isSessionValid(token) } returns
                flowOf(true)

        coEvery { settingsRepository.getStringValue(SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY) } returns flowOf(
            token
        )
        coEvery {
            settingsRepository.store(
                SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY,
                any()
            )
        } returns flowOf(Unit)

        val results = listenToStatesOfViewModel()
        verifyAll {
            loginRepository.isSessionValid(any())
            settingsRepository.getStringValue(any())
        }

        assertTrue(
            "Should be Progress",
            results[0].getContentIfNotHandled() == ResultState.Progress
        )
        assertTrue(
            "Should be Success with True",
            results[1].getContentIfNotHandled()?.success == true
        )
    }

    @Test
    fun `session validity - check if the login session is valid - failed`() = runTest {
        coEvery { loginRepository.isSessionValid(token) } returns
                flowOf(false)

        coEvery { settingsRepository.getStringValue(SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY) } returns flowOf(
            token
        )
        coEvery {
            settingsRepository.store(
                SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY,
                any()
            )
        } returns flowOf(Unit)

        val results = listenToStatesOfViewModel()

        verifyAll {
            loginRepository.isSessionValid(any())
            settingsRepository.getStringValue(any())
        }

        assertTrue(
            "Should be Progress",
            results[0].getContentIfNotHandled() == ResultState.Progress
        )
        assertTrue(
            "Should be Success with False",
            results[1].getContentIfNotHandled()?.success == false
        )
    }

    @Test
    fun `session validity - check if the login session is valid - throws error`() = runTest {
        coEvery { loginRepository.isSessionValid(token) } throws testException
        coEvery { settingsRepository.getStringValue(SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY) } returns flowOf(
            token
        )
        coEvery {
            settingsRepository.store(
                SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY,
                any()
            )
        } returns flowOf(Unit)

        val results = listenToStatesOfViewModel()

        verifyAll {
            loginRepository.isSessionValid(any())
            settingsRepository.getStringValue(any())
        }

        assertTrue(
            "Should be Progress",
            results[0].getContentIfNotHandled() == ResultState.Progress
        )
        assertTrue(
            "Should be Failure",
            results[1].getContentIfNotHandled()?.failure == testException
        )
    }

    @Test
    fun `session validity - stored token was not found - failed`() = runTest {
        coEvery { settingsRepository.getStringValue(SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY) } returns
                flow {
                    throw SettingsRepositoryInterface.Exceptions.NoValueException(
                        "Key",
                        Boolean::class.java
                    )
                }

        coEvery {
            settingsRepository.store(
                SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY,
                any()
            )
        } returns flowOf(Unit)

        val results = listenToStatesOfViewModel()

        verify {
            settingsRepository.getStringValue(any())
        }

        assertTrue(
            "Should be Progress",
            results[0].getContentIfNotHandled() == ResultState.Progress
        )
        assertTrue(
            "Should be Failure",
            results[1].getContentIfNotHandled()?.success == false
        )
    }

    @Test
    fun `session validity - failed to fetch the stored token - throws error`() = runTest {
        coEvery { settingsRepository.getStringValue(SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY) } returns
                flow {
                    throw testException
                }

        coEvery {
            settingsRepository.store(
                SettingsRepositoryInterface.Keys.AUTH_TOKEN_KEY,
                any()
            )
        } returns flowOf(Unit)

        val results = listenToStatesOfViewModel()

        verify {
            settingsRepository.getStringValue(any())
        }

        assertTrue(
            "Should be Progress",
            results[0].getContentIfNotHandled() == ResultState.Progress
        )
        assertTrue(
            "Should be Failure",
            results[1].getContentIfNotHandled()?.failure == testException
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.listenToStatesOfViewModel(): MutableList<Event<ResultState<Boolean>>> {
        val viewModel = InitializationViewModel(
            SessionValidityUseCase(
                loginRepository,
                settingsRepository,
                mainDispatcherRule.testDispatcher
            )
        )

        val results = mutableListOf<Event<ResultState<Boolean>>>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.state.collect {
                results.add(it)
            }
        }
        advanceUntilIdle()
        return results
    }
}