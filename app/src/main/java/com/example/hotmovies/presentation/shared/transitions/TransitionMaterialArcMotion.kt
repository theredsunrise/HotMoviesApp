package com.example.hotmovies.presentation.shared.transitions

import android.content.Context
import android.util.AttributeSet
import androidx.transition.PathMotion
import com.google.android.material.transition.MaterialArcMotion

class TransitionMaterialArcMotion(context: Context, attrs: AttributeSet) :
    PathMotion(context, attrs) {
    private val materialArcMotion = MaterialArcMotion()
    override fun getPath(startX: Float, startY: Float, endX: Float, endY: Float) =
        materialArcMotion.getPath(startX, startY, endX, endY)
}
