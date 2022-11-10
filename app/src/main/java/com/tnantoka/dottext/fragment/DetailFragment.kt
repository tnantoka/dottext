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
import com.tnantoka.dottext.*
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.io.File

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private var file: File? = null

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

        val contentEdit = view.findViewById<EditText>(R.id.contentEdit)
        val previewWeb = view.findViewById<WebView>(R.id.previewWeb)
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
}