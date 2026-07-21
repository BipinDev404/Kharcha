package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ShareUtils {
    fun shareAppApk(context: Context) {
        try {
            val app = context.applicationInfo
            val originalApk = File(app.sourceDir)
            val cachePath = File(context.cacheDir, "apks")
            cachePath.mkdirs()
            val copiedApk = File(cachePath, "Kharcha.apk")
            
            FileInputStream(originalApk).use { input ->
                FileOutputStream(copiedApk).use { output ->
                    input.copyTo(output)
                }
            }
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                copiedApk
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(intent, "Share Kharcha App"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
