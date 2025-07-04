package utils

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
