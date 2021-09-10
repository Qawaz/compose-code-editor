package com.wakaztahir.codeeditorexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//            AndroidView(factory = {
//                TextView(it).apply {
//                    text = HtmlCompat.fromHtml(
//                        CodeHighlighter.highlight(
//                            "java", """
//                    public static void main(String[] args){
//                        int x = 3;
//                        System.out.println(x)
//                    }
//                """.trimIndent(), ColorTheme.MONOKAI.theme()
//                        ), HtmlCompat.FROM_HTML_MODE_COMPACT
//                    )
//                }
//            })

            val language = "markdown"
            val code = """
                    public static void main(String[] args){
                        int x = 3;
                        System.out.println(x)
                    }
                """.trimIndent()

            var result by remember {
                mutableStateOf(TextFieldValue(code))
            }

            val parser = remember { PrettifyParser() }

            val theme = remember { DefaultTheme() }

            TextField(
                modifier = Modifier.fillMaxSize(),
                value = result,
                onValueChange = {
                    result = it.copy(
                        annotatedString = parser.parse(language, it.text)
                            .toAnnotatedString(theme, it.text)
                    )
                }
            )
        }
    }

}