package com.example.hotmovies.presentation.movies.list.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration(spacing: Int) : ItemDecoration() {
    private val halfSpacing: Int = spacing / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.paddingLeft != halfSpacing) {
            parent.setPadding(halfSpacing, halfSpacing, halfSpacing, halfSpacing)
            parent.setClipToPadding(false)
        }
        outRect.top = halfSpacing
        outRect.bottom = halfSpacing
        outRect.left = halfSpacing
        outRect.right = halfSpacing
    }
}