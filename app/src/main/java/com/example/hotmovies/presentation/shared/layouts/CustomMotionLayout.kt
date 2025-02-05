package com.example.hotmovies.presentation.shared.layouts

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout

class CustomMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) :
    MotionLayout(context, attrs, defStyleAttr) {

    private val customLayoutTransition = LayoutTransition().apply {
        enableTransitionType(LayoutTransition.APPEARING)
        enableTransitionType(LayoutTransition.CHANGING)
        enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        enableTransitionType(LayoutTransition.DISAPPEARING)
    }

    var isEnabledLayoutTransition: Boolean
        get() {
            return layoutTransition != null
        }
        set(value) {
            layoutTransition = if (value) {
                customLayoutTransition
            } else {
                null
            }
        }

    init {
        isEnabledLayoutTransition = true
        isTransitionGroup = true
    }
}