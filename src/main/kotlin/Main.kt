import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import nu.pattern.OpenCV
import platform.platform
import ui.App

fun main() {
    OpenCV.loadShared()
    platform.load()
    
    application {
        Window(
            title = "Kompare",
            onCloseRequest = ::exitApplication
        ) {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    App()
                }
            }
        }
    }
}
