package codes.umair.wastatussaver

import java.io.File

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
        return inFiles
    }

}