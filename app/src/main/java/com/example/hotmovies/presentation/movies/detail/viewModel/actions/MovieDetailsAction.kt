package com.example.hotmovies.presentation.movies.detail.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.presentation.shared.viewModels.BaseAsyncViewModelAction
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class MovieDetailsAction(
    coroutineScope: CoroutineScope,
    diContainer: DIContainer
) :
    BaseAsyncViewModelAction<Int, MovieDetails>(coroutineScope) {

    private val movieDataRepository = diContainer.tmdbMovieDataRepository

    override fun action(value: Int): Flow<ResultState<MovieDetails>> {
        return MovieDetailsUseCase(movieDataRepository)(value)
    }
}

