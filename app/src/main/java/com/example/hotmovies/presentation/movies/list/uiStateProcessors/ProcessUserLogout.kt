package com.example.hotmovies.presentation.movies.list.uiStateProcessors

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hotmovies.R
import com.example.hotmovies.presentation.movies.list.MoviesFragmentDirections
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Intents.Logout
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentHandler
import com.example.hotmovies.presentation.shared.helpers.FragmentExitDialogHandler
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.safeNavigation
import com.example.hotmovies.shared.userInteractionComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProcessUserLogout(
    private val fragment: Fragment,
    private val moviesViewModel: MoviesViewModel
) : UiStateProcessorInterface {

    private val exitDialogHandler: FragmentExitDialogHandler = FragmentExitDialogHandler(
        "backPressing", R.string.dialog_info_title,
        fragment.getString(
            R.string.dialog_msg_are_you_sure_to_logout
        )
    )
    private val logoutDialogHandler = DialogFragmentHandler("logout")

    init {
        fragment.lifecycle.apply {
            addObserver(logoutDialogHandler)
            addObserver(exitDialogHandler)
        }
    }

    override suspend fun collect(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesViewModel.state.collect { state ->
                processLogoutAction(state.logoutAction)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            logoutDialogHandler.state.collect exit@{ action ->
                checkMainThread()
                if (action is Accept) else {
                    return@exit
                }
                moviesViewModel.sendIntent(Logout)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            exitDialogHandler.state.collect {
                moviesViewModel.sendIntent(Logout)
            }
        }
    }

    private fun processLogoutAction(logoutAction: Event<ResultState<Boolean>>) {
        checkMainThread()
        fragment.userInteractionComponent.isEnabled = !logoutAction.content.isProgress
        val logoutAction = logoutAction.getContentIfNotHandled() ?: return
        when {
            logoutAction.isSuccessTrue -> {
                fragment.exitTransition = null
                fragment.findNavController().safeNavigation(R.id.moviesFragment) {
                    val navDirection =
                        MoviesFragmentDirections.actionMovieFragmentToLoginFragment()
                    navigate(navDirection)
                }
            }

            logoutAction is ResultState.Failure -> logoutDialogHandler.showErrorDialog(
                fragment.findNavController(),
                logoutAction.exception
            )
        }
    }
}