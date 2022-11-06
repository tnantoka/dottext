package com.tnantoka.dottext

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import java.io.File

class DetailFragment : Fragment(R.layout.fragment_detail) {
    companion object {
        fun newInstance(file: File): DetailFragment {
            val fragment = DetailFragment()
            fragment.arguments = Bundle().apply {
                putSerializable("file", file)
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentEdit = view.findViewById<EditText>(R.id.contentEdit)

        (arguments ?: activity?.intent?.extras)?.let {
            val file = it.getSerializable("file") as File
            contentEdit.setText(file.readText())
        }

        context?.let {
            val preferences = PreferenceManager.getDefaultSharedPreferences(it)
            contentEdit.textSize = preferences.getString("font_size", "14")?.toFloatOrNull() ?: 14f
        }
    }
}