package com.tnantoka.dottext

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach

private val INSET_TYPES =
    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()

/**
 * Reserves room for the system bars as padding, needed since edge to edge became
 * mandatory in targetSdk 35.
 *
 * AppCompat's decor ActionBar is pushed down by the status bar inset, but
 * android.R.id.content is only offset by actionBarSize, so the ActionBar ends up
 * covering the top of the content. The gap is exactly the top inset, so adding it as
 * padding lines the content back up.
 *
 * The root insets are used instead of the dispatched ones because ActionBarOverlayLayout
 * consumes the top inset and children only ever see 0 (measured: dispatched top=0,
 * root top=136).
 */
fun View.applySystemBarsPadding(applyTop: Boolean = true, applyBottom: Boolean = true) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom

    val updatePadding = { view: View, windowInsets: WindowInsetsCompat ->
        val insets = windowInsets.getInsets(INSET_TYPES)
        view.setPadding(
            initialLeft + insets.left,
            initialTop + if (applyTop) insets.top else 0,
            initialRight + insets.right,
            initialBottom + if (applyBottom) insets.bottom else 0
        )
    }

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        updatePadding(view, ViewCompat.getRootWindowInsets(view) ?: windowInsets)
        windowInsets
    }

    // A fragment added through a transaction attaches after the insets have already been
    // dispatched, and requestApplyInsets does not redeliver unchanged insets, so the
    // listener above would never run for it. Read the insets directly once attached.
    doOnAttach { view ->
        ViewCompat.getRootWindowInsets(view)?.let { updatePadding(view, it) }
    }
}
