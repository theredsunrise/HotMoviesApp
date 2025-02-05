package com.example.hotmovies.presentation.shared.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.R.attr
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors

class CustomButton
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    private data class Snapshot(
        val strokeWidth: Int,
        val backgroundColor: ColorStateList?,
        val textColors: ColorStateList?
    )

    private val colorPrimary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary)
    private val colorStateList = ColorStateList.valueOf(colorPrimary)
    private val transparentColorStateList =
        ColorStateList.valueOf(ContextCompat.getColor(this.context, android.R.color.transparent))

    private var snapshot: Snapshot? = null
    private val finalSnapshot = Snapshot(0, colorStateList, transparentColorStateList)

    var floatProgress: Float
        get() {
            return 0f
        }
        set(value) {
            if (value == 0f) {
                bindSnapshot(snapshot)
                this.snapshot = Snapshot(
                    strokeWidth, backgroundTintList, textColors
                )
            } else {
                bindSnapshot(finalSnapshot)
            }
        }

    private fun bindSnapshot(snapshot: Snapshot?) {
        snapshot ?: return
        strokeWidth = snapshot.strokeWidth
        setTextColor(snapshot.textColors)
        backgroundTintList = snapshot.backgroundColor
    }
}