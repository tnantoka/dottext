package com.tnantoka.dottext

import android.webkit.MimeTypeMap
import java.io.File

fun File.mimeType(): String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
}
