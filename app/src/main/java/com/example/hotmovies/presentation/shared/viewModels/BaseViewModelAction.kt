package com.example.hotmovies.presentation.shared.viewModels

import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
abstract class BaseViewModelAction<I, O>(
    coroutineScope: CoroutineScope,
    replay: Int = 0,
    onStart: (suspend () -> Unit)? = null
) {
    private var trigger =
        MutableSharedFlow<I>(replay, 1, BufferOverflow.DROP_OLDEST)

    protected abstract fun action(value: I): Flow<O>

    val state: Flow<O> =
        trigger.onStart {
            onStart?.invoke()
        }.debounce(100).flatMapLatest { input ->
            action(input)
        }.shareIn(coroutineScope, SharingStarted.Lazily, replay)

    fun run(input: I) {
        trigger.tryEmit(input)
    }
}

typealias BaseAsyncViewModelAction<I, O> = BaseViewModelAction<I, Async<O>>
typealias BaseAsyncEventViewModelAction<I, O> = BaseViewModelAction<I, Event<Async<O>>>