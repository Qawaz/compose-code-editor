package com.wakaztahir.common.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.wakaztahir.common.model.CodeLang
import com.wakaztahir.common.parser.ParseResult
import com.wakaztahir.common.prettify.PrettifyParser
import com.wakaztahir.common.theme.CodeTheme

fun List<ParseResult>.toAnnotatedString(theme: CodeTheme, source: String): AnnotatedString {
    val result = this
    return buildAnnotatedString {

        append(source)

        // Appending Span Styles
        result.forEach {
            addStyle(theme toSpanStyle it, it.offset, it.offset + it.length)
        }
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