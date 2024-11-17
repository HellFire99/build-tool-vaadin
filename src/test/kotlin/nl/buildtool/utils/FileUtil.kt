package nl.buildtool.utils

import java.io.File

object FileUtil {
    fun getFile(fileName: String): File {
        val fileResource = this::class.java.getResource("/$fileName")!!
        return File(fileResource.path)
    }
}