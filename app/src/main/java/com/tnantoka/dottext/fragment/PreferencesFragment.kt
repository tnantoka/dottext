package com.tnantoka.dottext.fragment

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.tnantoka.dottext.R
import com.tnantoka.dottext.applySystemBarsPadding

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView =
            view.findViewById<RecyclerView>(androidx.preference.R.id.recycler_view)
        recyclerView?.apply {
            clipToPadding = false
            applySystemBarsPadding()
        }
    }
}
