package com.example.hotmovies.presentation.movies.list.uiStateProcessors

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import com.example.hotmovies.databinding.FragmentMoviesBinding
import com.example.hotmovies.presentation.movies.list.adapters.MoviesAdapter
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadMovies
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadUserDetails
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentFactory
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.failure
import com.example.hotmovies.shared.isLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProcessCollectingMovies(
    private val fragment: Fragment,
    private val binding: FragmentMoviesBinding,
    private val moviesViewModel: MoviesViewModel
) : UiStateProcessorInterface {

    private val moviesDialogFactory = DialogFragmentFactory("movies")

    init {
        fragment.lifecycle.addObserver(moviesDialogFactory)
    }

    override suspend fun collect(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesViewModel.state.collect { state ->
                processCollectingMoviesAction(state)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesDialogFactory.state.collect exit@{ action ->
                checkMainThread()
                if (action is Accept) else {
                    return@exit
                }
                moviesViewModel.doAction(LoadUserDetails)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesViewModel.adapterMoviesFlow.collectLatest { movies ->
                checkMainThread()
                moviesAdapter.submitData(movies)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesAdapter.loadStateFlow.collect {
                checkMainThread()
                processMovieLoadStatesAction(it)
            }
        }
    }

    private fun processCollectingMoviesAction(state: MoviesViewModel.UIState) {
        val loadAction = state.loadAction.getContentIfNotHandled() ?: return
        checkMainThread()

        when {
            loadAction.isSuccessTrue -> moviesViewModel.doAction(LoadMovies)
            loadAction.isSuccessFalse -> moviesViewModel.doAction(LoadUserDetails)
            loadAction is Async.Failure -> moviesDialogFactory.showErrorDialog(
                fragment.findNavController(),
                loadAction.exception
            )
        }
    }

    private fun processMovieLoadStatesAction(loadStates: CombinedLoadStates) {
        fragment.startPostponedEnterTransition()

        loadStates.source.apply {
            val isRefreshLoading = refresh.isLoading
            binding.prependProgress.isVisible =
                prepend.isLoading || isRefreshLoading
            binding.appendProgress.isVisible =
                append.isLoading || isRefreshLoading

            arrayOf(append, prepend, refresh).firstNotNullOfOrNull { it.failure }?.let { error ->
                moviesDialogFactory.showErrorDialog(fragment.findNavController(), error)
            }
        }
    }

    private val moviesAdapter: MoviesAdapter
        get() = binding.recyclerView.adapter as MoviesAdapter
}