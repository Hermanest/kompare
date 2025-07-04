package platform

import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager

object WindowsPlatform : IPlatform {
    override fun load() { }

    override fun openFilePicker(directory: Boolean): String? {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (_: Exception) { }

        val chooser = JFileChooser().apply {
            dialogTitle = "Select a ${if (directory) "folder" else "file"}"
            fileSelectionMode = if (directory) JFileChooser.DIRECTORIES_ONLY else JFileChooser.FILES_ONLY
            isAcceptAllFileFilterUsed = true
        }

        val result = chooser.showOpenDialog(null)

        return if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    override fun showFileInExplorer(path: String) {
        val file = File(path)

        if (!file.exists()) {
            println("File does not exist: $path")
            return
        }

        try {
            Runtime.getRuntime().exec(arrayOf("explorer.exe", "/select,", file.absolutePath))
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to open file: $path")
        }
    }
}