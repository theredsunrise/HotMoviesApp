package com.example.hotmovies.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface Async<out T> {
    data object Progress : Async<Nothing>
    data class Success<T>(val value: T) : Async<T>
    data class Failure(val exception: Exception) : Async<Nothing>

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

fun <T> T.async(): Async.Success<T> = Async.Success(this)
fun <T> T.asyncEvent(): Event<Async.Success<T>> = Event(Async.Success(this))
fun Exception.asyncFailure() = Async.Failure(this)
fun Exception.asyncEventFailure() = Event(Async.Failure(this))
typealias progress = Async.Progress

val progressEvent: Event<Async.Progress> get() = Event(progress)

fun <T> Flow<T>.asResult(): Flow<Async<T>> {
    return this.map<T, Async<T>> { it.async() }
        .onStart { emit(progress) }
        .catch { e ->
            if (e !is Exception) throw e
            emit(e.asyncFailure())
        }
}