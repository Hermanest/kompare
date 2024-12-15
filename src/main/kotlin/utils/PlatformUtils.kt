package utils

import java.io.File

fun getFileSize(path: String): String {
    val file = File(path)
    val sizeInKB = file.length() / 1024
    return if (sizeInKB < 1024) {
        "$sizeInKB KB"
    } else {
        "${sizeInKB / 1024} MB"
    }
}

fun deleteFile(path: String) {
    val file = File(path)
    if (file.exists()) {
        file.delete()
    }
}

fun openInFinder(path: String) {
    val file = File(path)
    if (!file.exists()) {
        println("File does not exist: $path")
        return
    }

    try {
        when {
            System.getProperty("os.name").contains("Mac", ignoreCase = true) -> {
                // macOS: Highlight the file in Finder using `open -R`
                Runtime.getRuntime().exec(arrayOf("open", "-R", file.absolutePath))
            }

            System.getProperty("os.name").contains("Windows", ignoreCase = true) -> {
                // Windows: Highlight the file in Explorer using `explorer.exe /select,`
                Runtime.getRuntime().exec(arrayOf("explorer.exe", "/select,", file.absolutePath))
            }

            else -> {
                // Linux: Open the parent directory in the default file manager
                Runtime.getRuntime().exec(arrayOf("xdg-open", file.parentFile.absolutePath))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Failed to open file: $path")
    }
}