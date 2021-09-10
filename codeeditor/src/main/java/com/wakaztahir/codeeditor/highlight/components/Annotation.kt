package com.wakaztahir.codeeditor.highlight.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.codeeditor.highlight.parser.ParseResult
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeTheme
import com.wakaztahir.codeeditor.highlight.theme.CodeThemeType
import com.wakaztahir.codeeditor.highlight.theme.DefaultTheme
import com.wakaztahir.codeeditor.highlight.theme.MonokaiTheme

fun List<ParseResult>.toAnnotatedString(theme: CodeTheme, source: String): AnnotatedString {
    val result = this
    return buildAnnotatedString {

        append(source)

        // Appending Span Styles
        result.forEach {
            addStyle(theme toSpanStyle it, it.offset, it.offset + it.length)
        }

        toAnnotatedString()
    }
}


fun parseCodeAsAnnotatedString(
    parser: PrettifyParser,
    theme: CodeTheme,
    lang: String,
    code: String
): AnnotatedString {
    return parser.parse(lang, code).toAnnotatedString(theme, code)
}