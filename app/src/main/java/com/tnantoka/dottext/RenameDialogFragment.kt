package com.tnantoka.dottext

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import java.io.File

class RenameDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_RENAME = "RESULT_FILE"
        const val NAME = "NAME"
        const val FILE = "FILE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_rename_dialog, null)
        val nameEdit = view.findViewById<EditText>(R.id.nameEdit)
        val file = arguments?.getSerializable("file") as File

        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(R.string.rename)
            setView(view)
            setPositiveButton(android.R.string.ok) { dialog, which ->
                setFragmentResult(
                    RESULT_RENAME,
                    bundleOf(NAME to nameEdit.text.toString(), FILE to file)
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