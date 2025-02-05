package com.example.hotmovies.presentation.shared.layouts

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.roundToInt

class CustomStaggeredGridLayoutManager(
    context: Context, attrs: AttributeSet, defStyleAttr: Int,
    defStyleRes: Int
) : StaggeredGridLayoutManager(context, attrs, defStyleAttr, defStyleRes) {

    var spacing: Int = 0

    init {
        gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp?.width = ((this.width - spacing).toFloat() / spanCount.toFloat()).roundToInt()
        return super.checkLayoutParams(lp)
    }
}