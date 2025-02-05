package com.example.hotmovies.presentation.shared

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hotmovies.appplication.DIContainer
import com.example.hotmovies.presentation.shared.KeyboardStateAwareComponent.KeyboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface KeyboardStateAwareComponent {
    data class KeyboardState(val imeVisible: Boolean = false) {
        private val timeStamp = System.nanoTime()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as KeyboardState

            if (imeVisible != other.imeVisible) return false
            if (timeStamp != other.timeStamp) return false

            return true
        }

        override fun hashCode(): Int {
            var result = imeVisible.hashCode()
            result = 31 * result + timeStamp.hashCode()
            return result
        }
    }

    val keyboardState: StateFlow<KeyboardState>
}

class CustomApplication : Application(), ActivityLifecycleCallbacks, KeyboardStateAwareComponent {
    val diContainer = DIContainer(this)
    private var _keyboardState = MutableStateFlow(KeyboardState())
    override val keyboardState: StateFlow<KeyboardState> = _keyboardState

    init {
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.setRecentsScreenshotEnabled(false)
        }
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        var imeVisible = false

        val decorView = activity.window.decorView
        ViewCompat.setWindowInsetsAnimationCallback(
            decorView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    return insets
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    super.onEnd(animation)
                    _keyboardState.value = KeyboardState(imeVisible)
                }
            })

        ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
            imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            insets
        }
    }

    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityResumed(p0: Activity) {}
    override fun onActivityPaused(p0: Activity) {}
    override fun onActivityStopped(p0: Activity) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityDestroyed(p0: Activity) {}
}