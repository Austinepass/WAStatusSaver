package codes.umair.wastatussaver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import codes.umair.wastatussaver.adapters.StatusAdapter
import umairayub.madialog.MaDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*
import kotlin.collections.ArrayList


class Utils() {
    private val DIRECTORY_TO_SAVE_MEDIA_NOW = "/Status Saver/"

    fun getListFiles(parentDir: File): ArrayList<File> {
        val inFiles = ArrayList<File>()
        val files: Array<File>? = parentDir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.name.endsWith(".mp4")) {
                    if (!inFiles.contains(file)) {
                        inFiles.add(file)
                    }
                } else if (file.name.endsWith(".jpg")) {
                    if (!inFiles.contains(file)) {
                        inFiles.add(file)
                    }
                }


            }
        }
        return sortList(inFiles)
    }

    fun downloadMediaItem(ctx: Context, sourceFile: File) {
            val path =
                Environment.getExternalStorageDirectory().toString() + DIRECTORY_TO_SAVE_MEDIA_NOW
            val dstfile = File(path, sourceFile.name)
            Runnable {
                try {
                    copyFile(sourceFile, dstfile)
                    Toast.makeText(ctx, "saved!", Toast.LENGTH_SHORT).show()
                    scanFile(ctx,dstfile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Download", "onClick: Error:" + e.message)
                    Toast.makeText(ctx, "unable to save\n" + e.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }.run()
    }

    fun scanFile(ctx: Context, file: File){
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        ctx.sendBroadcast(mediaScanIntent)
    }
    fun shareMediaItem(ctx: Context, uris: ArrayList<Uri>) {
            try {
                val fileDirPath =
                    File(Environment.getExternalStorageDirectory(), "/WhatsApp/Media/.Statuses/")
                fileDirPath.mkdirs()

                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.type = "*/*"
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                ctx.startActivity(Intent.createChooser(intent, "Share via "))
            } catch (e: IOException) {
                throw RuntimeException("Error generating file", e)
            }

        }

    fun deleteMediaItem(ctx: Context, sourceFile: File){
            Runnable {
                try {
                    if (sourceFile.exists()) {
                        MaDialog.Builder(ctx)
                            .setTitle("Delete?")
                            .setTitleTextColor(Color.RED)
                            .setMessage("Are you sure you want to delete this file?")
                            .setPositiveButtonText("Yes")
                            .setNegativeButtonText("Cancel")
                            .setPositiveButtonListener {
                                sourceFile.delete()
                                scanFile(ctx,sourceFile)
                                if (!sourceFile.exists()) {
                                    Toast.makeText(
                                        ctx,
                                        "File deleted!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    (ctx as Activity).finish()
                                }
                            }
                            .setNegativeButtonListener {}
                            .build()


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Delete", "onClick: Error:" + e.message)
                    Toast.makeText(ctx, "unable to delete", Toast.LENGTH_SHORT).show()
                }
            }.run()
    }

    fun getFileUri(ctx: Context,file: File): Uri{
        return FileProvider.getUriForFile(
            ctx,
            "codes.umair.wastatussaver.fileprovider",
            file)
    }
    /**
     * copy file to destination.
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }




    private fun sortList(list: ArrayList<File>): ArrayList<File> {
        Collections.sort(list, FileDateComparator())
        return list
    }
    class FileDateComparator : Comparator<File> {
        override fun compare(file: File, file1: File): Int {
            return file1.lastModified().compareTo(file.lastModified())
        }
    }


}