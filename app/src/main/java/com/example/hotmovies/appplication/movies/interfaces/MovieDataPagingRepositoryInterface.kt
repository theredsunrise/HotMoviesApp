package com.example.hotmovies.appplication.login.interfaces

import androidx.paging.PagingData
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.domain.User
import kotlinx.coroutines.flow.Flow

interface MovieDataPagingRepositoryInterface {
    fun getUser(): Flow<User>
    fun getMovieDetails(movieId: Int): Flow<MovieDetails>
    fun getTrendingMoviesInfo(): Flow<PagingData<Movie>>
}