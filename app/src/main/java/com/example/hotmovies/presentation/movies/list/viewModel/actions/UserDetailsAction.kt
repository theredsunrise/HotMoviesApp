package com.example.hotmovies.presentation.movies.list.viewModel.actions

import com.example.hotmovies.appplication.movies.interfaces.UserDetailsUseCase
import com.example.hotmovies.domain.User
import com.example.hotmovies.presentation.shared.viewModels.BaseResultStateViewModelAction
import com.example.hotmovies.shared.ResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class UserDetailsAction(
    coroutineScope: CoroutineScope,
    private val useCase: UserDetailsUseCase
) : BaseResultStateViewModelAction<Unit, User>(coroutineScope) {

    override fun action(value: Unit): Flow<ResultState<User>> {
        return useCase()
    }
}