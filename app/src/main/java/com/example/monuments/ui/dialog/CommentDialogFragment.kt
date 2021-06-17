package com.example.monuments.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.example.monuments.R
import com.google.android.material.snackbar.Snackbar

class CommentDialogFragment: DialogFragment() {

    interface AddCommentListener {
        fun onAddBtnClicked(rate: Int, comment: String)
    }

    companion object {
        fun newInstance(): CommentDialogFragment {
            return CommentDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog()
    }

    private fun getListener(): AddCommentListener? {
        return when {
            targetFragment is AddCommentListener -> targetFragment as AddCommentListener
            parentFragment is AddCommentListener -> parentFragment as AddCommentListener
            activity is AddCommentListener -> activity as AddCommentListener
            else -> null
        }
    }

    private fun createDialog(): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val layoutInflater = requireActivity().layoutInflater
        val view: View = layoutInflater.inflate(R.layout.dialog_comment, null)
        builder.setView(view)

        val cancelBtn: Button = view.findViewById(R.id.comment_dialog__btn__cancel)

        val addCommentBtn: Button = view.findViewById(R.id.comment_dialog__btn__add_comment)

        val commentText: EditText = view.findViewById(R.id.comment_dialog__input__comment)

        val ratingBar: RatingBar = view.findViewById(R.id.comment_dialog__rating__rating_bar)

        cancelBtn.setOnClickListener {
            dismiss()
        }

        addCommentBtn.setOnClickListener {
            val listener = getListener()
            if (commentText.text.toString() != "") {
                listener?.onAddBtnClicked(ratingBar.rating.toInt(), commentText.text.toString())
                dismiss()
            } else {
                Snackbar.make(
                    view,
                    getString(R.string.error_empty_data),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        return builder.create()
    }

}