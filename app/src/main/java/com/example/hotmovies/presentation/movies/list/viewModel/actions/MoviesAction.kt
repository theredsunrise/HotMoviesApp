package com.example.hotmovies.presentation.movies.list.viewModel.actions

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.appplication.movies.interfaces.MoviesUseCase
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.presentation.shared.viewModels.BaseViewModelAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class MoviesAction(
    diContainer: DIContainer,
    coroutineScope: CoroutineScope,
    private val cacheCoroutineScope: CoroutineScope
) : BaseViewModelAction<Unit, PagingData<Movie>>(1, coroutineScope = coroutineScope) {

    private val movieDataRepository = diContainer.tmdbMovieDataRepository

    override fun action(value: Unit): Flow<PagingData<Movie>> {
        return MoviesUseCase(movieDataRepository)().cachedIn(cacheCoroutineScope)
    }
}