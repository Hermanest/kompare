import nu.pattern.OpenCV
import org.opencv.core.Core

fun applyPlatformTweaks() {
    val name = System.getProperty("os.name")
    when (name) {
        "Mac OS X" -> {
            System.setProperty("apple.awt.application.appearance", "system")
        }
    }
}

fun loadPlatformDependencies() {
    OpenCV.loadShared()
    //System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
}