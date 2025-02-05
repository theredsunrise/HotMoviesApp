package com.example.hotmovies.appplication.movies

import com.example.hotmovies.appplication.login.interfaces.MovieDataPagingRepositoryInterface
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.asResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class MovieDetailsUseCase(
    private val movieDataRepository: MovieDataPagingRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(movieId: Int): Flow<Async<MovieDetails>> =
        movieDataRepository.getMovieDetails(movieId).asResult().flowOn(dispatcher)
}