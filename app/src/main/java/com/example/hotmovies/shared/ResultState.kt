package com.example.hotmovies.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface ResultState<out T> {
    data object Progress : ResultState<Nothing>
    data class Success<T>(val value: T) : ResultState<T>
    data class Failure(val exception: Exception) : ResultState<Nothing>

    val isProgress: Boolean
        get() {
            return when (this) {
                is Progress -> true
                else -> false
            }
        }

    val success: T?
        get() {
            return when (this) {
                is Success -> this.value
                else -> null
            }
        }

    val isSuccess: Boolean
        get() = success != null

    val isSuccessTrue: Boolean
        get() = success == true

    val isSuccessFalse: Boolean
        get() = success == false

    val failure: Exception?
        get() {
            return when (this) {
                is Failure -> this.exception
                else -> null
            }
        }

    val isFailure: Boolean
        get() = failure != null

    fun print(): String {
        return when (this) {
            is Progress -> "Progress"
            is Success -> value.toString()
            is Failure -> {
                this.exception.message.orEmpty()
            }
        }
    }
}

fun <T> T.state(): ResultState.Success<T> = ResultState.Success(this)
fun <T> T.stateEvent(): Event<ResultState.Success<T>> = Event(ResultState.Success(this))
fun Exception.stateFailure() = ResultState.Failure(this)
fun Exception.stateEventFailure() = Event(ResultState.Failure(this))
typealias progress = ResultState.Progress

val progressEvent: Event<ResultState.Progress> get() = Event(progress)

fun <T> Flow<T>.asStateResult(): Flow<ResultState<T>> {
    return this.map<T, ResultState<T>> { it.state() }
        .onStart { emit(progress) }
        .catch { e ->
            if (e !is Exception) throw e
            emit(e.stateFailure())
        }
}