package com.example.hotmovies.presentation.shared.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.imageview.ShapeableImageView

class ImageViewWithAnimator
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

    private var animator: Animator? = null

    fun cancelAnimator() {
        animator?.cancel()
        animator?.setTarget(null)
        animator = null
    }

    fun runAnimator(block: AppCompatImageView.() -> Animator) {
        if (isAttachedToWindow) {
            cancelAnimator()
            animator = this.block().also { it.start() }
        }
    }

    override fun onDetachedFromWindow() {
        cancelAnimator()
        super.onDetachedFromWindow()
    }
}