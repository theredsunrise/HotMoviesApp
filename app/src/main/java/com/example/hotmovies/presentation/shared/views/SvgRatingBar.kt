package com.example.hotmovies.presentation.shared.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatRatingBar
import com.example.hotmovies.shared.px

class SvgRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.ratingBarStyle
) : AppCompatRatingBar(context, attrs, defStyleAttr) {

    companion object {
        private val SPACING = 3.px.toFloat()
    }

    private val spacingRatingDelta: Float?
    private var mSampleTile: Bitmap? = null

    init {
        val notCorrectedRatingValue = rating
        val drawable = tileify(progressDrawable, false) as LayerDrawable

        spacingRatingDelta = SPACING / requireNotNull(mSampleTile).width.toFloat()
        rating = notCorrectedRatingValue
        progressDrawable = drawable
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    private fun tileify(drawable: Drawable, clip: Boolean): Drawable {
        if (drawable is DrawableWrapper) {
            var inner: Drawable? = drawable.drawable
            if (inner != null) {
                inner = tileify(inner, clip)
                drawable.drawable = inner
            }
        } else if (drawable is LayerDrawable) {
            val numberOfLayers = drawable.numberOfLayers
            val outDrawables = arrayOfNulls<Drawable>(numberOfLayers)

            for (i in 0 until numberOfLayers) {
                val id = drawable.getId(i)
                outDrawables[i] = tileify(
                    drawable.getDrawable(i),
                    id == android.R.id.progress || id == android.R.id.secondaryProgress
                )
            }

            val newBg = LayerDrawable(outDrawables)

            for (i in 0 until numberOfLayers) {
                newBg.setId(i, drawable.getId(i))
            }

            return newBg

        } else if (drawable is BitmapDrawable) {
            val tileBitmap = drawable.bitmap
            if (mSampleTile == null) {
                mSampleTile = tileBitmap
            }

            val shapeDrawable = ShapeDrawable(createShapeDrawable())
            val bitmapShader = BitmapShader(
                tileBitmap,
                Shader.TileMode.REPEAT, Shader.TileMode.CLAMP
            )
            shapeDrawable.paint.shader = bitmapShader
            shapeDrawable.paint.colorFilter = drawable.paint.colorFilter
            return if (clip)
                ClipDrawable(
                    shapeDrawable, Gravity.LEFT,
                    ClipDrawable.HORIZONTAL
                )
            else
                shapeDrawable
        } else {
            return tileify(getBitmapDrawableFromVectorDrawable(drawable), clip)
        }

        return drawable
    }

    private fun createShapeDrawable(): Shape {
        val roundedCorners = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        return RoundRectShape(roundedCorners, null, null)
    }

    private fun getBitmapDrawableFromVectorDrawable(drawable: Drawable): BitmapDrawable {
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(
            intrinsicWidth + SPACING.toInt(), //dp between svg images  //* resources.displayMetrics.density
            intrinsicHeight + 0.px,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    override fun setRating(rating: Float) {
        super.setRating(rating - (spacingRatingDelta ?: 0f))
    }

    override fun getRating(): Float {
        return super.getRating() + (spacingRatingDelta ?: 0f)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mSampleTile?.also {
            val width = it.width * numStars
            setMeasuredDimension(
                resolveSizeAndState(width, widthMeasureSpec, 0),
                measuredHeight
            )
        }
    }
}