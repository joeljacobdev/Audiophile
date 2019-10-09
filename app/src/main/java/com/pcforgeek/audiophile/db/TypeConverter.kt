package com.pcforgeek.audiophile.db

import android.net.Uri
import androidx.room.TypeConverter

class TypeConverter {

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun stringToUri(path: String?): Uri? {
        return if (path == null) null else Uri.parse(path)
    }
}