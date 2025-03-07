package com.example.hotmovies.presentation.movies.list.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.interfaces.UserDetailsUseCase
import com.example.hotmovies.domain.User
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateViewModelAction
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class UserDetailsAction(
    coroutineScope: CoroutineScope,
    diContainer: DIContainer
) : BaseResultStateViewModelAction<Unit, User>(coroutineScope) {

    private val movieDataRepository = diContainer.tmdbMovieDataRepository

    override fun action(value: Unit): Flow<ResultState<User>> {
        return UserDetailsUseCase(movieDataRepository)()
    }
}