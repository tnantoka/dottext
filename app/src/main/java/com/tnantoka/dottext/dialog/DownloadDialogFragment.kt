package com.tnantoka.dottext.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.tnantoka.dottext.Constants
import com.tnantoka.dottext.R

class DownloadDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_DOWNLOAD = "DOWNLOAD_RESULT_DOWNLOAD"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_download_dialog, null)
        val urlEdit = view.findViewById<EditText>(R.id.urlEdit)

        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(R.string.create)
            setView(view)
            setPositiveButton(android.R.string.ok) { dialog, which ->
                setFragmentResult(
                    RESULT_DOWNLOAD,
                    bundleOf(Constants.URL to urlEdit.text.toString())
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            urlEdit.requestFocus()
        }

        urlEdit.addTextChangedListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                URLUtil.isValidUrl(it.toString())
        }

        return dialog
    }
}