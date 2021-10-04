// Copyright (C) 2006 Google Inc.
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
package com.wakaztahir.common.prettify.parser

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import com.wakaztahir.common.prettify.parser.CombinePrefixPattern
import com.wakaztahir.common.prettify.parser.Util.join
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.*
import java.util.regex.Pattern

/**
 * This is similar to the combinePrefixPattern.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 * @author mikesamuel@gmail.com
 */
class CombinePrefixPattern() {
    protected var capturedGroupIndex = 0
    protected var needToFoldCase = false

    /**
     * Given a group of [java.util.regex.Pattern]s, returns a `RegExp` that globally
     * matches the union of the sets of strings matched by the input RegExp.
     * Since it matches globally, if the input strings have a start-of-input
     * anchor (/^.../), it is ignored for the purposes of unioning.
     * @param regexs non multiline, non-global regexs.
     * @return Pattern a global regex.
     */
    @Throws(Exception::class)
    fun combinePrefixPattern(regexs: List<Pattern>): Pattern {
        var ignoreCase = false
        run {
            var i: Int = 0
            val n: Int = regexs.size
            while (i < n) {
                val regex: Pattern = regexs.get(i)
                if ((regex.flags() and Pattern.CASE_INSENSITIVE) != 0) {
                    ignoreCase = true
                } else if (Util.test(
                        Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE),
                        regex.pattern()
                            .replace("\\\\[Uu][0-9A-Fa-f]{4}|\\\\[Xx][0-9A-Fa-f]{2}|\\\\[^UuXx]".toRegex(), "")
                    )
                ) {
                    needToFoldCase = true
                    ignoreCase = false
                    break
                }
                ++i
            }
        }
        val rewritten: MutableList<String> = ArrayList()
        var i = 0
        val n = regexs.size
        while (i < n) {
            val regex = regexs[i]
            if ((regex.flags() and Pattern.MULTILINE) != 0) {
                throw Exception(regex.pattern())
            }
            rewritten.add("(?:" + allowAnywhereFoldCaseAndRenumberGroups(regex) + ")")
            ++i
        }
        return if (ignoreCase) Pattern.compile(
            Util.join(rewritten, "|"),
            Pattern.CASE_INSENSITIVE
        ) else Pattern.compile(
            Util.join(rewritten, "|")
        )
    }

    companion object {
        protected val escapeCharToCodeUnit: MutableMap<Char, Int> = HashMap()
        protected fun decodeEscape(charsetPart: String?): Int {
            var cc0 = charsetPart!!.codePointAt(0)
            if (cc0 != 92 /* \\ */) {
                return cc0
            }
            val c1 = charsetPart[1]
            cc0 = (escapeCharToCodeUnit[c1])!!
            if (cc0 != null) {
                return cc0
            } else if ('0' <= c1 && c1 <= '7') {
                return charsetPart.substring(1).toInt(8)
            } else return if (c1 == 'u' || c1 == 'x') {
                charsetPart.substring(2).toInt(16)
            } else {
                charsetPart.codePointAt(1)
            }
        }

        protected fun encodeEscape(charCode: Int): String {
            if (charCode < 0x20) {
                return (if (charCode < 0x10) "\\x0" else "\\x") + Integer.toString(charCode, 16)
            }
            val ch = String(Character.toChars(charCode))
            return if (((charCode == '\\'.toInt()) || (charCode == '-'.toInt()) || (charCode == ']'.toInt()) || (charCode == '^'.toInt()))) "\\" + ch else ch
        }

        protected fun caseFoldCharset(charSet: String?): String {
            val charsetParts = Util.match(
                Pattern.compile(
                    ("\\\\u[0-9A-Fa-f]{4}"
                            + "|\\\\x[0-9A-Fa-f]{2}"
                            + "|\\\\[0-3][0-7]{0,2}"
                            + "|\\\\[0-7]{1,2}"
                            + "|\\\\[\\s\\S]"
                            + "|-"
                            + "|[^-\\\\]")
                ), charSet!!.substring(1, charSet.length - 1), true
            )
            val ranges: MutableList<MutableList<Int>> = ArrayList()
            val inverse = charsetParts!![0] != null && (charsetParts[0] == "^")
            val out: MutableList<String> = ArrayList(Arrays.asList(*arrayOf("[")))
            if (inverse) {
                out.add("^")
            }
            run {
                var i: Int = if (inverse) 1 else 0
                val n: Int = charsetParts.size
                while (i < n) {
                    val p: String = charsetParts[i]
                    if (Util.test(
                            Pattern.compile("\\\\[bdsw]", Pattern.CASE_INSENSITIVE),
                            p
                        )
                    ) {  // Don't muck with named groups.
                        out.add(p)
                    } else {
                        val start: Int = decodeEscape(p)
                        var end: Int
                        if (i + 2 < n && ("-" == charsetParts[i + 1])) {
                            end = decodeEscape(charsetParts[i + 2])
                            i += 2
                        } else {
                            end = start
                        }
                        ranges.add(Arrays.asList(*arrayOf(start, end)))
                        // If the range might intersect letters, then expand it.
                        // This case handling is too simplistic.
                        // It does not deal with non-latin case folding.
                        // It works for latin source code identifiers though.
                        if (!(end < 65 || start > 122)) {
                            if (!(end < 65 || start > 90)) {
                                ranges.add(Arrays.asList(*arrayOf(Math.max(65, start) or 32, Math.min(end, 90) or 32)))
                            }
                            if (!(end < 97 || start > 122)) {
                                ranges.add(
                                    Arrays.asList(
                                        *arrayOf(
                                            Math.max(97, start) and 32.inv(),
                                            Math.min(end, 122) and 32.inv()
                                        )
                                    )
                                )
                            }
                        }
                    }
                    ++i
                }
            }

            // [[1, 10], [3, 4], [8, 12], [14, 14], [16, 16], [17, 17]]
            // -> [[1, 12], [14, 14], [16, 17]]
            Collections.sort(ranges, Comparator { a, b -> if (a[0] !== b[0]) (a[0] - b[0]) else (b[1] - a[1]) })
            val consolidatedRanges: MutableList<List<Int>> = ArrayList()
            //        List<Integer> lastRange = Arrays.asList(new Integer[]{0, 0});
            var lastRange: MutableList<Int> = ArrayList(Arrays.asList(*arrayOf(0, 0)))
            for (i in ranges.indices) {
                val range = ranges[i]
                if (lastRange[1] != null && range[0] <= lastRange[1]!! + 1) {
                    lastRange[1] = Math.max(lastRange[1]!!, range[1])
                } else {
                    // reference of lastRange is added
                    consolidatedRanges.add(range.also { lastRange = it })
                }
            }
            for (i in consolidatedRanges.indices) {
                val range = consolidatedRanges[i]
                out.add(encodeEscape(range[0]))
                if (range[1] > range[0]) {
                    if (range[1] + 1 > range[0]) {
                        out.add("-")
                    }
                    out.add(encodeEscape(range[1]))
                }
            }
            out.add("]")
            return Util.join(out)
        }

        init {
            escapeCharToCodeUnit['b'] = 8
            escapeCharToCodeUnit['t'] = 9
            escapeCharToCodeUnit['n'] = 0xa
            escapeCharToCodeUnit['v'] = 0xb
            escapeCharToCodeUnit['f'] = 0xc
            escapeCharToCodeUnit['r'] = 0xf
        }
    }

    protected fun allowAnywhereFoldCaseAndRenumberGroups(regex: Pattern): String {
        // Split into character sets, escape sequences, punctuation strings
        // like ('(', '(?:', ')', '^'), and runs of characters that do not
        // include any of the above.
        val parts = Util.match(
            Pattern.compile(
                ("(?:"
                        + "\\[(?:[^\\x5C\\x5D]|\\\\[\\s\\S])*\\]" // a character set
                        + "|\\\\u[A-Fa-f0-9]{4}" // a unicode escape
                        + "|\\\\x[A-Fa-f0-9]{2}" // a hex escape
                        + "|\\\\[0-9]+" // a back-reference or octal escape
                        + "|\\\\[^ux0-9]" // other escape sequence
                        + "|\\(\\?[:!=]" // start of a non-capturing group
                        + "|[\\(\\)\\^]" // start/end of a group, or line start
                        + "|[^\\x5B\\x5C\\(\\)\\^]+" // run of other characters
                        + ")")
            ), regex.pattern(), true
        )
        val n = parts.size

        // Maps captured group numbers to the number they will occupy in
        // the output or to -1 if that has not been determined, or to
        // undefined if they need not be capturing in the output.
        val capturedGroups: MutableMap<Int, Int?> = HashMap()

        // Walk over and identify back references to build the capturedGroups
        // mapping.
        run {
            var i: Int = 0
            var groupIndex: Int = 0
            while (i < n) {
                val p: String? = parts.get(i)
                if ((p == "(")) {
                    // groups are 1-indexed, so max group index is count of '('
                    ++groupIndex
                } else if ('\\' == p!!.get(0)) {
                    try {
                        val decimalValue: Int = Math.abs(p.substring(1).toInt())
                        if (decimalValue <= groupIndex) {
                            capturedGroups.put(decimalValue, -1)
                        } else {
                            // Replace with an unambiguous escape sequence so that
                            // an octal escape sequence does not turn into a backreference
                            // to a capturing group from an earlier regex.
                            parts[i] = encodeEscape(decimalValue)
                        }
                    } catch (ex: NumberFormatException) {
                    }
                }
                ++i
            }
        }

        // Renumber groups and reduce capturing groups to non-capturing groups
        // where possible.
        for (i: Int in capturedGroups.keys) {
            if (-1 == capturedGroups[i]) {
                capturedGroups[i] = ++capturedGroupIndex
            }
        }
        run {
            var i: Int = 0
            var groupIndex: Int = 0
            while (i < n) {
                val p: String? = parts.get(i)
                if ((p == "(")) {
                    ++groupIndex
                    if (capturedGroups.get(groupIndex) == null) {
                        parts[i] = "(?:"
                    }
                } else if ('\\' == p!!.get(0)) {
                    try {
                        val decimalValue: Int = Math.abs(p.substring(1).toInt())
                        if (decimalValue <= groupIndex) {
                            parts[i] = "\\" + capturedGroups.get(decimalValue)
                        }
                    } catch (ex: NumberFormatException) {
                    }
                }
                ++i
            }
        }

        // Remove any prefix anchors so that the output will match anywhere.
        // ^^ really does mean an anchored match though.
        for (i in 0 until n) {
            if (("^" == parts[i]) && "^" != parts.get(i + 1)) {
                parts[i] = ""
            }
        }

        // Expand letters to groups to handle mixing of case-sensitive and
        // case-insensitive patterns if necessary.
        if ((regex.flags() and Pattern.CASE_INSENSITIVE) != 0 && needToFoldCase) {
            for (i in 0 until n) {
                val p = parts[i]
                val ch0: Char = if (p!!.length > 0) p[0] else '0'
                if (p.length >= 2 && ch0 == '[') {
                    parts[i] = caseFoldCharset(p)
                } else if (ch0 != '\\') {
                    // TODO: handle letters in numeric escapes.
                    val sb = StringBuffer()
                    val _matcher = Pattern.compile("[a-zA-Z]").matcher(p)
                    while (_matcher.find()) {
                        val cc = _matcher.group(0).codePointAt(0)
                        _matcher.appendReplacement(sb, "")
                        sb.append("[").append(Character.toString((cc and 32.inv()).toChar())).append(
                            Character.toString(
                                (cc or 32).toChar()
                            )
                        ).append("]")
                    }
                    _matcher.appendTail(sb)
                    parts[i] = sb.toString()
                }
            }
        }
        return join(parts)
    }
}