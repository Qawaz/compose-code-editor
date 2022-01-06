package com.wakaztahir.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString

@Composable
fun DisplayCodeEditor() {
    val language = CodeLang.HTML
    val code = """             
    package com.wakaztahir.codeeditor
    
    public static void main(String[] args){
        System.out.println("Hello World")
    }
    """.trimIndent()

    val parser = remember { PrettifyParser() }
    val themeState by remember { mutableStateOf(CodeThemeType.Default) }
    val theme = remember(themeState) { themeState.theme() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                annotatedString = parseCodeAsAnnotatedString(
                    parser = parser,
                    theme = theme,
                    lang = language,
                    code = code
                )
            )
        )
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxSize(),
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it.copy(
                annotatedString = parseCodeAsAnnotatedString(
                    parser = parser,
                    theme = theme,
                    lang = language,
                    code = it.text
                )
            )
        }
    )
}