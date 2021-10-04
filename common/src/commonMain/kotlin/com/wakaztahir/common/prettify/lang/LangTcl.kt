// Copyright (C) 2012 Pyrios.
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
 * This is similar to the lang-tcl.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 * To use, include prettify.js and this file in your HTML page.
 * Then put your code in an HTML tag like
 * <pre class="prettyprint lang-tcl">proc foo {} {puts bar}</pre>
 *
 * I copy-pasted lang-lisp.js, so this is probably not 100% accurate.
 * I used http://wiki.tcl.tk/1019 for the keywords, but tried to only
 * include as keywords that had more impact on the program flow
 * rather than providing convenience. For example, I included 'if'
 * since that provides branching, but left off 'open' since that is more
 * like a proc. Add more if it makes sense.
 *
 * @author pyrios@gmail.com
 */
class LangTcl : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = Arrays.asList(*arrayOf("tcl"))
    }

    init {
        val _shortcutStylePatterns: MutableList<List<Any?>?> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any?>?> = ArrayList()
        _shortcutStylePatterns.add(Arrays.asList(*arrayOf<Any?>("opn", Pattern.compile("^\\{+"), null, "{")))
        _shortcutStylePatterns.add(Arrays.asList(*arrayOf<Any?>("clo", Pattern.compile("^\\}+"), null, "}")))
        // A line comment that starts with ;
        _shortcutStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any?>(
                    Prettify.PR_COMMENT,
                    Pattern.compile("^#[^\\r\\n]*"),
                    null,
                    "#"
                )
            )
        )
        // Whitespace
        _shortcutStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any?>(
                    Prettify.PR_PLAIN, Pattern.compile("^[\\t\\n\\r \\xA0]+"), null, "\t\n\r " + Character.toString(
                        0xA0.toChar()
                    )
                )
            )
        )
        // A double quoted, possibly multi-line, string.
        _shortcutStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any?>(
                    Prettify.PR_STRING,
                    Pattern.compile("^\\\"(?:[^\\\"\\\\]|\\\\[\\s\\S])*(?:\\\"|$)"),
                    null,
                    "\""
                )
            )
        )
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any?>(
                    Prettify.PR_KEYWORD,
                    Pattern.compile("^(?:after|append|apply|array|break|case|catch|continue|error|eval|exec|exit|expr|for|foreach|if|incr|info|proc|return|set|switch|trace|uplevel|upvar|while)\\b"),
                    null
                )
            )
        )
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any>(
                    Prettify.PR_LITERAL,
                    Pattern.compile(
                        "^[+\\-]?(?:[0#]x[0-9a-f]+|\\d+\\/\\d+|(?:\\.\\d+|\\d+(?:\\.\\d*)?)(?:[ed][+\\-]?\\d+)?)",
                        Pattern.CASE_INSENSITIVE
                    )
                )
            )
        )
        // A single quote possibly followed by a word that optionally ends with
        // = ! or ?.
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any>(
                    Prettify.PR_LITERAL,
                    Pattern.compile("^\\'(?:-*(?:\\w|\\\\[\\x21-\\x7e])(?:[\\w-]*|\\\\[\\x21-\\x7e])[=!?]?)?")
                )
            )
        )
        // A word that optionally ends with = ! or ?.
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any>(
                    Prettify.PR_PLAIN,
                    Pattern.compile("^-*(?:[a-z_]|\\\\[\\x21-\\x7e])(?:[\\w-]*|\\\\[\\x21-\\x7e])[=!?]?")
                )
            )
        )
        // A printable non-space non-special character
        _fallthroughStylePatterns.add(
            Arrays.asList(
                *arrayOf<Any>(
                    Prettify.PR_PUNCTUATION,
                    Pattern.compile("^[^\\w\\t\\n\\r \\xA0()\\\"\\\\\\';]+")
                )
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }
}