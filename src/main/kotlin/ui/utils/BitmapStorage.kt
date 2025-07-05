package ui.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.lang.ref.WeakReference
import java.nio.file.Files
import java.nio.file.Paths

object BitmapStorage {
    private val _bitmaps = mutableMapOf<String, WeakReference<ImageBitmap>>()

    fun getBitmap(path: String): ImageBitmap {
        val cached = _bitmaps[path]?.get()

        if (cached != null) {
            return cached
        }

        val bitmap = loadBitmap(path)
        _bitmaps[path] = WeakReference(bitmap)
        
        return bitmap
    }

    fun clearCache() {
        _bitmaps.clear()
    }

    private fun loadBitmap(path: String): ImageBitmap {
        val path = Paths.get(path)
        if (!Files.exists(path)) {
            throw IllegalArgumentException("File does not exist: $path")
        }

        val bytes = Files.readAllBytes(path)
        val skiaImage = Image.makeFromEncoded(bytes)
        return skiaImage.toComposeImageBitmap()
    }
}

fun String.getBitmapFromStorage(): ImageBitmap {
    return BitmapStorage.getBitmap(this)
}