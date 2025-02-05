package com.example.hotmovies.presentation.shared.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.hotmovies.databinding.FragmentDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogFragment : DialogFragment() {
    sealed class Actions(val owner: String) {
        class None(owner: String) : Actions(owner)
        class Accept(owner: String) : Actions(owner)
        class Cancel(owner: String) : Actions(owner)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val arguments = requireArguments()
        val binding = FragmentDialogBinding.inflate(LayoutInflater.from(context)).apply {
            title.setText(arguments.getInt(TITLE_ID_KEY))
            content.text = arguments.getString(MESSAGE_KEY)
        }
        val owner = requireNotNull(arguments.getString(OWNER_KEY))
        val confirmKey = requireNotNull(arguments.getString(CONFIRM_KEY))
        val cancelKey = requireNotNull(arguments.getString(CANCEL_KEY))
        val confirmButtonTitleId = arguments.getInt(CONFIRM_BUTTON_ID_KEY)

        val dialog = MaterialAlertDialogBuilder(context).setView(binding.root).setCancelable(false)
            .setPositiveButton(confirmButtonTitleId) { _, _ ->
                setFragmentResult(confirmKey, bundleOf(OWNER_KEY to owner))

            }.setNeutralButton(android.R.string.cancel) { _, _ ->
                setFragmentResult(cancelKey, bundleOf(OWNER_KEY to owner))

            }.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    companion object {
        const val OWNER_KEY = "ownerKey"
        const val TITLE_ID_KEY = "titleKey"
        const val MESSAGE_KEY = "messageKey"
        const val CONFIRM_BUTTON_ID_KEY = "confirmButtonKey"
        const val CONFIRM_KEY = "comfirmKey"
        const val CANCEL_KEY = "cancelKey"
    }
}

