package com.example.hotmovies.presentation.movies.list.viewModel.actions

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.hotmovies.domain.Movie
import com.example.hotmovies.presentation.shared.viewModels.BaseViewModelAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class MoviesAction(
    private val moviePager: Pager<Int, Movie>,
    private val cacheCoroutineScope: CoroutineScope
) : BaseViewModelAction<Unit, PagingData<Movie>>() {

    override fun action(value: Unit): Flow<PagingData<Movie>> {
        return moviePager.flow.cachedIn(cacheCoroutineScope)
    }
}