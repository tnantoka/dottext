package com.tnantoka.dottext.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.tnantoka.dottext.Constants
import com.tnantoka.dottext.R
import java.io.File

class MenuDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_RENAME = "MENU_RESULT_RENAME"
        const val RESULT_MOVE = "MENU_RESULT_MOVE"
        const val RESULT_DUPLICATE = "MENU_RESULT_DUPLICATE"
        const val RESULT_DELETE = "MENU_RESULT_DELETE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf(
            R.string.rename,
            R.string.move,
            R.string.duplicate,
            R.string.delete
        ).map { key -> getString(key) }.toTypedArray()
        val dialog = AlertDialog.Builder(activity).apply {
            val file = arguments?.getSerializable(Constants.FILE) as File
            setTitle(file.name)
            setItems(items) { dialog, which ->
                setFragmentResult(
                    when (which) {
                        0 -> RESULT_RENAME
                        1 -> RESULT_MOVE
                        2 -> RESULT_DUPLICATE
                        else -> RESULT_DELETE
                    },
                    bundleOf(Constants.FILE to file)
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
        }.create()
        return dialog
    }
}