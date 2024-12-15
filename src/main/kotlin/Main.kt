import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.App


fun main() {
    System.setProperty("apple.awt.application.appearance", "system")
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
