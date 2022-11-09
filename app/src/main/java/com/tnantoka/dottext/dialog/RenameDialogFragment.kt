package com.tnantoka.dottext.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.tnantoka.dottext.Constants
import com.tnantoka.dottext.R
import java.io.File

class RenameDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_RENAME = "RENAME_RESULT_RENAME"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_rename_dialog, null)
        val nameEdit = view.findViewById<EditText>(R.id.nameEdit)
        val file = arguments?.getSerializable(Constants.FILE) as File

        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(R.string.rename)
            setView(view)
            setPositiveButton(android.R.string.ok) { dialog, which ->
                setFragmentResult(
                    RESULT_RENAME,
                    bundleOf(Constants.NAME to nameEdit.text.toString(), Constants.FILE to file)
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            nameEdit.setText(file.name)
            nameEdit.requestFocus()
        }

        nameEdit.addTextChangedListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                !it.isNullOrBlank() && file.name != it.toString() && !File(
                    file.parent,
                    it.toString()
                ).exists()
        }

        return dialog
    }
}