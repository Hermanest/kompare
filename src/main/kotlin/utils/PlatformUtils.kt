package utils

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

fun getFileSize(path: String): String {
    val file = File(path)
    val sizeInKB = file.length() / 1024.0
    
    return if (sizeInKB < 1024) {
        String.format("%.2f KB", sizeInKB)
    } else {
        String.format("%.2f MB", sizeInKB / 1024.0)
    }
}

fun deleteFile(path: String) {
    val file = File(path)
    if (file.exists()) {
        file.delete()
    }
}

fun showInExplorer(path: String) {
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

fun openFilePicker(directory: Boolean): String? {
    val frame = Frame().apply { isVisible = false }
    val fileDialog = FileDialog(
        frame,
        "Select a ${if (directory) "Folder" else "File"}",
        if (directory) FileDialog.LOAD else FileDialog.LOAD
    )

    if (System.getProperty("os.name") != "Mac OS X") {
        throw NotImplementedError("File picker is not implemented for your platform")
    }

    System.setProperty("apple.awt.fileDialogForDirectories", directory.toString())

    fileDialog.isVisible = true
    val selectedPath = fileDialog.file?.let { fileDialog.directory + it }

    System.clearProperty("apple.awt.fileDialogForDirectories")

    frame.dispose()

    return selectedPath
}
