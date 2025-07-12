package com.tnantoka.dottext.fragment

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.tnantoka.dottext.R

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT > 34) {
            val recyclerView =
                view.findViewById<RecyclerView>(androidx.preference.R.id.recycler_view)
            recyclerView?.let { rv: RecyclerView ->
                val typedValue = android.util.TypedValue()
                val actionBarHeight = if (requireActivity().theme.resolveAttribute(
                        android.R.attr.actionBarSize,
                        typedValue,
                        true
                    )
                ) {
                    android.util.TypedValue.complexToDimensionPixelSize(
                        typedValue.data,
                        resources.displayMetrics
                    )
                } else {
                    (56 * resources.displayMetrics.density).toInt()
                }

                rv.setPadding(
                    rv.paddingLeft,
                    rv.paddingTop + actionBarHeight,
                    rv.paddingRight,
                    rv.paddingBottom
                )
            }
        }
    }
}