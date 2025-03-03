package com.example.hotmovies.presentation.shared.helpers

import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Cancel
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.None
import com.example.hotmovies.shared.checkMainThread
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentExitDialogHandler(
    private val owner: String,
    @StringRes private val titleId: Int,
    private val message: String,
    @StringRes private val confirmButtonTitleId: Int = android.R.string.ok
) : OnBackPressedCallback(false), DefaultLifecycleObserver {

    private val _state: MutableSharedFlow<Unit> =
        MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)
    val state: Flow<Unit> = _state.asSharedFlow()

    private val dialogHandler = DialogFragmentHandler(owner)
    private var fragment: Fragment? = null

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        (owner as Fragment).requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                owner,
                this@FragmentExitDialogHandler
            )

        val viewLifecycleOwner = owner.viewLifecycleOwner
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dialogHandler.state.collect { action ->
                    checkMainThread()
                    when (action) {
                        is Accept -> _state.tryEmit(Unit)
                        is Cancel, is None -> {}
                    }
                }
            }
        }

        owner.lifecycle.addObserver(dialogHandler)
        fragment = owner
        isEnabled = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        (owner as Fragment).lifecycle.removeObserver(dialogHandler)
        fragment = null
        isEnabled = false
        this.remove()
    }

    override fun handleOnBackPressed() {
        val fragment = fragment ?: return
        if (!fragment.viewLifecycleOwner.lifecycle.currentState.isAtLeast(STARTED)) return
        dialogHandler.showDialog(
            fragment.findNavController(),
            titleId,
            message,
            confirmButtonTitleId
        )
    }
}