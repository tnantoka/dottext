package com.tnantoka.dottext

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
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

        val contentEdit = view.findViewById<EditText>(R.id.contentEdit)
        val countText = view.findViewById<TextView>(R.id.countText)
        val shareButton = view.findViewById<ImageButton>(R.id.shareButton)

        contentEdit.requestFocus()
        contentEdit.addTextChangedListener {
            file?.writeText(it.toString())
            countText.setText(getString(R.string.chars, it?.count() ?: 0))
        }

        shareButton.setOnClickListener {
            Log.d("hoge", "share")
        }

        (arguments ?: activity?.intent?.extras)?.let {
            file = it.getSerializable(Constants.FILE) as File
            contentEdit.setText(file?.readText())
        }

        context?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            contentEdit.textSize = preferences.getString("font_size", "14")?.toFloatOrNull() ?: 14f
        }
    }
}