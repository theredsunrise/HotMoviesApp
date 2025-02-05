package com.example.hotmovies.infrastructure.dataRepository

import com.example.hotmovies.domain.MovieDetails
import com.example.hotmovies.domain.MoviesInfo
import com.example.hotmovies.domain.User
import kotlinx.coroutines.flow.Flow

interface MovieDataRepositoryInterface {
    fun getUser(): Flow<User>
    fun getMovieDetails(movieId: Int): Flow<MovieDetails>
    fun getTrendingMoviesInfo(pageId: Int, itemsPerPage: Int): Flow<MoviesInfo>
}