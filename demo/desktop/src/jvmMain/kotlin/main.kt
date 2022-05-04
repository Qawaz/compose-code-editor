import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wakaztahir.common.DisplayCodeEditor


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            DisplayCodeEditor()
        }
    }
}