// Copyright (C) 2012 Jeffrey Arnold
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
import java.util.*
import java.util.regex.Pattern

/**
 * This is similar to the lang-rd.js in JavaScript Prettify.
 *
 *
 * Support for R documentation (Rd) files
 *
 *
 * Minimal highlighting or Rd files, basically just highlighting
 * macros. It does not try to identify verbatim or R-like regions of
 * macros as that is too complicated for a lexer.  Descriptions of the
 * Rd format can be found
 * http://cran.r-project.org/doc/manuals/R-exts.html and
 * http://developer.r-project.org/parseRd.pdf.
 *
 * @author Jeffrey Arnold
 */
class LangRd : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = listOf("Rd", "rd")
    }

    init {
        val _shortcutStylePatterns: MutableList<List<Any?>> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any?>> = ArrayList()

        // whitespace
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_PLAIN, Pattern.compile("^[\\t\\n\\r \\xA0]+"), null, "\t\n\r " + Character.toString(
                    0xA0.toChar()
                )
            )
        )
        // all comments begin with '%'
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_COMMENT,
                Pattern.compile("^%[^\\r\\n]*"),
                null,
                "%"
            )
        )

        // special macros with no args
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_LITERAL,
                Pattern.compile("^\\\\(?:cr|l?dots|R|tab)\\b")
            )
        )
        // macros
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_KEYWORD,
                Pattern.compile("^\\\\[a-zA-Z@]+")
            )
        )
        // highlighted as macros, since technically they are
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_KEYWORD,
                Pattern.compile("^#(?:ifn?def|endif)")
            )
        )
        // catch escaped brackets
        _fallthroughStylePatterns.add(listOf(Prettify.PR_PLAIN, Pattern.compile("^\\\\[{}]")))
        // punctuation
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_PUNCTUATION,
                Pattern.compile("^[{}()\\[\\]]+")
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}