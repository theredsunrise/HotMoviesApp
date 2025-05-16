package com.example.hotmovies.shared

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.os.Build
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.paging.LoadState
import com.example.hotmovies.presentation.CustomApplication
import com.example.hotmovies.presentation.KeyboardStateAwareComponent
import com.example.hotmovies.presentation.KeyboardStateAwareComponent.KeyboardState
import com.example.hotmovies.presentation.UserInteractionConfigurableComponent
import com.example.hotmovies.presentation.shared.layouts.CustomMotionLayout
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import kotlin.coroutines.resume
import kotlin.experimental.xor
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Activity.keyboardState: StateFlow<KeyboardState>
    get() = (application as KeyboardStateAwareComponent).keyboardState

suspend fun Activity.hideSoftKeyboardAsync() {
    keyboardState.takeWhile { state ->
        if (state.imeVisible) {
            val currentFocus = currentFocus?.windowToken
            val result = currentFocus != null &&
                    ((getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                        ?.hideSoftInputFromWindow(currentFocus, 0) ?: false)
            result
        } else {
            false
        }
    }.lastOrNull()
}

suspend fun View.doOnLayoutAsync(): Unit = suspendCancellableCoroutine { continuation ->
    if (isInLayout) {
        continuation.resume(Unit)
    } else {
        doOnLayout {
            continuation.resume(Unit)
        }
    }
}

suspend fun CustomMotionLayout.transitionToEndAsync(immediately: Boolean): Unit =
    suspendCancellableCoroutine { continuation ->
        val startTransitionId = this.definedTransitions[0].id
        val endId = this.definedTransitions[0].endConstraintSetId

        if (currentState == endId) {
            continuation.resume(Unit)
            return@suspendCancellableCoroutine
        }

        setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                isEnabledLayoutTransition = true
                setTransitionListener(null)
                continuation.resume(Unit)
            }
        })
        continuation.invokeOnCancellation {
            // TODO: cancel
            isEnabledLayoutTransition = true
            setTransitionListener(null)
        }

        isEnabledLayoutTransition = false
        setTransition(startTransitionId)

        if (immediately) {
            transitionToState(endId, 1)
        } else {
            transitionToState(endId)
        }
    }

suspend fun CustomMotionLayout.transitionToStartAsync(immediately: Boolean): Unit =
    suspendCancellableCoroutine { continuation ->
        val startTransitionId = this.definedTransitions[0].id
        val startId = this.definedTransitions[0].startConstraintSetId

        if (currentState == startId) {
            continuation.resume(Unit)
            return@suspendCancellableCoroutine
        }

        setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                super.onTransitionCompleted(motionLayout, currentId)
                isEnabledLayoutTransition = true
                setTransitionListener(null)
                continuation.resume(Unit)
            }
        })
        continuation.invokeOnCancellation {
            // TODO: cancel
            isEnabledLayoutTransition = true
            setTransitionListener(null)
        }

        isEnabledLayoutTransition = false
        setTransition(startTransitionId)

        if (immediately) {
            transitionToState(startId, 1)
        } else {
            transitionToState(startId)
        }
    }

@SuppressLint("NewApi")
@Suppress("DEPRECATION")
suspend fun Fragment.areSystemGesturesSupportedAsync(): Boolean =
    suspendCancellableCoroutine { continuation ->
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P || view == null) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }

        requireView().doOnPreDraw {
            val rootInsets = it.rootWindowInsets
            val version = Build.VERSION.SDK_INT
            val result = (
                    if (version <= Build.VERSION_CODES.Q) rootInsets.systemGestureInsets
                    else rootInsets.getInsets(WindowInsets.Type.systemGestures())
                    ).left > 0
            continuation.resume(result)
        }
    }

val Fragment.userInteractionComponent: UserInteractionConfigurableComponent
    get() = activity as UserInteractionConfigurableComponent

val View.bounds: RectF
    get() = RectF(0f, 0f, width.toFloat(), height.toFloat())

fun checkMainThread() {
    if (System.getProperty("isTest") == null) {
        assert(Looper.myLooper() == Looper.getMainLooper())
    }
}

fun checkNotMainThread() {
    if (System.getProperty("isTest") == null) {
        assert(Looper.myLooper() != Looper.getMainLooper())
    }
}

val Fragment.application get() = (requireActivity().application as CustomApplication)

//xor string with a char key
@OptIn(ExperimentalEncodingApi::class)
fun String.toUse(value: Byte): String {
    return Base64.decode(this).map { it xor value }.toByteArray().toString(Charsets.UTF_8)
}

fun ResponseBody.toBitmap(): Bitmap {
    return byteStream().use { BitmapFactory.decodeStream(it) }
}

val LoadState.isLoading: Boolean get() = this is LoadState.Loading
val LoadState.failure: Throwable? get() = (this as? LoadState.Error)?.let { it.error }

fun <V : View> Set<V>.toPairs(): Array<Pair<V, String>> {
    return map { Pair(it, it.transitionName) }.toTypedArray()
}

fun NavController.safeNavigation(
    currentDestinationId: Int,
    block: NavController.() -> Unit
): Boolean {
    if (this.currentDestination?.id != currentDestinationId) return false
    this.block()
    return true
}