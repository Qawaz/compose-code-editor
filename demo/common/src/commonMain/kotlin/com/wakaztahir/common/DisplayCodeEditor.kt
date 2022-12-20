package com.wakaztahir.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString

@Composable
fun DisplayCodeEditor() {
    val language = CodeLang.Kotlin
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
        return parseCodeAsAnnotatedString(
            parser = parser,
            theme = theme,
            lang = language,
            code = code
        )
    }

    var textFieldValue by remember { mutableStateOf(TextFieldValue(parse(code))) }

    val (lineCount, setLineCount) = remember { mutableStateOf(0) }
    val (lineHeight, setLineHeight) = remember { mutableStateOf(0f) }

    Row {
        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
            repeat(lineCount) { line ->
                Text(
                    text = (line + 1).toString(),
                    color = MaterialTheme.colors.onBackground.copy(.3f),
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
