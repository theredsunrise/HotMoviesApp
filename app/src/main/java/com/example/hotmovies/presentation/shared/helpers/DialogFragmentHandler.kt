package com.example.hotmovies.presentation.shared.helpers

import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.hotmovies.R
import com.example.hotmovies.presentation.shared.fragments.DialogFragment
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class DialogFragmentHandler(private val owner: String) : DefaultLifecycleObserver {

    private val confirmKey = owner + "ConfirmKey"
    private val cancelKey = owner + "CancelKey"

    private val _state: MutableSharedFlow<DialogFragment.Actions> =
        MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)
    val state: SharedFlow<DialogFragment.Actions> = _state

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        (owner as Fragment).setFragmentResultListener(confirmKey) { _, bundle ->
            _state.tryEmit(DialogFragment.Actions.Accept(bundle.getString(DialogFragment.OWNER_KEY)!!))
        }
        owner.setFragmentResultListener(cancelKey) { _, bundle ->
            _state.tryEmit(DialogFragment.Actions.Cancel(bundle.getString(DialogFragment.OWNER_KEY)!!))
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        (owner as Fragment).clearFragmentResultListener(confirmKey)
        owner.clearFragmentResultListener(cancelKey)
    }

    fun showDialog(
        navController: NavController,
        @StringRes titleId: Int,
        message: String,
        @StringRes confirmButtonTitleId: Int = android.R.string.ok,
    ) {
        if (navController.currentDestination?.id == R.id.dialogFragment) {
            return
        }
        navController.navigate(
            R.id.dialogFragment,
            bundleOf(
                DialogFragment.CONFIRM_KEY to confirmKey,
                DialogFragment.CANCEL_KEY to cancelKey,
                DialogFragment.OWNER_KEY to owner,
                DialogFragment.TITLE_ID_KEY to titleId,
                DialogFragment.MESSAGE_KEY to message,
                DialogFragment.CONFIRM_BUTTON_ID_KEY to confirmButtonTitleId
            )
        )
    }

    fun showErrorDialog(navController: NavController, error: Throwable) {
        showDialog(
            navController,
            R.string.dialog_error_title,
            error.localizedMessage.orEmpty(),
            R.string.dialog_action_retry
        )
    }
}