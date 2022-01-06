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
package com.wakaztahir.codeeditor.prettify.lang

import com.wakaztahir.codeeditor.prettify.parser.Prettify

import java.util.regex.Pattern

/**
 * This is similar to the lang-appollo.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
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
class LangAppollo : Lang() {

    companion object{
        val fileExtensions = listOf("apollo", "agc", "aea")
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }

    init {
        val _shortcutStylePatterns: MutableList<List<Any?>> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any?>> = ArrayList()

        // A line comment that starts with ;
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_COMMENT,
                Pattern.compile("^#[^\r\n]*"),
                null,
                "#"
            )
        )
        // Whitespace
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_PLAIN, Pattern.compile("^[\t\n\r \\xA0]+"), null, "\t\n\r " + 0xA0.toChar().toString()
            )
        )
        // A double quoted, possibly multi-line, string.
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_STRING,
                Pattern.compile("^\\\"(?:[^\\\"\\\\]|\\\\[\\s\\S])*(?:\\\"|$)"),
                null,
                "\""
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_KEYWORD,
                Pattern.compile("^(?:ADS|AD|AUG|BZF|BZMF|CAE|CAF|CA|CCS|COM|CS|DAS|DCA|DCOM|DCS|DDOUBL|DIM|DOUBLE|DTCB|DTCF|DV|DXCH|EDRUPT|EXTEND|INCR|INDEX|NDX|INHINT|LXCH|MASK|MSK|MP|MSU|NOOP|OVSK|QXCH|RAND|READ|RELINT|RESUME|RETURN|ROR|RXOR|SQUARE|SU|TCR|TCAA|OVSK|TCF|TC|TS|WAND|WOR|WRITE|XCH|XLQ|XXALQ|ZL|ZQ|ADD|ADZ|SUB|SUZ|MPY|MPR|MPZ|DVP|COM|ABS|CLA|CLZ|LDQ|STO|STQ|ALS|LLS|LRS|TRA|TSQ|TMI|TOV|AXT|TIX|DLY|INP|OUT)\\s"),
                null
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_TYPE,
                Pattern.compile("^(?:-?GENADR|=MINUS|2BCADR|VN|BOF|MM|-?2CADR|-?[1-6]DNADR|ADRES|BBCON|[SE]?BANK\\=?|BLOCK|BNKSUM|E?CADR|COUNT\\*?|2?DEC\\*?|-?DNCHAN|-?DNPTR|EQUALS|ERASE|MEMORY|2?OCT|REMADR|SETLOC|SUBRO|ORG|BSS|BES|SYN|EQU|DEFINE|END)\\s"),
                null
            )
        )
        // A single quote possibly followed by a word that optionally ends with
        // = ! or ?.
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_LITERAL,
                Pattern.compile("^\\'(?:-*(?:\\w|\\\\[\\x21-\\x7e])(?:[\\w-]*|\\\\[\\x21-\\x7e])[=!?]?)?")
            )
        )
        // Any word including labels that optionally ends with = ! or ?.
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_PLAIN,
                Pattern.compile(
                    "^-*(?:[!-z_]|\\\\[\\x21-\\x7e])(?:[\\w-]*|\\\\[\\x21-\\x7e])[=!?]?",
                    Pattern.CASE_INSENSITIVE
                )
            )
        )
        // A printable non-space non-special character
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_PUNCTUATION,
                Pattern.compile("^[^\\w\\t\\n\\r \\xA0()\\\"\\\\\\';]+")
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }
}