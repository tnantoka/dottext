package com.tnantoka.dottext

import android.webkit.MimeTypeMap
import java.io.File

fun File.mimeType(): String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
}

fun File.isMarkdown(): Boolean {
    return mimeType() == "text/markdown"
}

fun File.isText(): Boolean {
    return mimeType().startsWith("text/")
}
