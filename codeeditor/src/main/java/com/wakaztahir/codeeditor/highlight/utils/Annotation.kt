package com.wakaztahir.codeeditor.highlight.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import com.wakaztahir.codeeditor.highlight.parser.ParseResult
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser
import com.wakaztahir.codeeditor.highlight.theme.CodeTheme

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

fun parseCodeAsAnnotatedString(
    parser: PrettifyParser,
    theme: CodeTheme,
    lang: CodeLang,
    code: String
): AnnotatedString = parseCodeAsAnnotatedString(
    parser = parser,
    theme = theme,
    lang = lang.value.first(),
    code = code,
)