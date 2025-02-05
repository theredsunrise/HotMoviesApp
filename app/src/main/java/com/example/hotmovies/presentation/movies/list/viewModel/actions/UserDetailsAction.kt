package com.example.hotmovies.presentation.movies.list.viewModel.actions

import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.interfaces.UserDetailsUseCase
import com.example.hotmovies.domain.User
import com.example.hotmovies.presentation.shared.viewModels.BaseAsyncViewModelAction
import com.example.hotmovies.shared.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class UserDetailsAction(
    diContainer: DIContainer,
    coroutineScope: CoroutineScope
) : BaseAsyncViewModelAction<Unit, User>(1, coroutineScope = coroutineScope) {

    private val movieDataRepository = diContainer.tmdbMovieDataRepository

    override fun action(value: Unit): Flow<Async<User>> {
        return UserDetailsUseCase(movieDataRepository)()
    }
}