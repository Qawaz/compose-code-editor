package com.wakaztahir.codeeditorexample

import androidx.compose.ui.graphics.Color

class DefaultTheme : CodeTheme() {
    override val colors = SyntaxColors(
        type = Color(0xFF859900),
        keyword = Color(0xFF268BD2),
        literal = Color(0xFF269186),
        comment = Color(0xFF93A1A1),
        string = Color(0xFF269186),
        punctuation = Color(0xFF586E75),
        plain = Color(0xFF586E75),
        tag = Color(0xFF859900),
        declaration = Color(0xFF268BD2),
        source = Color(0xFF586E75),
        attrName = Color(0xFF268BD2),
        attrValue = Color(0xFF269186),
        nocode = Color(0xFF586E75),
    )
}