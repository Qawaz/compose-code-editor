// Copyright (C) 2010 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.wakaztahir.codeeditor.prettify.lang

import com.wakaztahir.codeeditor.prettify.parser.Prettify
import com.wakaztahir.codeeditor.prettify.parser.StylePattern
import com.wakaztahir.codeeditor.utils.new

import java.util.regex.Pattern

/**
 * This is similar to the lang-scala.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 *
 *
 * Registers a language handler for Scala.
 *
 * Derived from http://lampsvn.epfl.ch/svn-repos/scala/scala-documentation/trunk/src/reference/SyntaxSummary.tex
 *
 * @author mikesamuel@gmail.com
 */
class LangScala : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = listOf("scala")
    }

    init {
        val _shortcutStylePatterns: MutableList<StylePattern> = ArrayList()
        val _fallthroughStylePatterns: MutableList<StylePattern> = ArrayList()

        // Whitespace
        _shortcutStylePatterns.new(
                Prettify.PR_PLAIN, Pattern.compile("^[\\t\\n\\r \\xA0]+"), null, "\t\n\r " + 0xA0.toChar().toString()
        )
        // A double or single quoted string 
        // or a triple double-quoted multi-line string.
        _shortcutStylePatterns.new(
                Prettify.PR_STRING,
                Pattern.compile("^(?:\"(?:(?:\"\"(?:\"\"?(?!\")|[^\\\\\"]|\\\\.)*\"{0,3})|(?:[^\"\\r\\n\\\\]|\\\\.)*\"?))"),
                null,
                "\""
        )
        _shortcutStylePatterns.new(
                Prettify.PR_LITERAL, Pattern.compile("^`(?:[^\\r\\n\\\\`]|\\\\.)*`?"), null, "`"
        )
        _shortcutStylePatterns.new(
                Prettify.PR_PUNCTUATION,
                Pattern.compile("^[!#%&()*+,\\-:;<=>?@\\[\\\\\\]^{|}~]+"),
                null,
                "!#%&()*+,-:;<=>?@[\\\\]^{|}~"
        )
        // A symbol literal is a single quote followed by an identifier with no
        // single quote following
        // A character literal has single quotes on either side
        _fallthroughStylePatterns.new(
                Prettify.PR_STRING, Pattern.compile("^'(?:[^\\r\\n\\\\']|\\\\(?:'|[^\\r\\n']+))'")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL, Pattern.compile("^'[a-zA-Z_$][\\w$]*(?!['$\\w])")
            )
        _fallthroughStylePatterns.new(
                Prettify.PR_KEYWORD, Pattern.compile("^(?:abstract|case|catch|class|def|do|else|extends|final|finally|for|forSome|if|implicit|import|lazy|match|new|object|override|package|private|protected|requires|return|sealed|super|throw|trait|try|type|val|var|while|with|yield)\\b")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL, Pattern.compile("^(?:true|false|null|this)\\b")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_LITERAL, Pattern.compile(
                    "^(?:(?:0(?:[0-7]+|X[0-9A-F]+))L?|(?:(?:0|[1-9][0-9]*)(?:(?:\\.[0-9]+)?(?:E[+\\-]?[0-9]+)?F?|L?))|\\\\.[0-9]+(?:E[+\\-]?[0-9]+)?F?)",
                    Pattern.CASE_INSENSITIVE
            )
        )
        // Treat upper camel case identifiers as types.
        _fallthroughStylePatterns.new(
                Prettify.PR_TYPE, Pattern.compile("^[\$_]*[A-Z][_\$A-Z0-9]*[a-z][\\w$]*")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_PLAIN, Pattern.compile("^[\$a-zA-Z_][\\w$]*")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_COMMENT, Pattern.compile("^\\/(?:\\/.*|\\*(?:\\/|\\**[^*/])*(?:\\*+\\/?)?)")
        )
        _fallthroughStylePatterns.new(
                Prettify.PR_PUNCTUATION, Pattern.compile("^(?:\\.+|\\/)")
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}