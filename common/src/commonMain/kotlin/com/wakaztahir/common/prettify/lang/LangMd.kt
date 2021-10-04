package com.wakaztahir.common.prettify.lang

import com.wakaztahir.common.prettify.parser.Prettify
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
            get() = Arrays.asList("md", "markdown")
    }

    init {
        val _shortcutStylePatterns: List<List<Any>> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any>> = ArrayList()
        _fallthroughStylePatterns.add(
            Arrays.asList(
                Prettify.PR_DECLARATION,
                Pattern.compile("^#.*?[\\n\\r]")
            )
        )
        _fallthroughStylePatterns.add(
            Arrays.asList(
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