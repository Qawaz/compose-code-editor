import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wakaztahir.common.prettify.PrettifyParser
import com.wakaztahir.common.theme.CodeThemeType
import com.wakaztahir.common.utils.parseCodeAsAnnotatedString


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        DesktopMaterialTheme {
            val code = """
                function main(){
                    print("Hello World");
                }
            """.trimIndent()

            val annotatedString = parseCodeAsAnnotatedString(PrettifyParser(), CodeThemeType.Monokai.theme(),"js",code)

            Text(text = annotatedString)
        }
    }
}