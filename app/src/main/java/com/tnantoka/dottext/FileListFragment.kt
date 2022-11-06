package com.tnantoka.dottext

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListFragment : Fragment(R.layout.fragment_file_list) {
    private lateinit var rootDir: File
    private lateinit var currentDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(0, 0, 0, R.string.settings)
                    .setIcon(R.drawable.ic_baseline_settings_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.add(0, 1, 0, R.string.settings)
                    .setIcon(R.drawable.ic_baseline_add_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val activity = activity ?: return true
                if (menuItem.itemId == android.R.id.home) {
                    updateContent(currentDir.parentFile)
                } else {
                    when (menuItem.itemId) {
                        0 -> {
                            startActivity(
                                Intent(activity, PreferencesActivity::class.java)
                            )
                        }
                        1 -> {
                            val dialog = CreateDialogFragment()
                            dialog.show(activity.supportFragmentManager, "create")
                        }
                    }
                }
                return true
            }
        })

        setFragmentResultListener(CreateDialogFragment.RESULT_FILE) { requestKey, bundle ->
            File(currentDir, bundle.getString(CreateDialogFragment.NAME)).writeText("")
            updateContent(currentDir)
        }
        setFragmentResultListener(CreateDialogFragment.RESULT_DIRECTORY) { requestKey, bundle ->
            File(currentDir, bundle.getString(CreateDialogFragment.NAME)).mkdir()
            updateContent(currentDir)
        }
    }

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

        currentDir = dir

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
                    (activity as AppCompatActivity).supportActionBar?.setTitle(file.name)
                } else {
                    startActivity(
                        Intent(activity, DetailActivity::class.java).apply {
                            putExtra("file", file)
                        }
                    )
                }
            }
        }

        (activity as AppCompatActivity).supportActionBar?.apply {
            val isRoot = dir == rootDir
            setDisplayHomeAsUpEnabled(!isRoot)
            setTitle(
                if (isRoot) {
                    getString(R.string.app_name)
                } else {
                    dir.name
                }
            )
        }
    }
}
