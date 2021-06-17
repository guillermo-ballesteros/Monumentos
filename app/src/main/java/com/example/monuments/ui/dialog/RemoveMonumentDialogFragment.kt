package com.example.monuments.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.monuments.R

class RemoveMonumentDialogFragment: DialogFragment() {

    interface RemoveMonumentListener {
        fun remove(id: String)
    }

    private val idMonument by lazy { arguments?.getString(KEY_ID_MONUMENT) ?: "unknown" }


    companion object {
        private const val KEY_ID_MONUMENT = "key_index"
        fun newInstance(index: String): RemoveMonumentDialogFragment {
            val dialogInstance = RemoveMonumentDialogFragment()
            val auxBundle = Bundle().apply {
                putString(KEY_ID_MONUMENT, index)
            }
            dialogInstance.arguments = auxBundle
            return dialogInstance
        }
    }

    private fun getListener(): RemoveMonumentListener? {
        return when {
            targetFragment is RemoveMonumentListener -> targetFragment as RemoveMonumentListener
            parentFragment is RemoveMonumentListener -> parentFragment as RemoveMonumentListener
            activity is RemoveMonumentListener -> activity as RemoveMonumentListener
            else -> null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog()
    }

    private fun createDialog(): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_remove_monument, null)

        val cancelBtn: Button = view.findViewById(R.id.remove_dialog__btn__cancel)

        cancelBtn.stateListAnimator = null
        val removeMonumentBtn: Button = view.findViewById(R.id.remove_dialog__btn__remove)
        removeMonumentBtn.stateListAnimator = null

        builder.setView(view)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        removeMonumentBtn.setOnClickListener {
            val listener = getListener()
            listener?.remove(idMonument)
            dismiss()
        }

        return builder.create()
    }

}