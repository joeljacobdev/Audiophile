package com.pcforgeek.audiophile.util

import android.net.Uri
import android.provider.DocumentsContract


object FileUtils {
    private const val PRIMARY_VOLUME_NAME = "primary"

    fun getFullPathFromTreeUri(treeUri: Uri?): String? {
        if (treeUri == null) return null
        val docId = DocumentsContract.getTreeDocumentId(treeUri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (split.isNotEmpty()) {
            if (split[0] == PRIMARY_VOLUME_NAME) {
                "/storage/emulated/0/${split[1]}/"
            } else {
                "/storage/${split[0]}/${split[1]}/"
            }
        } else
            null
    }
}