package com.example.monuments.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.monuments.databinding.DialogLogOutBinding
import com.example.monuments.databinding.DialogMonumentLocationBinding

class LogOutDialogFragment: DialogFragment() {

    private var dialogViewBinding: DialogLogOutBinding? = null

    interface LogOutListener {
        fun onLogOutBtnClicked()
    }

    companion object {
        fun newInstance(): LogOutDialogFragment {
            return LogOutDialogFragment()
        }
    }

    private fun getListener(): LogOutListener? {
        return when {
            targetFragment is LogOutListener -> targetFragment as LogOutListener
            parentFragment is LogOutListener -> parentFragment as LogOutListener
            activity is LogOutListener -> activity as LogOutListener
            else -> null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogViewBinding = DialogLogOutBinding.inflate(LayoutInflater.from(context))
        return createDialog()
    }

    private fun createDialog(): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(dialogViewBinding?.root)
        dialogViewBinding?.dialogBtnCancel?.setOnClickListener {
            dismiss()
        }

        dialogViewBinding?.dialogBtnLogOut?.setOnClickListener {
            val listener = getListener()
            listener?.onLogOutBtnClicked()
            dismiss()
        }

        return builder.create()
    }
}