package com.wakaztahir.codeeditor.theme

import androidx.compose.ui.text.SpanStyle
import com.wakaztahir.codeeditor.parser.ParseResult

abstract class CodeTheme {

    abstract val colors: SyntaxColors

    open infix fun toSpanStyle(result: ParseResult): SpanStyle {

        val colorMap = hashMapOf(
            "typ" to colors.type,
            "kwd" to colors.keyword,
            "lit" to colors.literal,
            "com" to colors.comment,
            "str" to colors.string,
            "pun" to colors.punctuation,
            "pln" to colors.plain,
            "tag" to colors.tag,
            "dec" to colors.declaration,
            "src" to colors.source,
            "atn" to colors.attrName,
            "atv" to colors.attrValue,
            "nocode" to colors.nocode
        )

        return SpanStyle(color = colorMap[result.styleKeysString] ?: colors.nocode)
    }
}

enum class CodeThemeType {
    Default {
        override fun theme(): CodeTheme {
            return DefaultTheme()
        }
    },
    Monokai {
        override fun theme(): CodeTheme {
            return MonokaiTheme()
        }
    };

    abstract fun theme(): CodeTheme
}