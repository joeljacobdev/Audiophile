package com.pcforgeek.audiophile.db

import android.net.Uri
import androidx.room.TypeConverter

class TypeConverter {

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToUri(value: String) = Uri.parse(value)
}
