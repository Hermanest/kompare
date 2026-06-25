package utils

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class FileInfo(
    val size: String,
    val creationDate: String
)

fun getFileInfo(path: String): FileInfo {
    val file = File(path)
    val sizeInKB = file.length() / 1024.0

    val size = if (sizeInKB < 1024) {
        String.format("%.2f KB", sizeInKB)
    } else {
        String.format("%.2f MB", sizeInKB / 1024.0)
    }

    val creationDate = try {
        val attributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)

        val fileTime = attributes.creationTime().toInstant()
        val zoneId = ZoneId.systemDefault()
        val fileDateTime = ZonedDateTime.ofInstant(fileTime, zoneId)

        val today = LocalDate.now(zoneId)
        val fileDate = fileDateTime.toLocalDate()

        if (fileDate.isEqual(today)) {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            "Today at ${fileDateTime.format(timeFormatter)}"
        } else {
            val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm")
            fileDateTime.format(dateFormatter)
        }
    } catch (e: Exception) {
        "Unknown"
    }

    return FileInfo(size, creationDate)
}

fun deleteFile(path: String) {
    val file = File(path)

    if (file.exists()) {
        file.delete()
    }
}
