package com.example.hotmovies.appplication.movies.interfaces

import androidx.paging.PagingData
import com.example.hotmovies.appplication.login.interfaces.MovieDataPagingRepositoryInterface
import com.example.hotmovies.domain.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class MoviesUseCase(
    private val movieDataRepository: MovieDataPagingRepositoryInterface
) {

    operator fun invoke(): Flow<PagingData<Movie>> =
        movieDataRepository.getTrendingMoviesInfo().distinctUntilChanged()
}