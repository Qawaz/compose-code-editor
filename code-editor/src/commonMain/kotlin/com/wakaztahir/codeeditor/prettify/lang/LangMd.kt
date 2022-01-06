package com.wakaztahir.codeeditor.prettify.lang

import com.wakaztahir.codeeditor.prettify.parser.Prettify
import java.util.*
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
        val _shortcutStylePatterns: List<List<Any>> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any>> = ArrayList()
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_DECLARATION,
                Pattern.compile("^#.*?[\\n\\r]")
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_STRING,
                Pattern.compile("^```[\\s\\S]*?(?:```|$)")
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}