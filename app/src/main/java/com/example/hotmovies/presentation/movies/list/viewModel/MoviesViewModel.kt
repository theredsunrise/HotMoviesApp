package com.example.hotmovies.presentation.movies.list.viewModel

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.hotmovies.R
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.domain.User
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadMovies
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.LoadUserDetails
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Actions.Logout
import com.example.hotmovies.presentation.movies.list.viewModel.actions.LogoutAction
import com.example.hotmovies.presentation.movies.list.viewModel.actions.MoviesAction
import com.example.hotmovies.presentation.movies.list.viewModel.actions.UserDetailsAction
import com.example.hotmovies.presentation.shared.viewModels.CustomViewModel
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.asyncEvent
import com.example.hotmovies.shared.asyncEventFailure
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.progress
import com.example.hotmovies.shared.progressEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class MoviesViewModel(diContainer: DIContainer) : CustomViewModel() {

    data class UserDetailsUIState(
        val name: String,
        val userName: String,
        val overview: String,
        val avatar: BitmapDrawable?,
    ) {
        companion object {
            fun defaultState() = UserDetailsUIState(
                "",
                "",
                "",
                null
            )

            fun fromDomain(resources: Resources, user: User): UserDetailsUIState {
                return UserDetailsUIState(
                    user.name,
                    user.userName,
                    resources.getString(R.string.my_overview),
                    user.avatar?.let { BitmapDrawable(resources, it) })
            }
        }
    }

    data class UIState(
        val userDetails: UserDetailsUIState,
        var movieDetailAction: Event<Boolean>,
        val loadAction: Event<Async<Boolean>>,
        val logoutAction: Event<Async<Boolean>>
    ) {
        companion object {
            fun defaultState() =
                UIState(
                    UserDetailsUIState.defaultState(),
                    Event(false),
                    false.asyncEvent(),
                    false.asyncEvent()
                )
        }
    }

    sealed interface Actions {
        data class ShowingMovieDetail(val isActive: Boolean) : Actions
        data object LoadUserDetails : Actions
        data object LoadMovies : Actions
        data object Logout : Actions
    }

    private val resources = diContainer.appContext.resources
    private val logoutAction = LogoutAction(diContainer, viewModelScope)
    private val moviesAction = MoviesAction(diContainer, viewModelScope, viewModelScope)
    private val userDetailsAction = UserDetailsAction(diContainer, viewModelScope)

    private var _state = MutableStateFlow(UIState.defaultState())
    val state = _state.asStateFlow()
    val adapterMoviesFlow: Flow<PagingData<Movie>> = moviesAction.state

    init {
        userDetailsAction.state.onEach { result ->
            checkMainThread()
            when (result) {
                is Async.Success -> {
                    _state.update {
                        it.copy(
                            userDetails = UserDetailsUIState.fromDomain(
                                resources,
                                result.value
                            ),
                            loadAction = true.asyncEvent()
                        )
                    }

                }

                is progress -> _state.update { it.copy(loadAction = progressEvent) }
                is Async.Failure -> _state.update { it.copy(loadAction = result.exception.asyncEventFailure()) }
            }
        }.launchIn(viewModelScope)

        logoutAction.state.onEach { result ->
            checkMainThread()
            when (result) {
                is Async.Success -> {
                    _state.update {
                        it.copy(
                            logoutAction = true.asyncEvent()
                        )
                    }
                }

                is progress -> _state.update {
                    it.copy(
                        loadAction = progressEvent,
                        logoutAction = progressEvent
                    )
                }

                is Async.Failure -> _state.update { it.copy(logoutAction = result.exception.asyncEventFailure()) }
            }
        }.launchIn(viewModelScope)
    }

    fun doAction(action: Actions) {
        when (action) {
            is Actions.ShowingMovieDetail -> {
                _state.update {
                    it.copy(
                        movieDetailAction = Event(
                            action.isActive
                        )
                    )
                }
            }

            is LoadMovies -> moviesAction.run(Unit)
            is LoadUserDetails -> userDetailsAction.run(Unit)
            is Logout -> logoutAction.run(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}