package com.example.hotmovies.presentation.movies.detail.viewModel

import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.movies.interfaces.MovieDataRepositoryInterface
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModel.Actions.LoadMovieDetails
import com.example.hotmovies.presentation.movies.detail.viewModel.actions.MovieDetailsAction
import com.example.hotmovies.presentation.shared.viewModels.CustomViewModel
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.progress
import com.example.hotmovies.shared.progressEvent
import com.example.hotmovies.shared.stateEvent
import com.example.hotmovies.shared.stateEventFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MovieDetailsViewModel(movieDataRepository: MovieDataRepositoryInterface) : CustomViewModel() {

    data class MovieDetailsUIState(
        val title: String,
        val overview: String,
        val posterUrl: String?,
        val backDropUrl: String?,
        val rating: Float
    ) {

        companion object {
            fun defaultState() = MovieDetailsUIState("", "", null, null, 0f)
            fun fromDomain(movieDetails: MovieDetails) = MovieDetailsUIState(
                movieDetails.title,
                movieDetails.overview.orEmpty(),
                movieDetails.posterPath,
                movieDetails.backdropPath,
                movieDetails.voteAverage.let { it.toFloat() * 5f * 0.1f }
            )
        }
    }

    data class UIState(
        val movieDetails: MovieDetailsUIState,
        val loadAction: Event<ResultState<Boolean>>
    ) {
        companion object {
            fun defaultState() = UIState(MovieDetailsUIState.defaultState(), false.stateEvent())
        }
    }

    private val movieDetailsAction =
        MovieDetailsAction(viewModelScope, movieDataRepository)

    private var _state = MutableStateFlow(UIState.defaultState())
    val state = _state.asStateFlow()

    init {
        movieDetailsAction.state.onEach { result ->
            checkMainThread()
            when (result) {
                is ResultState.Success -> {
                    _state.update {
                        it.copy(
                            MovieDetailsUIState.fromDomain(result.value),
                            loadAction = true.stateEvent()
                        )
                    }
                }

                is progress -> _state.update { it.copy(loadAction = progressEvent) }
                is ResultState.Failure -> _state.update { it.copy(loadAction = result.exception.stateEventFailure()) }
            }
        }.launchIn(viewModelScope)
    }

    sealed interface Actions {
        data class LoadMovieDetails(val movieId: Int) : Actions
    }

    fun doAction(action: Actions) {
        when (action) {
            is LoadMovieDetails -> movieDetailsAction.run(action.movieId)
        }
    }
}