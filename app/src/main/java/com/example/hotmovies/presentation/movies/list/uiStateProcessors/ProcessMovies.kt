package com.example.hotmovies.presentation.movies.list.uiStateProcessors

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState.Loading
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.example.hotmovies.databinding.FragmentMoviesBinding
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.presentation.movies.list.adapters.MoviesAdapter
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadMovies
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentHandler
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.failure
import com.example.hotmovies.shared.isLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProcessMovies(
    private val fragment: Fragment,
    private val binding: FragmentMoviesBinding,
    private val moviesViewModel: MoviesViewModel
) : UiStateProcessorInterface {

    private val moviesDialogHandler = DialogFragmentHandler("movies")

    init {
        fragment.lifecycle.addObserver(moviesDialogHandler)
    }

    override suspend fun collect(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesViewModel.moviesPagingData
                .combine(
                    moviesViewModel.state,
                    { movies, state -> Pair(movies, state.logoutAction) })
                .collectLatest { groupedState ->
                    checkMainThread()
                    processLoadMoviesAction(groupedState)
                }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesAdapter.loadStateFlow.collect() {
                checkMainThread()
                processMovieLoadStatesAction(it)
            }
        }
        coroutineScope.launch(Dispatchers.Main.immediate) {
            moviesDialogHandler.state.collect exit@{ action ->
                checkMainThread()
                if (action is Accept) else {
                    return@exit
                }
                moviesViewModel.doAction(LoadMovies)
            }
        }
    }

    private fun processLoadMoviesAction(groupedState: Pair<PagingData<Movie>, Event<ResultState<Boolean>>>) {
        val pagingData = with(groupedState) {
            val (movies, logoutAction) = groupedState
            val showLoading =
                with(logoutAction.content) { isSuccessTrue || isProgress || isFailure }
            if (showLoading) {
                val loadStates = LoadStates(
                    Loading,
                    Loading,
                    Loading
                )
                PagingData.from(emptyList<Movie>(), sourceLoadStates = loadStates)
            } else {
                movies
            }
        }
        moviesAdapter.submitData(fragment.viewLifecycleOwner.lifecycle, pagingData)
    }

    private fun processMovieLoadStatesAction(loadStates: CombinedLoadStates) {
        checkMainThread()
        fragment.startPostponedEnterTransition()

        loadStates.source.apply {
            val isRefreshLoading = refresh.isLoading
            binding.prependProgress.isVisible =
                prepend.isLoading || isRefreshLoading
            binding.appendProgress.isVisible =
                append.isLoading || isRefreshLoading

            arrayOf(append, prepend, refresh).firstNotNullOfOrNull { it.failure }?.let { error ->
                moviesDialogHandler.showErrorDialog(fragment.findNavController(), error)
            }
        }
    }

    private val moviesAdapter: MoviesAdapter
        get() = binding.recyclerView.adapter as MoviesAdapter
}