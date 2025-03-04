package com.example.hotmovies.presentation.movies.list.uiStateProcessors

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadMovies
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadUserDetails
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentHandler
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProcessUserDetails(
    private val fragment: Fragment,
    private val moviesViewModel: MoviesViewModel
) : UiStateProcessorInterface {

    private val userDetailsDialogHandler = DialogFragmentHandler("userDetails")

    init {
        fragment.lifecycle.addObserver(userDetailsDialogHandler)
    }

    override suspend fun collect(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesViewModel.state.collect { state ->
                processUserDetailsAction(state.userDetailsAction)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            userDetailsDialogHandler.state.collect exit@{ action ->
                checkMainThread()
                if (action is Accept) else {
                    return@exit
                }
                moviesViewModel.doAction(LoadUserDetails)
            }
        }
    }

    private fun processUserDetailsAction(loadAction: Event<ResultState<Boolean>>) {
        checkMainThread()
        val loadAction = loadAction.getContentIfNotHandled() ?: return

        when {
            loadAction.isSuccessTrue -> moviesViewModel.doAction(LoadMovies)
            loadAction.isSuccessFalse -> moviesViewModel.doAction(LoadUserDetails)
            loadAction is ResultState.Failure -> userDetailsDialogHandler.showErrorDialog(
                fragment.findNavController(),
                loadAction.exception
            )
        }
    }
}