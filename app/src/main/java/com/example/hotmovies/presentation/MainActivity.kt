package com.example.hotmovies.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.example.hotmovies.R
import com.example.hotmovies.databinding.ActivityMainBinding

interface UserInteractionConfigurableComponent {
    var isEnabled: Boolean
}

class MainActivity : AppCompatActivity(), UserInteractionConfigurableComponent {
    private lateinit var binding: ActivityMainBinding

    override var isEnabled: Boolean = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnabled) super.dispatchTouchEvent(ev) else true
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (isEnabled) super.dispatchKeyEvent(event) else true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp() || super.onNavigateUp()
    }

}
