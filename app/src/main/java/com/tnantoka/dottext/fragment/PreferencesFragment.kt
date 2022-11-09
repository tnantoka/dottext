package com.tnantoka.dottext.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.tnantoka.dottext.R

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}