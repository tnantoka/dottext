package com.tnantoka.dottext

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListFragment : Fragment(R.layout.fragment_file_list) {
    private lateinit var rootDir: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = view.context
        rootDir = context.getExternalFilesDir(null) ?: return

        createExamples()
        updateContent(rootDir)
    }

    private fun createExamples() {
        val exampleText = File(rootDir, "example.txt")
        if (!exampleText.exists()) {
            exampleText.writeText("example")
        }

        val exampleDir = File(rootDir, "example.d")
        if (!exampleDir.exists()) {
            exampleDir.mkdir()
            File(exampleDir, "child.txt").writeText("child")
        }
    }

    private fun updateContent(dir: File) {
        val view = view ?: return

        val files = dir.listFiles().toList()
        val parent = dir.parentFile
        val data = if (rootDir == dir) {
            files
        } else {
            listOf(parent) + files
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.filesRecycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        recyclerView.adapter = FileListAdapter(data, parent) { file ->
            if (file.isDirectory) {
                updateContent(file)
            } else {
                if (activity?.findViewById<FrameLayout>(R.id.detailFrame) != null) {
                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.detailFrame, DetailFragment.newInstance(file))
                        .commit()
                } else {
                    startActivity(
                        Intent(activity, DetailActivity::class.java).apply {
                            putExtra("file", file)
                        }
                    )
                }
            }
        }
    }
}
