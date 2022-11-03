package com.tnantoka.dottext

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleText = view.findViewById<TextView>(R.id.titleText)
    val iconImage = view.findViewById<ImageView>(R.id.iconImage)
}
