package com.wakaztahir.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString
import kotlin.math.roundToInt

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
    var lineTops by remember { mutableStateOf(emptyArray<Float>()) }
    val density = LocalDensity.current

    Row {
        if (lineTops.isNotEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                lineTops.forEachIndexed { index, top ->
                    Text(
                        modifier = Modifier.offset(y = with(density) { top.toDp() }),
                        text = index.toString(),
                        color = MaterialTheme.colors.onBackground.copy(.3f)
                    )
                }
            }
        }
        BasicTextField(
            modifier = Modifier.fillMaxSize(),
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it.copy(annotatedString = parse(it.text))
            },
            onTextLayout = { result ->
                lineTops = Array(result.lineCount) { result.getLineTop(it) }
            }
        )
    }
}