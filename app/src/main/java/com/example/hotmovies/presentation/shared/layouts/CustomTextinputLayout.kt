package com.example.hotmovies.presentation.shared.layouts

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.hotmovies.R
import com.google.android.material.R.attr
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = attr.textInputStyle
) : TextInputLayout(context, attrs, defStyleAttr) {

    private data class Snapshot(
        val isCursorVisible: Boolean,
        val strokeWidth: Int,
        val backgroundColor: Int,
        val textColors: ColorStateList?,
        val errorTextColors: ColorStateList?,
        val defaultHintTextColor: ColorStateList?,
        val endIconDrawable: Drawable?,
        val errorIconDrawable: Drawable?
    )

    private val colorPrimary = context.getColor(R.color.colorPrimaryInverted)
    private val transparentColorStateList =
        ColorStateList.valueOf(ContextCompat.getColor(this.context, android.R.color.transparent))
    private val finalSnapshot = Snapshot(
        false,
        0,
        colorPrimary,
        transparentColorStateList,
        transparentColorStateList,
        transparentColorStateList,
        null,
        null
    )

    private var snapshot: Snapshot? = null

    var corner: Float
        get() {
            return boxCornerRadiusTopEnd
        }
        set(value) {
            this.setBoxCornerRadii(value, value, value, value)
        }

    var floatProgress: Float
        get() = 0f
        set(value) {
            if (value == 0f) {
                bindSnapshot(snapshot)
                this.snapshot = Snapshot(
                    true,
                    boxStrokeWidth,
                    boxBackgroundColor,
                    editText?.textColors,
                    boxStrokeErrorColor,
                    defaultHintTextColor,
                    endIconDrawable?.mutate(),
                    errorIconDrawable?.mutate()
                )
            } else {
                bindSnapshot(finalSnapshot)
            }
        }

    private fun bindSnapshot(snapshot: Snapshot?) {
        snapshot ?: return
        boxStrokeWidth = snapshot.strokeWidth
        boxBackgroundColor = snapshot.backgroundColor
        editText?.isCursorVisible = snapshot.isCursorVisible
        editText?.setTextColor(snapshot.textColors)
        defaultHintTextColor = snapshot.defaultHintTextColor
        endIconDrawable = snapshot.endIconDrawable?.mutate()
        errorIconDrawable = snapshot.errorIconDrawable?.mutate()
        setErrorTextColor(snapshot.errorTextColors)
    }
}
