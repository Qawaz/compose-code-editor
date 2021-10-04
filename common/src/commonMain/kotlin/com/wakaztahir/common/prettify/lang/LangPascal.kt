// Copyright (C) 2009 Onno Hommes.
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
 * This is similar to the lang-appollo.js in JavaScript Prettify.
 *
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 *
 *
 *
 * Registers a language handler for the AGC/AEA Assembly Language as described
 * at http://virtualagc.googlecode.com
 *
 *
 * This file could be used by goodle code to allow syntax highlight for
 * Virtual AGC SVN repository or if you don't want to commonize
 * the header for the agc/aea html assembly listing.
 *
 * @author ohommes@alumni.cmu.edu
 */
class LangPascal : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = Arrays.asList(*arrayOf("pascal"))
    }

    init {
        val _shortcutStylePatterns: MutableList<List<Any>?> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any>?> = ArrayList()

        // 'single-line-string'
        _shortcutStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_STRING,
                    Pattern.compile("^(?:\\'(?:[^\\\\\\'\\r\\n]|\\\\.)*(?:\\'|$))"),
                    null,
                    "'"
                )
            )
        )
        // Whitespace
        _shortcutStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_PLAIN, Pattern.compile("^\\s+"), null, " \r\n\t" + Character.toString(
                        0xA0.toChar()
                    )
                )
            )
        )

        // A cStyleComments comment (* *) or {}
        _fallthroughStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_COMMENT,
                    Pattern.compile("^\\(\\*[\\s\\S]*?(?:\\*\\)|$)|^\\{[\\s\\S]*?(?:\\}|$)"),
                    null
                )
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_KEYWORD,
                    Pattern.compile(
                        "^(?:ABSOLUTE|AND|ARRAY|ASM|ASSEMBLER|BEGIN|CASE|CONST|CONSTRUCTOR|DESTRUCTOR|DIV|DO|DOWNTO|ELSE|END|EXTERNAL|FOR|FORWARD|FUNCTION|GOTO|IF|IMPLEMENTATION|IN|INLINE|INTERFACE|INTERRUPT|LABEL|MOD|NOT|OBJECT|OF|OR|PACKED|PROCEDURE|PROGRAM|RECORD|REPEAT|SET|SHL|SHR|THEN|TO|TYPE|UNIT|UNTIL|USES|VAR|VIRTUAL|WHILE|WITH|XOR)\\b",
                        Pattern.CASE_INSENSITIVE
                    ),
                    null
                )
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_LITERAL,
                    Pattern.compile("^(?:true|false|self|nil)", Pattern.CASE_INSENSITIVE),
                    null
                )
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_PLAIN,
                    Pattern.compile("^[a-z][a-z0-9]*", Pattern.CASE_INSENSITIVE),
                    null
                )
            )
        )
        // Literals .0, 0, 0.0 0E13
        _fallthroughStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_LITERAL,
                    Pattern.compile(
                        "^(?:\\$[a-f0-9]+|(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:e[+\\-]?\\d+)?)",
                        Pattern.CASE_INSENSITIVE
                    ),
                    null,
                    "0123456789"
                )
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                listOf(
                    Prettify.PR_PUNCTUATION,
                    Pattern.compile("^.[^\\s\\w\\.$@\\'\\/]*"),
                    null
                )
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }
}