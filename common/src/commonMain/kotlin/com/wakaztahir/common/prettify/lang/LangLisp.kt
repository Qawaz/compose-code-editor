// Copyright (C) 2008 Google Inc.
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

import com.wakaztahir.common.prettify.parser.Prettify
import java.util.*
import java.util.regex.Pattern

/**
 * This is similar to the lang-lisp.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 *
 *
 * Registers a language handler for Common Lisp and related languages.
 *
 *
 * To use, include prettify.js and this file in your HTML page.
 * Then put your code in an HTML tag like
 * <pre class="prettyprint lang-lisp">(my lisp code)</pre>
 * The lang-cl class identifies the language as common lisp.
 * This file supports the following language extensions:
 * lang-cl - Common Lisp
 * lang-el - Emacs Lisp
 * lang-lisp - Lisp
 * lang-scm - Scheme
 * lang-lsp - FAT 8.3 filename version of lang-lisp.
 *
 *
 * I used http://www.devincook.com/goldparser/doc/meta-language/grammar-LISP.htm
 * as the basis, but added line comments that start with ; and changed the atom
 * production to disallow unquoted semicolons.
 *
 * "Name"    = 'LISP'
 * "Author"  = 'John McCarthy'
 * "Version" = 'Minimal'
 * "About"   = 'LISP is an abstract language that organizes ALL'
 * | 'data around "lists".'
 *
 * "Start Symbol" = [s-Expression]
 *
 * {Atom Char}   = {Printable} - {Whitespace} - [()"\'']
 *
 * Atom = ( {Atom Char} | '\'{Printable} )+
 *
 * [s-Expression] ::= [Quote] Atom
 * | [Quote] '(' [Series] ')'
 * | [Quote] '(' [s-Expression] '.' [s-Expression] ')'
 *
 * [Series] ::= [s-Expression] [Series]
 * |
 *
 * [Quote]  ::= ''      !Quote = do not evaluate
 * |
 *
 *
 * I used [Practical Common Lisp](http://gigamonkeys.com/book/) as
 * the basis for the reserved word list.
 *
 *
 * @author mikesamuel@gmail.com
 */
class LangLisp : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = Arrays.asList("cl", "el", "lisp", "lsp", "scm", "ss", "rkt")
    }

    init {
        val _shortcutStylePatterns: MutableList<List<Any?>> = ArrayList()
        val _fallthroughStylePatterns: MutableList<List<Any?>> = ArrayList()
        _shortcutStylePatterns.add(listOf("opn", Pattern.compile("^\\(+"), null, "("))
        _shortcutStylePatterns.add(listOf("clo", Pattern.compile("^\\)+"), null, ")"))
        // A line comment that starts with ;
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_COMMENT,
                Pattern.compile("^;[^\r\n]*"),
                null,
                ";"
            )
        )
        // Whitespace
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_PLAIN, Pattern.compile("^[\t\n\r \\xA0]+"), null, "\t\n\r " + Character.toString(
                    0xA0.toChar()
                )
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
                Pattern.compile(
                    "^(?:block|c[ad]+r|catch|con[ds]|def(?:ine|un)|do|eq|eql|equal|equalp|eval-when|flet|format|go|if|labels|lambda|let|load-time-value|locally|macrolet|multiple-value-call|nil|progn|progv|quote|require|return-from|setq|symbol-macrolet|t|tagbody|the|throw|unwind)\\b",
                    Pattern.CASE_INSENSITIVE
                ),
                null
            )
        )
        _fallthroughStylePatterns.add(
            Arrays.asList(
                Prettify.PR_LITERAL, Pattern.compile(
                    "^[+\\-]?(?:[0#]x[0-9a-f]+|\\d+\\/\\d+|(?:\\.\\d+|\\d+(?:\\.\\d*)?)(?:[ed][+\\-]?\\d+)?)",
                    Pattern.CASE_INSENSITIVE
                )
            )
        )
        // A single quote possibly followed by a word that optionally ends with
        // = ! or ?.
        _fallthroughStylePatterns.add(
            Arrays.asList(
                Prettify.PR_LITERAL,
                Pattern.compile("^\\'(?:-*(?:\\w|\\\\[\\x21-\\x7e])(?:[\\w-]*|\\\\[\\x21-\\x7e])[=!?]?)?")
            )
        )
        // A word that optionally ends with = ! or ?.
        _fallthroughStylePatterns.add(
            Arrays.asList(
                Prettify.PR_PLAIN, Pattern.compile(
                    "^-*(?:[a-z_]|\\\\[\\x21-\\x7e])(?:[\\w-]*|\\\\[\\x21-\\x7e])[=!?]?",
                    Pattern.CASE_INSENSITIVE
                )
            )
        )
        // A printable non-space non-special character
        _fallthroughStylePatterns.add(
            Arrays.asList(
                Prettify.PR_PUNCTUATION, Pattern.compile("^[^\\w\\t\\n\\r \\xA0()\\\"\\\\\\';]+")
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}