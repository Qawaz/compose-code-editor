/**
 * @license Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wakaztahir.codeeditor.prettify.lang

import com.wakaztahir.codeeditor.prettify.parser.Prettify
import com.wakaztahir.codeeditor.prettify.parser.StylePattern
import com.wakaztahir.codeeditor.utils.new

import java.util.regex.Pattern

/**
 * This is similar to the lang-dart.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 *
 *
 * Registers a language handler for Dart.
 *
 *
 * Loosely structured based on the DartLexer in Pygments: http://pygments.org/.
 *
 * To use, include prettify.js and this file in your HTML page.
 * Then put your code in an HTML tag like
 * <pre class="prettyprint lang-dart">(Dart code)</pre>
 *
 * @author armstrong.timothy@gmail.com
 */
class LangDart : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = listOf(("dart"))
    }

    init {
        val _shortcutStylePatterns: MutableList<StylePattern> = ArrayList()
        val _fallthroughStylePatterns: MutableList<StylePattern> = ArrayList()

        // Whitespace.
        _shortcutStylePatterns.new(
                Prettify.PR_PLAIN, Pattern.compile("^[\t\n\r \\xA0]+"), null, "\t\n\r " + 0xA0.toChar().toString()
            )

        // Script tag.
        _fallthroughStylePatterns.new(Prettify.PR_COMMENT, Pattern.compile("^#!(?:.*)"))
        // `import`, `library`, `part of`, `part`, `as`, `show`, and `hide`
        // keywords.
        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD,
                Pattern.compile("^\\b(?:import|library|part of|part|as|show|hide)\\b", Pattern.CASE_INSENSITIVE)
            )
        // Single-line comments.
        _fallthroughStylePatterns.new(
                Prettify.PR_COMMENT,
                Pattern.compile("^\\/\\/(?:.*)")
        )
        // Multiline comments.
        _fallthroughStylePatterns.new(
                Prettify.PR_COMMENT,
                Pattern.compile("^\\/\\*[^*]*\\*+(?:[^\\/*][^*]*\\*+)*\\/")
        )
        // `class` and `interface` keywords.
        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD,
                Pattern.compile("^\\b(?:class|interface)\\b", Pattern.CASE_INSENSITIVE)
        )
        // General keywords.
        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD,
                Pattern.compile(
                    "^\\b(?:assert|break|case|catch|continue|default|do|else|finally|for|if|in|is|new|return|super|switch|this|throw|try|while)\\b",
                    Pattern.CASE_INSENSITIVE
            )
        )
        // Declaration keywords.
        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD,
                Pattern.compile(
                    "^\\b(?:abstract|const|extends|factory|final|get|implements|native|operator|set|static|typedef|var)\\b",
                    Pattern.CASE_INSENSITIVE
                )
            )
        // Keywords for types.
        _fallthroughStylePatterns.new(
                Prettify.PR_TYPE,
                Pattern.compile(
                    "^\\b(?:bool|double|Dynamic|int|num|Object|String|void)\\b",
                    Pattern.CASE_INSENSITIVE
                )
            )
        // Keywords for constants.
        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD,
                Pattern.compile("\\b(?:false|null|true)\\b", Pattern.CASE_INSENSITIVE)
            )
        // Multiline strings, single- and double-quoted.
        _fallthroughStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^r?[\\']{3}[\\s|\\S]*?[^\\\\][\\']{3}")
            )

        _fallthroughStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^r?[\\\"]{3}[\\s|\\S]*?[^\\\\][\\\"]{3}")
            )

        // Normal and raw strings, single- and double-quoted.
        _fallthroughStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^r?\\'(\\'|(?:[^\\n\\r\\f])*?[^\\\\]\\')")
            )

        _fallthroughStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^r?\\\"(\\\"|(?:[^\\n\\r\\f])*?[^\\\\]\\\")")
            )
        // Identifiers.
        _fallthroughStylePatterns.new(
                Prettify.PR_PLAIN,
                Pattern.compile("^[a-z_$][a-z0-9_]*", Pattern.CASE_INSENSITIVE)
            )
        // Operators.
        _fallthroughStylePatterns.new(
                Prettify.PR_PUNCTUATION,
                Pattern.compile("^[~!%^&*+=|?:<>/-]")
            )
        // Hex numbers.
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL,
                Pattern.compile("^\\b0x[0-9a-f]+", Pattern.CASE_INSENSITIVE)
        )
        // Decimal numbers.
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL,
                Pattern.compile("^\\b\\d+(?:\\.\\d*)?(?:e[+-]?\\d+)?", Pattern.CASE_INSENSITIVE)
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL,
                Pattern.compile("^\\b\\.\\d+(?:e[+-]?\\d+)?", Pattern.CASE_INSENSITIVE)
            )
        // Punctuation.
        _fallthroughStylePatterns.new(
                Prettify.PR_PUNCTUATION,
                Pattern.compile("^[(){}\\[\\],.;]")
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}