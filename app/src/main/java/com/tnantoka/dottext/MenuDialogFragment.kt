package com.tnantoka.dottext

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.io.File

class MenuDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_RENAME = "RESULT_RENAME"
        const val RESULT_MOVE = "RESULT_MOVE"
        const val RESULT_DUPLICATE = "RESULT_DUPLICATE"
        const val RESULT_DELETE = "RESULT_DELETE"
        const val FILE = "file"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf(
            R.string.rename,
            R.string.move,
            R.string.duplicate,
            R.string.delete
        ).map { key -> getString(key) }.toTypedArray()
        val dialog = AlertDialog.Builder(activity).apply {
            val file = arguments?.getSerializable("file") as File
            setTitle(file.name)
            setItems(items) { dialog, which ->
                setFragmentResult(
                    when (which) {
                        0 -> RESULT_RENAME
                        1 -> RESULT_MOVE
                        2 -> RESULT_DUPLICATE
                        else -> RESULT_DELETE
                    },
                    bundleOf(FILE to file)
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
        }.create()
        return dialog
    }
}