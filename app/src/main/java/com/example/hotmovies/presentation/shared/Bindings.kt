package com.example.hotmovies.presentation.shared

import android.os.Build
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
    view.error = errorMessage
}

@BindingAdapter("lineSpacingMultiplier")
fun setLineSpacingMultiplier(
    view: TextView,
    value: Float
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        view.setLineSpacing(view.lineSpacingExtra, value)
    } else {
        view.setLineSpacing(0f, value)
    }
}