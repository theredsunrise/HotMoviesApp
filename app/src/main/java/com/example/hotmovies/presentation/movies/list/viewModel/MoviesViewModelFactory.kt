package com.example.hotmovies.presentation.movies.list.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.login.LogoutUserCase
import com.example.hotmovies.appplication.movies.interfaces.UserDetailsUseCase
import kotlinx.coroutines.Dispatchers

class MoviesViewModelFactory(
    private val diContainer: DIContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(
            diContainer.appContext.resources,
            diContainer.pager,
            UserDetailsUseCase(diContainer.movieDataRepository, Dispatchers.IO),
            LogoutUserCase(
                diContainer.loginRepository, diContainer.settingsRepository, Dispatchers.IO
            )
        ) as T
    }
}

