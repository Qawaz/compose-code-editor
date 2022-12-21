import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import com.wakaztahir.common.DisplayCodeEditor
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("Chat") {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    DisplayCodeEditor()
                }
            }
        }
    }
}


