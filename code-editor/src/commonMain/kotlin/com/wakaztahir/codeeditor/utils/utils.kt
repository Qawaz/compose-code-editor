package com.wakaztahir.codeeditor.utils

import com.wakaztahir.codeeditor.prettify.parser.StylePattern

fun MutableList<StylePattern>.new(
    tokenStyle: String,
    regExp: Regex,
    shortcutChars: String? = null,
    unknownThing: String? = null,
) {
    this.add(
        StylePattern(
            tokenStyle = tokenStyle,
            regExp = regExp,
            shortcutChars = shortcutChars,
            unknownThing = unknownThing
        )
    )
}
