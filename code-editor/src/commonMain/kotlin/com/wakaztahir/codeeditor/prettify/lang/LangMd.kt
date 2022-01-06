package com.wakaztahir.codeeditor.prettify.lang

import com.wakaztahir.codeeditor.prettify.parser.Prettify
import com.wakaztahir.codeeditor.prettify.parser.StylePattern
import com.wakaztahir.codeeditor.utils.new

import java.util.regex.Pattern

/**
 * Registers a language handler for markdown.
 *
 * @author Kirill Biakov (kbiakov@gmail.com)
 */
class LangMd : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = listOf("md", "markdown")
    }

    init {
        val _shortcutStylePatterns: List<StylePattern> = ArrayList()
        val _fallthroughStylePatterns: MutableList<StylePattern> = ArrayList()
        _fallthroughStylePatterns.new(
                Prettify.PR_DECLARATION,
                Pattern.compile("^#.*?[\\n\\r]")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^```[\\s\\S]*?(?:```|$)")
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}