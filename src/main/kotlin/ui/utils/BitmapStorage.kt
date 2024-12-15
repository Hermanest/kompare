package ui.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.nio.file.Files
import java.nio.file.Paths

object BitmapStorage {
    private val _bitmaps = mutableMapOf<String, ImageBitmap>()

    fun getBitmap(path: String): ImageBitmap {
        return if (_bitmaps.containsKey(path)) {
            _bitmaps[path]!!
        } else {
            val bitmap = loadBitmap(path)
            _bitmaps[path] = bitmap
            bitmap
        }
    }

    fun clearCache() {
        _bitmaps.clear()
    }

    private fun loadBitmap(path: String): ImageBitmap {
        val path = Paths.get(path)
        if (!Files.exists(path)) {
            throw IllegalArgumentException("File does not exist: $this")
        }

        val bytes = Files.readAllBytes(path)
        val skiaImage = Image.makeFromEncoded(bytes)
        return skiaImage.toComposeImageBitmap()
    }
}

fun String.getBitmapFromStorage(): ImageBitmap {
    return BitmapStorage.getBitmap(this)
}