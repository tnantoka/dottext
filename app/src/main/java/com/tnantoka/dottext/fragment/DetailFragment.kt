package com.tnantoka.dottext.fragment

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.tnantoka.dottext.*
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.File

class DetailFragment : Fragment(R.layout.fragment_detail) {
    enum class Mode {
        EDIT, PREVIEW, SPLIT
    }

    private var file: File? = null
    private lateinit var contentEdit: EditText
    private lateinit var previewWeb: WebView

    companion object {
        fun newInstance(file: File): DetailFragment {
            val fragment = DetailFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(Constants.FILE, file)
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        contentEdit = view.findViewById<EditText>(R.id.contentEdit)
        previewWeb = view.findViewById<WebView>(R.id.previewWeb)

        val modeGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.modeGroup)
        val editToggle = view.findViewById<MaterialButton>(R.id.editToggle)
        val previewToggle = view.findViewById<MaterialButton>(R.id.previewToggle)
        val splitToggle = view.findViewById<MaterialButton>(R.id.splitToggle)
        val countText = view.findViewById<TextView>(R.id.countText)
        val shareButton = view.findViewById<ImageButton>(R.id.shareButton)

        contentEdit.requestFocus()
        contentEdit.addTextChangedListener {
            if (isEdiable()) {
                file?.writeText(it.toString())
                countText.setText(getString(R.string.chars, it?.count() ?: 0))
            }
            if (file?.isMarkdown() ?: false) {
                val flavour = GFMFlavourDescriptor()
                val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(it.toString())
                val html = HtmlGenerator(it.toString(), parsedTree, flavour).generateHtml()
                previewWeb.loadData(html, null, null)

            } else {
                previewWeb.loadUrl(file?.toUri().toString())
            }
        }

        previewWeb.setWebViewClient(WebViewClient())
        previewWeb.settings.javaScriptEnabled = true
        previewWeb.settings.allowFileAccess = true

        editToggle.addOnCheckedChangeListener { _button, isChecked ->
            if (isChecked) {
                changeMode(Mode.EDIT)
            }
        }
        previewToggle.addOnCheckedChangeListener { _button, isChecked ->
            if (isChecked) {
                changeMode(Mode.PREVIEW)
            }
        }
        splitToggle.addOnCheckedChangeListener { _button, isChecked ->
            if (isChecked) {
                changeMode(Mode.SPLIT)
            }
        }

        shareButton.setOnClickListener {
            val file = file ?: return@setOnClickListener

            startActivity(
                Intent.createChooser(
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        type = file.mimeType()
                        if (file.isText()) {
                            putExtra(Intent.EXTRA_TEXT, file.readText())
                        } else {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "com.tnantoka.dottext.fileprovider",
                                file
                            )
                            putExtra(
                                Intent.EXTRA_STREAM,
                                uri
                            )
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            clipData = ClipData.newRawUri(null, uri)
                        }
                    }, null
                )
            )
        }

        (arguments ?: activity?.intent?.extras)?.let {
            file = it.getSerializable(Constants.FILE) as File
            contentEdit.setText(
                if (isEdiable()) {
                    file?.readText()
                } else {
                    getString(R.string.cant_edit)
                }
            )
            if (isEdiable()) {
                modeGroup.check(R.id.editToggle)
                changeMode(Mode.EDIT)
            } else {
                modeGroup.check(R.id.previewToggle)
                changeMode(Mode.PREVIEW)
            }
            contentEdit.isEnabled = isEdiable()
        }

        context?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            contentEdit.textSize = preferences.getString("font_size", "14")?.toFloatOrNull() ?: 14f
        }
    }

    private fun isEdiable(): Boolean {
        return file?.isText() ?: false
    }

    private fun changeMode(mode: Mode) {
        when (mode) {
            Mode.EDIT -> {
                contentEdit.visibility = View.VISIBLE
                previewWeb.visibility = View.GONE
            }
            Mode.PREVIEW -> {
                contentEdit.visibility = View.GONE
                previewWeb.visibility = View.VISIBLE
            }
            Mode.SPLIT -> {
                contentEdit.visibility = View.VISIBLE
                previewWeb.visibility = View.VISIBLE
            }
        }
    }
}