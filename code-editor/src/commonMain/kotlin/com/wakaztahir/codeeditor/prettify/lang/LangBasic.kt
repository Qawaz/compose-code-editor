// Contributed by peter dot kofler at code minus cop dot org
package com.wakaztahir.codeeditor.prettify.lang

import com.wakaztahir.codeeditor.prettify.parser.Prettify
import com.wakaztahir.codeeditor.prettify.parser.StylePattern
import com.wakaztahir.codeeditor.utils.new

import java.util.regex.Pattern

/**
 * This is similar to the lang-basic.js in JavaScript Prettify.
 *
 *
 * To use, include prettify.js and this file in your HTML page.
 * Then put your code in an HTML tag like
 * <pre class="prettyprint lang-basic">(my BASIC code)</pre>
 *
 * @author peter dot kofler at code minus cop dot org
 */
class LangBasic : Lang() {

    companion object{
        val fileExtensions = listOf("basic", "cbm")
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }

    init {
        val _shortcutStylePatterns: MutableList<StylePattern> = ArrayList()
        val _fallthroughStylePatterns: MutableList<StylePattern> = ArrayList()

        // "single-line-string"
        _shortcutStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^(?:\"(?:[^\\\\\"\\r\\n]|\\\\.)*(?:\"|$))"),
                null,
                "\""
            )
        // Whitespace
        _shortcutStylePatterns.new(
                Prettify.PR_PLAIN, Pattern.compile("^\\s+"), null, "\t\n\r " + 0xA0.toChar().toString()
            )

        // A line comment that starts with REM
        _fallthroughStylePatterns.new(
                Prettify.PR_COMMENT, Pattern.compile("^REM[^\\r\\n]*"), null
            )

        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD,
                Pattern.compile("^\\b(?:AND|CLOSE|CLR|CMD|CONT|DATA|DEF ?FN|DIM|END|FOR|GET|GOSUB|GOTO|IF|INPUT|LET|LIST|LOAD|NEW|NEXT|NOT|ON|OPEN|OR|POKE|PRINT|READ|RESTORE|RETURN|RUN|SAVE|STEP|STOP|SYS|THEN|TO|VERIFY|WAIT)\\b"),
                null
            )
        _fallthroughStylePatterns.new(
                Prettify.PR_PLAIN, Pattern.compile("^[A-Z][A-Z0-9]?(?:\\$|%)?", Pattern.CASE_INSENSITIVE), null
            )
        // Literals .0, 0, 0.0 0E13
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL,
                Pattern.compile("^(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:e[+\\-]?\\d+)?", Pattern.CASE_INSENSITIVE),
                null,
                "0123456789"
            )
        _fallthroughStylePatterns.new(
                Prettify.PR_PUNCTUATION, Pattern.compile("^.[^\\s\\w\\.$%\"]*"), null
            )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }
}