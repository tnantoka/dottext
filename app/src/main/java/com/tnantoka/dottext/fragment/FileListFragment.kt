package com.tnantoka.dottext.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tnantoka.dottext.BuildConfig
import com.tnantoka.dottext.Constants
import com.tnantoka.dottext.FileListAdapter
import com.tnantoka.dottext.R
import com.tnantoka.dottext.activity.DetailActivity
import com.tnantoka.dottext.activity.PreferencesActivity
import com.tnantoka.dottext.dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class FileListFragment : Fragment(R.layout.fragment_file_list) {
    private lateinit var rootDir: File
    private lateinit var currentDir: File
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(0, 0, 2, R.string.settings)
                    .setIcon(R.drawable.ic_baseline_settings_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.add(0, 1, 0, R.string.create)
                    .setIcon(R.drawable.ic_baseline_add_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.add(0, 2, 1, R.string.download)
                    .setIcon(R.drawable.ic_baseline_download_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val activity = activity ?: return true
                if (menuItem.itemId == android.R.id.home) {
                    val parent = currentDir.parentFile ?: return true
                    updateContent(parent)
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
                        2 -> {
                            val dialog = DownloadDialogFragment()
                            dialog.show(activity.supportFragmentManager, "download")
                        }
                    }
                }
                return true
            }
        })

        onBackPressedCallback = activity?.onBackPressedDispatcher?.addCallback {
            val parent = currentDir.parentFile ?: return@addCallback
            updateContent(parent)
        }

        setFragmentResultListener(CreateDialogFragment.RESULT_FILE) { _, bundle ->
            val name = bundle.getString(Constants.NAME) ?: return@setFragmentResultListener
            File(currentDir, name).writeText("")
            updateContent(currentDir)
        }
        setFragmentResultListener(CreateDialogFragment.RESULT_DIRECTORY) { _, bundle ->
            val name = bundle.getString(Constants.NAME) ?: return@setFragmentResultListener
            File(currentDir, name).mkdir()
            updateContent(currentDir)
        }

        setFragmentResultListener(DownloadDialogFragment.RESULT_DOWNLOAD) { _, bundle ->
            val url = bundle.getString(Constants.URL)
            download(URL(url))
        }
        setFragmentResultListener(RenameDialogFragment.RESULT_RENAME) { _, bundle ->
            val file = bundle.getSerializable(Constants.FILE) as File
            val name = bundle.getString(Constants.NAME) ?: return@setFragmentResultListener
            file.renameTo(File(currentDir, name))
            updateContent(currentDir)
        }

        setFragmentResultListener(MoveDialogFragment.RESULT_MOVE) { _, bundle ->
            val file = bundle.getSerializable(Constants.FILE) as File
            val directory = bundle.getSerializable(Constants.DIRECTORY) as File
            file.renameTo(File(directory, file.name))
            updateContent(directory)
        }

        setFragmentResultListener(MenuDialogFragment.RESULT_RENAME) { _, bundle ->
            val activity = activity ?: return@setFragmentResultListener
            val file = bundle.getSerializable(Constants.FILE) as File
            RenameDialogFragment().apply {
                arguments = bundleOf(Constants.FILE to file)
                show(activity.supportFragmentManager, "rename")
            }
        }
        setFragmentResultListener(MenuDialogFragment.RESULT_MOVE) { _, bundle ->
            val activity = activity ?: return@setFragmentResultListener
            val file = bundle.getSerializable(Constants.FILE) as File
            MoveDialogFragment().apply {
                arguments = bundleOf(Constants.FILE to file, Constants.ROOT_DIRECTORY to rootDir)
                show(activity.supportFragmentManager, "move")
            }
        }
        setFragmentResultListener(MenuDialogFragment.RESULT_DUPLICATE) { _, bundle ->
            val file = bundle.getSerializable(Constants.FILE) as File
            file.copyRecursively(duplicatedFile(file))
            updateContent(currentDir)
        }
        setFragmentResultListener(MenuDialogFragment.RESULT_DELETE) { _, bundle ->
            val file = bundle.getSerializable(Constants.FILE) as File
            file.deleteRecursively()
            updateContent(currentDir)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(Constants.DIRECTORY, currentDir)
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback?.remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = view.context
        rootDir = context.getExternalFilesDir(null) ?: return

        createExamples()
        updateContent(savedInstanceState?.getSerializable(Constants.DIRECTORY) as? File ?: rootDir)
    }

    private fun createExamples() {
//        if (BuildConfig.DEBUG) {
//            File(rootDir, "example.txt").delete()
//            File(rootDir, "example.d").deleteRecursively()
//        }

        val exampleText = File(rootDir, "example.md")
        if (!exampleText.exists()) {
            exampleText.writeText("# Markdown\n\nhello world")
        }

        val exampleDir = File(rootDir, "directory")
        if (!exampleDir.exists()) {
            exampleDir.mkdir()
            File(exampleDir, "child.txt").writeText("child")
        }
    }

    private fun updateContent(dir: File) {
        val view = view ?: return

        currentDir = dir

        val files = dir.listFiles()?.toList() ?: return
        val parent = dir.parentFile ?: return
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
        recyclerView.adapter = FileListAdapter(
            data,
            parent,
            { file ->
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
                                putExtra(Constants.FILE, file)
                            }
                        )
                    }
                }
            },
            { file ->
                val activity = activity ?: return@FileListAdapter
                MenuDialogFragment().apply {
                    arguments = bundleOf(Constants.FILE to file)
                    show(activity.supportFragmentManager, "menu")
                }
            }
        )

        (activity as AppCompatActivity).supportActionBar?.apply {
            val isRoot = dir == rootDir
            setDisplayHomeAsUpEnabled(!isRoot)
            title = if (isRoot) {
                getString(R.string.app_name)
            } else {
                dir.name
            }
            onBackPressedCallback?.isEnabled = !isRoot
        }
    }

    private fun duplicatedFile(file: File): File {
        var i = 2
        while (true) {
            val name = "${file.nameWithoutExtension}_$i.${file.extension}"
            val dest = File(currentDir, name)
            if (!dest.exists()) {
                return dest
            }
            i++
        }
    }

    private fun download(url: URL) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                url.openStream().copyTo(FileOutputStream(File(currentDir, File(url.file).name)))
            }
            updateContent(currentDir)
        }
    }
}
