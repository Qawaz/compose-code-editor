package com.wakaztahir.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.prettify.lang.Lang
import com.wakaztahir.codeeditor.prettify.parser.Prettify
import com.wakaztahir.codeeditor.prettify.parser.StylePattern
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import com.wakaztahir.codeeditor.utils.toAnnotatedString

@Composable
fun DisplayCodeEditor() {
    var language by remember { mutableStateOf(CodeLang.Kotlin) }
    var customLanguage by remember { mutableStateOf<CustomLang?>(null) }

    val code = """             
    package com.wakaztahir.codeeditor
    
    fun main(){
        println("Hello World");
    }
    """.trimIndent()

    val parser = remember { PrettifyParser() }
    val themeState by remember { mutableStateOf(CodeThemeType.Default) }
    val theme = remember(themeState) { themeState.theme }

    fun parse(code: String): AnnotatedString {
        return if (customLanguage == null) {
            parseCodeAsAnnotatedString(
                parser = parser,
                theme = theme,
                lang = language,
                code = code
            )
        } else {
            parser.parse({ customLanguage!! }, code).toAnnotatedString(theme, code)
        }
    }

    var textFieldValue by remember { mutableStateOf(TextFieldValue(parse(code))) }

    val (lineCount, setLineCount) = remember { mutableStateOf(0) }
    val (lineHeight, setLineHeight) = remember { mutableStateOf(0f) }

    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            var langMenu by remember { mutableStateOf(false) }
            Column {
                Button(onClick = {
                    langMenu = true
                }) {
                    Text(text = language.name)
                }
                if (langMenu) {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        TextButton(onClick = {
                            customLanguage = CustomLang()
                            langMenu = false
                        }) {
                            Text(text = "Custom Lang")
                        }
                        CodeLang.values().forEach { lang ->
                            TextButton(onClick = {
                                customLanguage = null
                                language = lang
                                langMenu = false
                            }) {
                                Text(text = lang.name)
                            }
                        }
                    }
                }
            }
        }
        if(customLanguage != null){
            Text(text = "Custom Lang has these keywords : Google , Jetbrains , Compose , Qawaz",color = MaterialTheme.colorScheme.onBackground.copy(.5f))
        }
        Row {
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                repeat(lineCount) { line ->
                    Text(
                        text = (line + 1).toString(),
                        color = MaterialTheme.colorScheme.onBackground.copy(.3f),
                        modifier = Modifier.offset(y = (line * lineHeight).dp)
                    )
                }
            }
            BasicTextField(
                modifier = Modifier.fillMaxSize(),
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it.copy(annotatedString = parse(it.text))
                },
                onTextLayout = { result ->
                    setLineCount(result.lineCount)
                    setLineHeight(result.multiParagraph.height / result.lineCount)
                }
            )
        }
    }
}

class CustomLang() : Lang() {

    private val fileExtensions = listOf("qawaz")

    override val shortcutStylePatterns: List<StylePattern> = listOf(
        StylePattern(
            Prettify.PR_KEYWORD,
            Regex("Qawaz|CodeEditor|Compose|Jetbrains|Google")
        )
    )
    override val fallthroughStylePatterns: List<StylePattern> = listOf(
        StylePattern(
            Prettify.PR_KEYWORD,
            Regex("Qawaz|CodeEditor|Compose|Jetbrains|Google")
        )
    )

    override fun getFileExtensions(): List<String> = fileExtensions
}