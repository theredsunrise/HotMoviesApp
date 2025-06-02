package com.example.hotmovies.presentation.movies.detail.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.presentation.movies.detail.viewModel.MovieDetailsViewModel.Intents.LoadMovieDetails
import com.example.hotmovies.presentation.movies.detail.viewModel.actions.MovieDetailsAction
import com.example.hotmovies.presentation.shared.viewModels.CustomViewModel
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.progress
import com.example.hotmovies.shared.progressEvent
import com.example.hotmovies.shared.stateEvent
import com.example.hotmovies.shared.stateEventFailure
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailsViewModel(savedStateHandle: SavedStateHandle, useCase: MovieDetailsUseCase) :
    CustomViewModel() {

    private val movie: Movie = savedStateHandle["movie"]!!

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

    private val movieDetailsAction = MovieDetailsAction(useCase)

    private var job: Job? = null
    private var state_ = MutableSharedFlow<UIState>()
    val state = state_
        .onStart {
            startActions()
            sendIntent(LoadMovieDetails(movie.id))
        }
        .onCompletion {
            stopActions()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIState.defaultState())

    private fun startActions() {
        stopActions()
        println("***** Connected")
        job = viewModelScope.launch {
            movieDetailsAction.state.onEach {
                state_.emit(reduceMovieDetails(state.value, it))
            }.launchIn(this)
        }
    }

    private fun stopActions() {
        job?.cancel()
        job = null
        println("***** Disconnected")
    }

    private fun reduceMovieDetails(state: UIState, result: ResultState<MovieDetails>): UIState {
        checkMainThread()
        return when (result) {
            is ResultState.Success -> {
                state.copy(
                    movieDetails = MovieDetailsUIState.fromDomain(result.value),
                    loadAction = true.stateEvent()
                )
            }

            is progress -> state.copy(loadAction = progressEvent)
            is ResultState.Failure -> state.copy(loadAction = result.exception.stateEventFailure())
        }
    }

    sealed interface Intents {
        data class LoadMovieDetails(val movieId: Int) : Intents
    }

    fun sendIntent(action: Intents) {
        when (action) {
            is LoadMovieDetails -> movieDetailsAction.run(action.movieId)
        }
    }
}