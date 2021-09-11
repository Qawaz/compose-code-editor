package com.wakaztahir.codeeditorexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.utils.parseCodeAsAnnotatedString
import com.wakaztahir.codeeditorexample.theme.CodeEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            CodeEditorTheme {

                val language = CodeLang.Java
                val code = """
                    
                    package com.wakaztahir.codeeditor
                    
                    public static void main(String[] args){
                        System.out.println("Hello World")
                    }
                    
                """.trimIndent()

                val parser = remember { PrettifyParser() }

                var themeState by remember { mutableStateOf(CodeThemeType.Monokai) }

                val theme = remember(themeState) { themeState.theme() }

                var parsedCode by remember {
                    mutableStateOf(
                        TextFieldValue(
                            parseCodeAsAnnotatedString(
                                parser = parser,
                                theme = theme,
                                lang = language,
                                code = code
                            )
                        )
                    )
                }

                Column {
                    Text(text = parsedCode.annotatedString)
                }

                TextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.background),
                    value = parsedCode,
                    onValueChange = {
                        parsedCode = it.copy(
                            parseCodeAsAnnotatedString(
                                parser = parser,
                                theme = theme,
                                lang = language,
                                code = it.text
                            )
                        )
                    }
                )
            }
        }
    }
}