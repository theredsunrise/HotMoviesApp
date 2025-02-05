package com.example.hotmovies.presentation.shared.transitions

import android.animation.Animator
import android.animation.FloatArrayEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.example.hotmovies.shared.bounds
import com.google.android.material.shape.AbsoluteCornerSize
import com.google.android.material.shape.CornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable

class ButtonCornerTransition : Transition {
    companion object {
        private const val SHAPE_MODEL =
            "com.example.hotmovies:buttonCornerTransition:shapeModel"

        fun properties() = arrayOf(SHAPE_MODEL)
    }

    override fun getTransitionProperties(): Array<String> {
        return properties()
    }

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        (transitionValues.view as? Shapeable)?.also {
            transitionValues.values[SHAPE_MODEL] = it.shapeAppearanceModel
        }
    }

    @SuppressLint("RestrictedApi")
    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        startValues ?: return null
        endValues ?: return null

        val shapeModelStart =
            (startValues.values[SHAPE_MODEL] as? ShapeAppearanceModel) ?: return null
        val shapeModelEnd = (endValues.values[SHAPE_MODEL] as? ShapeAppearanceModel) ?: return null

        val viewBoundsStart = startValues.view.bounds
        val viewBoundsEnd = endValues.view.bounds
        val cornerSizeArrays =
            computeCornerSizes(viewBoundsStart, shapeModelStart, viewBoundsEnd, shapeModelEnd)
        val animator =
            ValueAnimator.ofObject(FloatArrayEvaluator(), cornerSizeArrays[0], cornerSizeArrays[1])

        val view = endValues.view
        animator.addUpdateListener { animation ->
            objectsTransformation(animation.animatedValue as FloatArray, view, shapeModelEnd)
        }
        animator.addListener(
            onEnd = {
                animator.removeAllListeners()
                animator.removeAllUpdateListeners()
            },
            onCancel = {
                animator.removeAllListeners()
                animator.removeAllUpdateListeners()
                objectsTransformation(cornerSizeArrays[1], view, shapeModelEnd)
            }
        )
        return animator
    }

    @SuppressLint("RestrictedApi")
    private fun objectsTransformation(
        cornerSizeArray: FloatArray,
        view: View?,
        shapeModel: ShapeAppearanceModel
    ) {
        when (view) {
            is Shapeable -> {
                var i = 0
                view.shapeAppearanceModel = shapeModel.withTransformedCornerSizes {
                    AbsoluteCornerSize(cornerSizeArray[i++])
                }
            }
        }
    }

    private fun computeCornerSizes(
        viewBoundsStart: RectF,
        shapeModelStart: ShapeAppearanceModel,
        viewBoundsEnd: RectF,
        shapeModelEnd: ShapeAppearanceModel
    ): List<FloatArray> {
        val startTopLeftCornerSize =
            resolveCornerSize(viewBoundsStart, shapeModelStart.topLeftCornerSize)
        val startTopRightCornerSize =
            resolveCornerSize(viewBoundsStart, shapeModelStart.topRightCornerSize)
        val startBottomRightCornerSize =
            resolveCornerSize(viewBoundsStart, shapeModelStart.bottomRightCornerSize)
        val startBottomLeftCornerSize =
            resolveCornerSize(viewBoundsStart, shapeModelStart.bottomLeftCornerSize)
        val startValues = floatArrayOf(
            startTopLeftCornerSize,
            startTopRightCornerSize,
            startBottomLeftCornerSize,
            startBottomRightCornerSize
        )

        val endTopLeftCornerSize = resolveCornerSize(viewBoundsEnd, shapeModelEnd.topLeftCornerSize)
        val endTopRightCornerSize =
            resolveCornerSize(viewBoundsEnd, shapeModelEnd.topRightCornerSize)
        val endBottomRightCornerSize =
            resolveCornerSize(viewBoundsEnd, shapeModelEnd.bottomRightCornerSize)
        val endBottomLeftCornerSize =
            resolveCornerSize(viewBoundsEnd, shapeModelEnd.bottomLeftCornerSize)
        val endValues = floatArrayOf(
            endTopLeftCornerSize,
            endTopRightCornerSize,
            endBottomLeftCornerSize,
            endBottomRightCornerSize
        )

        return listOf(startValues, endValues)
    }

    private fun resolveCornerSize(viewBounds: RectF, cornerSize: CornerSize): Float =
        cornerSize.getCornerSize(viewBounds)
}