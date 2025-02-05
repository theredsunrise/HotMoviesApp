package com.example.hotmovies.presentation.movies.detail.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.presentation.shared.viewModels.BaseAsyncViewModelAction
import com.example.hotmovies.shared.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class MovieDetailsAction(
    diContainer: DIContainer,
    coroutineScope: CoroutineScope
) :
    BaseAsyncViewModelAction<Int, MovieDetails>(1, coroutineScope) {

    private val movieDataRepository = diContainer.tmdbMovieDataRepository

    override fun action(value: Int): Flow<Async<MovieDetails>> {
        return MovieDetailsUseCase(movieDataRepository)(value)
    }
}

