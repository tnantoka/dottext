package com.tnantoka.dottext

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult

class CreateDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_FILE = "onCreateFile"
        const val RESULT_DIRECTORY = "onCreateDirectory"
        const val NAME = "name"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_create_dialog, null)
        val nameEdit = view.findViewById<EditText>(R.id.nameEdit)
        val fileRadio = view.findViewById<RadioButton>(R.id.fileRadio)

        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(R.string.create)
            setView(view)
            setPositiveButton(android.R.string.ok) { dialog, which ->
                Log.d("hoge", "ok ${nameEdit.text}")

                setFragmentResult(
                    if (fileRadio.isChecked) {
                        RESULT_FILE
                    } else {
                        RESULT_DIRECTORY
                    },
                    bundleOf(NAME to nameEdit.text.toString())
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->
                Log.d("hoge", "cancel ${nameEdit.text}")
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            nameEdit.requestFocus()
        }

        nameEdit.addTextChangedListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !it.isNullOrBlank()
        }

        return dialog
    }
}