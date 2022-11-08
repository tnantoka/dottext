package com.tnantoka.dottext

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MoveDialogFragment : DialogFragment() {
    companion object {
        const val RESULT_MOVE = "MOVE_RESULT_MOVE"
    }

    private lateinit var rootDir: File
    private lateinit var currentDir: File
    private lateinit var file: File

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_move_dialog, null)
        file = arguments?.getSerializable(Constants.FILE) as File
        rootDir = arguments?.getSerializable(Constants.ROOT_DIRECTORY) as File

        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(getString(R.string.move_file, file.name))
            setView(view)
            setPositiveButton(R.string.move) { dialog, which ->
                setFragmentResult(
                    RESULT_MOVE,
                    bundleOf(Constants.DIRECTORY to currentDir, Constants.FILE to file)
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog, which ->
            }
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            updateContent(dialog, file.parentFile)
        }

        return dialog
    }

    private fun updateContent(dialog: AlertDialog, dir: File) {
        currentDir = dir

        val files = dir.listFiles().filter { file -> file.isDirectory }
        val parent = dir.parentFile
        val data = if (rootDir == dir) {
            files
        } else {
            listOf(parent) + files
        }

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.filesRecycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(dialog.context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recyclerView.adapter = FileListAdapter(
            data,
            parent,
            { file ->
                updateContent(dialog, file)
            },
            { file ->
            }
        )

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
            !dir.absolutePath.startsWith((file.absolutePath)) && dir != file.parentFile
    }
}