package com.example.hotmovies.presentation.shared.helpers

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.hotmovies.R

class DrawableFactory private constructor(context: Context) {

    val cross: Drawable
    val crossInverted: Drawable

    init {
        cross = tintedDrawable(context, R.drawable.vector_cross, R.color.colorPrimary)
        crossInverted =
            tintedDrawable(context, R.drawable.vector_cross, R.color.colorPrimaryInverted)
    }

    companion object {
        @Volatile
        private var instance: DrawableFactory? = null
        fun getInstance(context: Context): DrawableFactory {
            return instance ?: synchronized(this) {
                instance ?: DrawableFactory(context)
            }
        }
    }

    private fun tintedDrawable(
        context: Context,
        @DrawableRes drawableId: Int,
        @ColorRes tintColor: Int
    ): Drawable {
        val color = ContextCompat.getColor(context, tintColor)
        val drawable: Drawable = requireNotNull(context.getDrawable(drawableId)).mutate()
        return drawable.apply {
            this.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}