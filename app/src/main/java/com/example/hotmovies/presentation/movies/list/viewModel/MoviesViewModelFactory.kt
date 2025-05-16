package com.example.hotmovies.presentation.movies.list.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.Pager
import com.example.hotmovies.appplication.login.LogoutUserCase
import com.example.hotmovies.appplication.movies.interfaces.UserDetailsUseCase
import com.example.hotmovies.domain.Movie
import javax.inject.Inject

class MoviesViewModelFactory @Inject constructor(
    private val context: Context,
    private val pager: Pager<Int, Movie>,
    private val logoutUserCase: LogoutUserCase,
    private val userDetailsUseCase: UserDetailsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(
            context.resources,
            pager,
            userDetailsUseCase,
            logoutUserCase
        ) as T
    }
}

