package com.example.hotmovies.presentation.shared.helpers

import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.hotmovies.R
import com.example.hotmovies.shared.areSystemGesturesSupportedAsync

object ToolbarConfigurator {
    suspend fun bindToolbarToFragmentAsync(toolbar: Toolbar, fragment: Fragment) {
        val appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.moviesFragment)).build()
        toolbar.setupWithNavController(fragment.findNavController(), appBarConfiguration)
        if (fragment.areSystemGesturesSupportedAsync()) {
            toolbar.navigationIcon = null
        }
        toolbar.isVisible = true
    }
}