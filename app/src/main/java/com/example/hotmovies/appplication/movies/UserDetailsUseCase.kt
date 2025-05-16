package com.example.hotmovies.appplication.movies.interfaces

import com.example.hotmovies.di.IODispatcher
import com.example.hotmovies.domain.User
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.asStateResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserDetailsUseCase @Inject constructor(
    private val movieDataRepository: MovieDataRepositoryInterface,
    private @IODispatcher val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<ResultState<User>> =
        movieDataRepository.getUser().asStateResult().flowOn(dispatcher)
}