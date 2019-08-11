package com.pcforgeek.audiophile.util

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream


object StorageUtils {

    suspend fun createDirectoryAndSaveFile(imageToSave: Bitmap, fileName: String): String {

        val appDirectoryName = ".audiophile"
        val appRoot = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), appDirectoryName
        )

        if (!appRoot.exists()) {
            appRoot.mkdirs()
        }

        val file = File(File(appRoot.path), "$fileName.jpeg")
        if (!file.exists())
            try {
                val out = FileOutputStream(file)
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        return file.path

    }
}