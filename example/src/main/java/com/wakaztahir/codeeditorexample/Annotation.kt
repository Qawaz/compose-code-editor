package com.wakaztahir.codeeditorexample

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.codeeditor.highlight.parser.ParseResult

fun List<ParseResult>.toAnnotatedString(theme: CodeTheme, source: String): AnnotatedString {
    val result = this
    return buildAnnotatedString {

        append(source)

        // Appending results
        result.forEach {
            addStyle(theme toSpanStyle it, it.offset, it.offset + it.length)
        }

        toAnnotatedString()
    }
}