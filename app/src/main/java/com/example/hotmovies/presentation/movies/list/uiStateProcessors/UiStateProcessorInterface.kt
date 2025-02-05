package com.example.hotmovies.presentation.movies.list.uiStateProcessors

import kotlinx.coroutines.CoroutineScope

interface UiStateProcessorInterface {
    suspend fun collect(coroutineScope: CoroutineScope)
}