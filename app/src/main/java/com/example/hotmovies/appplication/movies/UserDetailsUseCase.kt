package com.example.hotmovies.appplication.movies.interfaces

import com.example.hotmovies.appplication.login.interfaces.MovieDataPagingRepositoryInterface
import com.example.hotmovies.domain.User
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.asResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class UserDetailsUseCase(
    private val movieDataRepository: MovieDataPagingRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke(): Flow<Async<User>> =
        movieDataRepository.getUser().asResult().flowOn(dispatcher)
}