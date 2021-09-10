package com.wakaztahir.codeeditorexample

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.wakaztahir.codeeditor.highlight.CodeHighlighter
import com.wakaztahir.codeeditor.highlight.ColorTheme
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

            val language = "java"
            val code = """
                    public static void main(String[] args){
                        int x = 3;
                        System.out.println(x)
                    }
                """.trimIndent()

            val result = remember {
                PrettifyParser().parse(language, code)
            }

            Column {
                result.forEach {
                    Text(text = "length : ${it.length} , offset : ${it.offset} , style keys string : ${it.styleKeysString}")
                }
            }
        }
    }

}