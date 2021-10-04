package com.wakaztahir.common.prettify.lang

import com.wakaztahir.common.prettify.lang.Lang
import com.wakaztahir.common.prettify.parser.Prettify
import com.wakaztahir.common.prettify.lang.LangCss.LangCssKeyword
import com.wakaztahir.common.prettify.lang.LangCss.LangCssString
import com.wakaztahir.common.prettify.lang.LangMatlab
import com.wakaztahir.common.prettify.lang.LangMatlab.LangMatlabIdentifier
import com.wakaztahir.common.prettify.lang.LangMatlab.LangMatlabOperator
import com.wakaztahir.common.prettify.lang.LangN
import com.wakaztahir.common.prettify.lang.LangWiki.LangWikiMeta
import com.wakaztahir.common.prettify.lang.LangXq
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
            get() = Arrays.asList(*arrayOf("md", "markdown"))
    }

    init {
        val _shortcutStylePatterns: List<List<Any>?> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any>?> = ArrayList()
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any>(
                    Prettify.PR_DECLARATION,
                    Pattern.compile("^#.*?[\\n\\r]")
                )
            )
        )
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any>(
                    Prettify.PR_STRING,
                    Pattern.compile("^```[\\s\\S]*?(?:```|$)")
                )
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }
}