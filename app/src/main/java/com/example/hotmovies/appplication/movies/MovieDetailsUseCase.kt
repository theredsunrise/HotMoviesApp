package com.example.hotmovies.appplication.movies

import com.example.hotmovies.appplication.movies.interfaces.MovieDataRepositoryInterface
import com.example.hotmovies.di.IODispatcher
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.asStateResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieDetailsUseCase @Inject constructor(
    private val movieDataRepository: MovieDataRepositoryInterface,
    private @IODispatcher val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(movieId: Int): Flow<ResultState<MovieDetails>> =
        movieDataRepository.getMovieDetails(movieId).asStateResult().flowOn(dispatcher)
}