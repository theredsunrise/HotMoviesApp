package com.example.hotmovies.presentation.movies.list.uiStateProcessors

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState.Loading
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.example.hotmovies.R
import com.example.hotmovies.databinding.FragmentMoviesBinding
import com.example.hotmovies.presentation.movies.list.MoviesFragmentDirections
import com.example.hotmovies.presentation.movies.list.adapters.MoviesAdapter
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.Logout
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentFactory
import com.example.hotmovies.presentation.shared.helpers.FragmentExitDialogFactory
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.safeNavigation
import com.example.hotmovies.shared.userInteractionComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProcessUserLogout(
    private val fragment: Fragment,
    private val binding: FragmentMoviesBinding,
    private val moviesViewModel: MoviesViewModel
) : UiStateProcessorInterface {

    private val exitDialogFactory: FragmentExitDialogFactory = FragmentExitDialogFactory(
        "backPressing", R.string.dialog_info_title,
        fragment.getString(
            R.string.dialog_msg_are_you_sure_to_logout
        )
    )
    private val logoutDialogFactory = DialogFragmentFactory("logout")

    init {
        fragment.lifecycle.apply {
            addObserver(logoutDialogFactory)
            addObserver(exitDialogFactory)
        }
    }

    override suspend fun collect(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesViewModel.state.collect { state ->
                processLogoutAction(state)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            logoutDialogFactory.state.collect exit@{ action ->
                checkMainThread()
                if (action is Accept) else {
                    return@exit
                }
                moviesViewModel.doAction(Logout)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            exitDialogFactory.state.collect {
                moviesViewModel.doAction(Logout)
            }
        }
    }

    private fun processLogoutAction(state: MoviesViewModel.UIState) {
        val logoutAction = state.logoutAction.getContentIfNotHandled() ?: return
        checkMainThread()

        fragment.userInteractionComponent.isEnabled = !logoutAction.isProgress

        when {
            logoutAction.isSuccessTrue -> {
                fragment.exitTransition = null
                fragment.findNavController().safeNavigation(R.id.moviesFragment) {
                    val navDirection =
                        MoviesFragmentDirections.actionMovieFragmentToLoginFragment()
                    navigate(navDirection)
                }
            }

            logoutAction is Async.Progress -> {
                val loadStates = LoadStates(
                    Loading,
                    Loading,
                    Loading
                )
                moviesAdapter.submitData(
                    fragment.viewLifecycleOwner.lifecycle,
                    PagingData.from(emptyList(), sourceLoadStates = loadStates)
                )
            }

            logoutAction is Async.Failure -> logoutDialogFactory.showErrorDialog(
                fragment.findNavController(),
                logoutAction.exception
            )
        }
    }

    private val moviesAdapter: MoviesAdapter
        get() = binding.recyclerView.adapter as MoviesAdapter
}