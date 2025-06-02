package com.example.hotmovies.presentation.movies.list.viewModel

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import com.example.hotmovies.R
import com.example.hotmovies.appplication.login.LogoutUserCase
import com.example.hotmovies.appplication.movies.interfaces.UserDetailsUseCase
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.domain.User
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel.UIState
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Intents.LoadMovies
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Intents.LoadUserDetails
import com.example.hotmovies.presentation.movies.list.viewModel.MoviesViewModel.Intents.Logout
import com.example.hotmovies.presentation.movies.list.viewModel.actions.LogoutAction
import com.example.hotmovies.presentation.movies.list.viewModel.actions.MoviesAction
import com.example.hotmovies.presentation.movies.list.viewModel.actions.UserDetailsAction
import com.example.hotmovies.presentation.shared.viewModels.CustomViewModel
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.progress
import com.example.hotmovies.shared.progressEvent
import com.example.hotmovies.shared.stateEvent
import com.example.hotmovies.shared.stateEventFailure
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class MoviesViewModel(
    private val resources: Resources,
    moviePager: Pager<Int, Movie>,
    userDetailsUseCase: UserDetailsUseCase,
    logoutUseCase: LogoutUserCase,
) : CustomViewModel() {

    data class UserDetailsUIState(
        val name: String,
        val userName: String,
        val overview: String,
        val avatar: BitmapDrawable?,
    ) {
        companion object {
            fun defaultState() = UserDetailsUIState(
                "", "", "", null
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
        val userDetailsAction: Event<ResultState<Boolean>>,
        val logoutAction: Event<ResultState<Boolean>>
    ) {
        companion object {
            fun defaultState() = UIState(
                UserDetailsUIState.defaultState(),
                Event(false),
                false.stateEvent(),
                false.stateEvent()
            )
        }
    }

    private val logoutAction = LogoutAction(logoutUseCase)
    private val moviesAction = MoviesAction(moviePager, viewModelScope)
    private val userDetailsAction = UserDetailsAction(userDetailsUseCase)
    private val showingMovieDetail = MutableStateFlow(Event(false))

    private var job: Job? = null
    private var state_ = MutableSharedFlow<UIState>()
    val state = state_
        .onStart {
            startActions()
        }
        .onCompletion {
            stopActions()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIState.defaultState())

    private fun startActions() {
        stopActions()
        println("***** Connected")
        job = viewModelScope.launch {
            showingMovieDetail.onEach {
                state_.emit(state.value.copy(movieDetailAction = it))
            }.launchIn(this)
            logoutAction.state.onEach {
                state_.emit(reduceLogout(state.value, it))
            }.launchIn(this)
            userDetailsAction.state.onEach {
                state_.emit(reduceUserDetails(state.value, resources, it))
            }.launchIn(this)
        }
    }

    private fun stopActions() {
        job?.cancel()
        job = null
        println("***** Disconnected")
    }

    val moviesPagingData: Flow<PagingData<Movie>> = moviesAction.state.onStart {
        sendIntent(LoadUserDetails)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(60.minutes), 1)

    private fun reduceLogout(state: UIState, result: ResultState<Unit>): UIState {
        checkMainThread()
        return when (result) {
            is ResultState.Success -> state.copy(
                logoutAction = true.stateEvent()
            )

            is progress -> state.copy(
                userDetailsAction = progressEvent, logoutAction = progressEvent
            )

            is ResultState.Failure -> state.copy(logoutAction = result.exception.stateEventFailure())
        }
    }

    private fun reduceUserDetails(
        state: UIState, resources: Resources, result: ResultState<User>
    ): UIState {
        checkMainThread()
        return when (result) {
            is ResultState.Success -> state.copy(
                userDetails = UserDetailsUIState.fromDomain(
                    resources, result.value
                ), userDetailsAction = true.stateEvent()
            )

            is progress -> state.copy(userDetailsAction = progressEvent)
            is ResultState.Failure -> state.copy(userDetailsAction = result.exception.stateEventFailure())
        }
    }

    sealed interface Intents {
        data class ShowingMovieDetail(val isActive: Boolean) : Intents
        data object LoadUserDetails : Intents
        data object LoadMovies : Intents
        data object Logout : Intents
    }

    fun sendIntent(action: Intents) {
        when (action) {
            is Intents.ShowingMovieDetail -> showingMovieDetail.value = Event(action.isActive)
            is LoadMovies -> moviesAction.run(Unit)
            is LoadUserDetails -> userDetailsAction.run(Unit)
            is Logout -> logoutAction.run(Unit)
        }
    }
}