package com.example.hotmovies.presentation.movies.detail.viewModel.actions

import com.example.hotmovies.appplication.movies.MovieDetailsUseCase
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateViewModelAction
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.flow.Flow

class MovieDetailsAction(private val useCase: MovieDetailsUseCase) :
    BaseResultStateViewModelAction<Int, MovieDetails>(1) {

    override fun action(value: Int): Flow<ResultState<MovieDetails>> {
        return useCase(value)
    }
}

