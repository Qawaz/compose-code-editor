// Copyright (C) 2010 benoit@ryder.fr
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

import java.util.regex.Pattern

/**
 * This is similar to the lang-vhdl.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 *
 *
 * Registers a language handler for VHDL '93.
 *
 * Based on the lexical grammar and keywords at
 * http://www.iis.ee.ethz.ch/~zimmi/download/vhdl93_syntax.html
 *
 * @author benoit@ryder.fr
 */
class LangVhdl : Lang() {
    companion object {
        val fileExtensions: List<String>
            get() = listOf("vhdl", "vhd")
    }

    init {
        val _shortcutStylePatterns: MutableList<StylePattern> = ArrayList()
        val _fallthroughStylePatterns: MutableList<StylePattern> = ArrayList()

        // Whitespace
        _shortcutStylePatterns.add(
            listOf(
                Prettify.PR_PLAIN, Pattern.compile("^[\\t\\n\\r \\xA0]+"), null, "\t\n\r " + 0xA0.toChar().toString()
            )
        )
        // String, character or bit string
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_STRING, Pattern.compile("^(?:[BOX]?\"(?:[^\\\"]|\"\")*\"|'.')", Pattern.CASE_INSENSITIVE)
            )
        )
        // Comment, from two dashes until end of line.
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_COMMENT, Pattern.compile("^--[^\\r\\n]*")
            )
        )
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_KEYWORD, Pattern.compile(
                    "^(?:abs|access|after|alias|all|and|architecture|array|assert|attribute|begin|block|body|buffer|bus|case|component|configuration|constant|disconnect|downto|else|elsif|end|entity|exit|file|for|function|generate|generic|group|guarded|if|impure|in|inertial|inout|is|label|library|linkage|literal|loop|map|mod|nand|new|next|nor|not|null|of|on|open|or|others|out|package|port|postponed|procedure|process|pure|range|record|register|reject|rem|report|return|rol|ror|select|severity|shared|signal|sla|sll|sra|srl|subtype|then|to|transport|type|unaffected|units|until|use|variable|wait|when|while|with|xnor|xor)(?=[^\\w-]|$)",
                    Pattern.CASE_INSENSITIVE
                ), null
            )
        )
        // Type, predefined or standard
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_TYPE, Pattern.compile(
                    "^(?:bit|bit_vector|character|boolean|integer|real|time|string|severity_level|positive|natural|signed|unsigned|line|text|std_u?logic(?:_vector)?)(?=[^\\w-]|$)",
                    Pattern.CASE_INSENSITIVE
                ), null
            )
        )
        // Predefined attributes
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_TYPE, Pattern.compile(
                    "^\\'(?:ACTIVE|ASCENDING|BASE|DELAYED|DRIVING|DRIVING_VALUE|EVENT|HIGH|IMAGE|INSTANCE_NAME|LAST_ACTIVE|LAST_EVENT|LAST_VALUE|LEFT|LEFTOF|LENGTH|LOW|PATH_NAME|POS|PRED|QUIET|RANGE|REVERSE_RANGE|RIGHT|RIGHTOF|SIMPLE_NAME|STABLE|SUCC|TRANSACTION|VAL|VALUE)(?=[^\\w-]|$)",
                    Pattern.CASE_INSENSITIVE
                ), null
            )
        )
        // Number, decimal or based literal
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_LITERAL, Pattern.compile(
                    "^\\d+(?:_\\d+)*(?:#[\\w\\\\.]+#(?:[+\\-]?\\d+(?:_\\d+)*)?|(?:\\.\\d+(?:_\\d+)*)?(?:E[+\\-]?\\d+(?:_\\d+)*)?)",
                    Pattern.CASE_INSENSITIVE
                )
            )
        )
        // Identifier, basic or extended
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_PLAIN, Pattern.compile("^(?:[a-z]\\w*|\\\\[^\\\\]*\\\\)", Pattern.CASE_INSENSITIVE)
            )
        )
        // Punctuation
        _fallthroughStylePatterns.add(
            listOf(
                Prettify.PR_PUNCTUATION, Pattern.compile("^[^\\w\\t\\n\\r \\xA0\\\"\\'][^\\w\\t\\n\\r \\xA0\\-\\\"\\']*")
            )
        )
        setShortcutStylePatterns(_shortcutStylePatterns)
        setFallthroughStylePatterns(_fallthroughStylePatterns)
    }

    override fun getFileExtensions(): List<String> {
        return fileExtensions
    }
}