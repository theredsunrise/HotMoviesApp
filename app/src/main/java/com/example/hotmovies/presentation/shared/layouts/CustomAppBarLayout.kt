package com.example.hotmovies.presentation.shared.layouts

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class CustomAppBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.appBarLayoutStyle
) : AppBarLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    enum class UiState {
        IDLE,
        COLLAPSED,
        EXPANDED
    }

    private var _collapseState = MutableStateFlow(UiState.IDLE)
    val collapseState = _collapseState.asStateFlow()

    private val callback: OnOffsetChangedListener = OnOffsetChangedListener { _, verticalOffset ->
        _collapseState.value = when {
            verticalOffset == 0 -> UiState.EXPANDED
            abs(verticalOffset) >= totalScrollRange -> UiState.COLLAPSED
            else -> UiState.IDLE
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requireNotNull(findViewTreeLifecycleOwner()).lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        addOnOffsetChangedListener(callback)
    }

    override fun onStop(owner: LifecycleOwner) {
        removeOnOffsetChangedListener(callback)
        super.onStop(owner)
    }
}