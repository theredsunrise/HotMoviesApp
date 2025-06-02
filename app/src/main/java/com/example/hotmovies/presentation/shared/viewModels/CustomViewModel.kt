package com.example.hotmovies.presentation.shared.viewModels

import androidx.lifecycle.ViewModel

open class CustomViewModel() : ViewModel() {
    var singleUseCreatedFlag: Boolean = true
        get() {
            val prevValue = field
            field = false
            return prevValue
        }
}