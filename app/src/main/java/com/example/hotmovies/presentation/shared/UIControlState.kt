package com.example.hotmovies.presentation.shared

data class UIControlState(val isEmpty: Boolean, val isEnabled: Boolean, val exception: Exception?) {
    companion object {
        fun enabled() = UIControlState(false, true, null)
    }
}
