package io.sketchware.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipUtils {
    suspend fun addFolderToZip(
        folder: File,
        destination: File,
    ) = withContext(Dispatchers.IO) {
        var out: ZipOutputStream? = null
        try {
            out = ZipOutputStream(
                BufferedOutputStream(FileOutputStream(destination))
            )
            addZipEntriesRecursively(folder, folder.absolutePath, out)
        } catch (e: Exception) {
            throw e
        } finally {
            out?.close()
        }
    }


    private suspend fun addZipEntriesRecursively(
        folder: File,
        basePath: String,
        out: ZipOutputStream
    ): Unit = withContext(Dispatchers.IO) {

        val files = folder.listFiles() ?: return@withContext
        for (file in files) {

            if (file.isDirectory) {
                addZipEntriesRecursively(file, basePath, out)
            } else {
                val origin = BufferedInputStream(FileInputStream(file))
                origin.use {
                    val entryName = file.path.substring(basePath.length)
                    out.putNextEntry(ZipEntry(entryName))
                    origin.copyTo(out, 1024)
                }
            }

        }

    }
}