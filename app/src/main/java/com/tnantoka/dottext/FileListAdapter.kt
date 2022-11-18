package com.tnantoka.dottext

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListAdapter(
    private val data: List<File>,
    private val parent: File,
    private val onClick: (File) -> Unit,
    private val onLongClick: (File) -> Unit,
) : RecyclerView.Adapter<FileListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        return FileListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.file_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FileListViewHolder, position: Int) {
        val file = data[position]

        holder.titleText.text = if (file == parent) {
            ".."
        } else {
            file.name
        }

        holder.iconImage.setImageResource(
            if (file.isDirectory) {
                R.drawable.ic_outline_folder_24
            } else {
                R.drawable.ic_outline_insert_drive_file_24
            }
        )

        holder.itemView.setOnClickListener {
            onClick(file)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(file)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
