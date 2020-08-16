package codes.umair.wastatussaver

import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class Utils {
    constructor()

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