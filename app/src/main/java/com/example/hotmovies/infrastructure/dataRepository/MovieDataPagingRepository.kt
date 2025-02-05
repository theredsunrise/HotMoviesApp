package com.example.hotmovies.infrastructure.dataRepository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.hotmovies.appplication.login.interfaces.MovieDataPagingRepositoryInterface
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.domain.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class MovieDataPagingRepository(
    private val pagingConfig: PagingConfig,
    private val dataRepository: MovieDataRepositoryInterface,
    private val dispatcher: CoroutineDispatcher
) :
    MovieDataPagingRepositoryInterface {
    override fun getUser(): Flow<User> = dataRepository.getUser().flowOn(dispatcher)

    override fun getMovieDetails(movieId: Int): Flow<MovieDetails> =
        dataRepository.getMovieDetails(movieId).flowOn(dispatcher)

    override fun getTrendingMoviesInfo(): Flow<PagingData<Movie>> =
        Pager(
            config = pagingConfig,
            pagingSourceFactory = {
                MoviePagingSource(
                    dataRepository,
                    dispatcher
                )
            }
        ).flow
}
