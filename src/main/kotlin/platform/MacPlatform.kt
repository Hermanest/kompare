package platform

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

object MacPlatform : IPlatform {
    override fun load() {
        System.setProperty("apple.awt.application.appearance", "system")
    }

    override fun openFilePicker(directory: Boolean): String? {
        val frame = Frame().apply { isVisible = false }
        val fileDialog = FileDialog(
            frame,
            "Select a ${if (directory) "Folder" else "File"}",
            if (directory) FileDialog.LOAD else FileDialog.LOAD
        )

        System.setProperty("apple.awt.fileDialogForDirectories", directory.toString())

        fileDialog.isVisible = true
        val selectedPath = fileDialog.file?.let { fileDialog.directory + it }

        System.clearProperty("apple.awt.fileDialogForDirectories")

        frame.dispose()

        return selectedPath
    }

    override fun showFileInExplorer(path: String) {
        val file = File(path)

        if (!file.exists()) {
            println("File does not exist: $path")
            return
        }

        try {
            Runtime.getRuntime().exec(arrayOf("open", "-R", file.absolutePath))
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to open file: $path")
        }
    }
}