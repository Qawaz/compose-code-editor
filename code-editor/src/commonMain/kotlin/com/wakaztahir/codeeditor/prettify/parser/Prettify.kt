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
package com.wakaztahir.codeeditor.prettify.parser

import com.wakaztahir.codeeditor.prettify.lang.*
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

/**
 * This is similar to the prettify.js in JavaScript Prettify.
 *
 * All comments are adapted from the JavaScript Prettify.
 *
 *
 *
 * Some functions for browser-side pretty printing of code contained in html.
 *
 *
 *
 *
 * For a fairly comprehensive set of languages see the
 * [README](http://google-code-prettify.googlecode.com/svn/trunk/README.html#langs)
 * file that came with this source.  At a minimum, the lexer should work on a
 * number of languages including C and friends, Java, Python, Bash, SQL, HTML,
 * XML, CSS, Javascript, and Makefiles.  It works passably on Ruby, PHP and Awk
 * and a subset of Perl, but, because of commenting conventions, doesn't work on
 * Smalltalk, Lisp-like, or CAML-like languages without an explicit lang class.
 *
 *
 * Usage:
 *  1.  include this source file in an html page via
 * `<script type="text/javascript" src="/path/to/prettify.js"></script>`
 *  1.  define style rules.  See the example page for examples.
 *  1.  mark the `<pre>` and `<code>` tags in your source with
 * `class=prettyprint.`
 * You can also use the (html deprecated) `<xmp>` tag, but the pretty
 * printer needs to do more substantial DOM manipulations to support that, so
 * some css styles may not be preserved.
 *
 * That's it.  I wanted to keep the API as simple as possible, so there's no
 * need to specify which language the code is in, but if you wish, you can add
 * another class to the `<pre>` or `<code>` element to specify the
 * language, as in `<pre class="prettyprint lang-java">`.  Any class that
 * starts with "lang-" followed by a file extension, specifies the file type.
 * See the "lang-*.js" files in this directory for code that implements
 * per-language file handlers.
 *
 *
 * Change log:<br></br>
 * cbeust, 2006/08/22
 * <blockquote>
 * Java annotations (start with "@") are now captured as literals ("lit")
</blockquote> *
 */
class Prettify {

    inner class CreateSimpleLexer(
        shortcutStylePatterns: List<List<Any?>>,
        private var fallthroughStylePatterns: List<List<Any?>>
    ) {
        private var shortcuts: MutableMap<Char, List<Any?>> = HashMap()
        private var tokenizer: Pattern
        private var nPatterns: Int

        /**
         * Lexes job.sourceCode and produces an output array job.decorations of
         * style classes preceded by the position at which they start in
         * job.sourceCode in order.
         *
         * @param job an object like <pre>{
         * sourceCode: {string} sourceText plain text,
         * basePos: {int} position of job.sourceCode in the larger chunk of
         * sourceCode.
         * }</pre>
         */
        fun decorate(job: Job) {
            val sourceCode = job.getSourceCode()
            val basePos = job.basePos

            /** Even entries are positions in source in ascending order.  Odd enties
             * are style markers (e.g., PR_COMMENT) that run from that position until
             * the end.
             * @type {Array.<number></number>|string>}
             */
            val decorations: MutableList<Any> = ArrayList(listOf(basePos, PR_PLAIN))
            var pos = 0 // index into sourceCode
            val tokens = Util.match(tokenizer, sourceCode, true)
            val styleCache: MutableMap<String, String?> = HashMap()
            var ti = 0
            val nTokens = tokens.size
            while (ti < nTokens) {
                val token = tokens[ti]
                var style = styleCache[token]
                var match: Array<String>? = null
                var isEmbedded: Boolean
                if (style != null) {
                    isEmbedded = false
                } else {
                    var patternParts = shortcuts[token[0]]
                    if (patternParts != null) {
                        match = Util.match(patternParts[1] as Pattern, token, false)
                        style = patternParts[0] as String
                    } else {
                        for (i in 0 until nPatterns) {
                            patternParts = fallthroughStylePatterns[i]
                            match = Util.match(patternParts[1] as Pattern, token, false)
                            if (match.isNotEmpty()) {
                                style = patternParts[0] as String
                                break
                            }
                        }
                        if (match!!.isEmpty()) {  // make sure that we make progress
                            style = PR_PLAIN
                        }
                    }
                    isEmbedded = style != null && style.length >= 5 && style.startsWith("lang-")
                    if (isEmbedded && match.size <= 1) {
                        isEmbedded = false
                        style = PR_SOURCE
                    }
                    if (!isEmbedded) {
                        styleCache[token] = style
                    }
                }
                val tokenStart = pos
                pos += token.length
                if (!isEmbedded) {
                    decorations.add(basePos + tokenStart)
                    if (style != null) {
                        decorations.add(style)
                    }
                } else {  // Treat group 1 as an embedded block of source code.
                    val embeddedSource = match!![1]
                    var embeddedSourceStart = token.indexOf(embeddedSource!!)
                    var embeddedSourceEnd = embeddedSourceStart + embeddedSource.length
                    if (match.size > 2) {
                        // If embeddedSource can be blank, then it would match at the
                        // beginning which would cause us to infinitely recurse on the
                        // entire token, so we catch the right context in match[2].
                        embeddedSourceEnd = token.length - match[2].length
                        embeddedSourceStart = embeddedSourceEnd - embeddedSource.length
                    }
                    val lang = style!!.substring(5)
                    // Decorate the left of the embedded source
                    appendDecorations(
                        basePos + tokenStart,
                        token.substring(0, embeddedSourceStart),
                        this, decorations
                    )
                    // Decorate the embedded source
                    appendDecorations(
                        basePos + tokenStart + embeddedSourceStart,
                        embeddedSource,
                        langHandlerForExtension(lang, embeddedSource),
                        decorations
                    )
                    // Decorate the right of the embedded section
                    appendDecorations(
                        basePos + tokenStart + embeddedSourceEnd,
                        token.substring(embeddedSourceEnd),
                        this, decorations
                    )
                }
                ++ti
            }
            job.setDecorations(Util.removeDuplicates(decorations, job.getSourceCode()))
        }

        /** Given triples of [style, pattern, context] returns a lexing function,
         * The lexing function interprets the patterns to find token boundaries and
         * returns a decoration list of the form
         * [index_0, style_0, index_1, style_1, ..., index_n, style_n]
         * where index_n is an index into the sourceCode, and style_n is a style
         * constant like PR_PLAIN.  index_n-1 <= index_n, and style_n-1 applies to
         * all characters in sourceCode[index_n-1:index_n].
         *
         * The stylePatterns is a list whose elements have the form
         * [style : string, pattern : RegExp, DEPRECATED, shortcut : string].
         *
         * Style is a style constant like PR_PLAIN, or can be a string of the
         * form 'lang-FOO', where FOO is a language extension describing the
         * language of the portion of the token in $1 after pattern executes.
         * E.g., if style is 'lang-lisp', and group 1 contains the text
         * '(hello (world))', then that portion of the token will be passed to the
         * registered lisp handler for formatting.
         * The text before and after group 1 will be restyled using this decorator
         * so decorators should take care that this doesn't result in infinite
         * recursion.  For example, the HTML lexer rule for SCRIPT elements looks
         * something like ['lang-js', /<[s]cript>(.+?)<\/script>/].  This may match
         * '<script>foo()<\/script>', which would cause the current decorator to
        be called with '<script>' which would not match the same rule since
        group 1 must not be empty, so it would be instead styled as PR_TAG by
        the generic tag rule.  The handler registered for the 'js' extension would
        then be called with 'foo()', and finally, the current decorator would
        be called with '<\/script>' which would not match the original rule and
        so the generic tag rule would identify it as a tag.

        Pattern must only match prefixes, and if it matches a prefix, then that
        match is considered a token with the same style.

        Context is applied to the last non-whitespace, non-comment token
        recognized.

        Shortcut is an optional string of characters, any of which, if the first
        character, gurantee that this pattern and only this pattern matches.

        @param shortcutStylePatterns patterns that always start with
        a known character.  Must have a shortcut string.
        @param fallthroughStylePatterns patterns that will be tried in
        order if the shortcut ones fail.  May have shortcuts.
        </script> */
        init {
            val allPatterns: MutableList<List<Any?>> = ArrayList(shortcutStylePatterns)
            allPatterns.addAll(fallthroughStylePatterns)
            val allRegexs: MutableList<Pattern> = ArrayList()
            val regexKeys: MutableMap<String, Any?> = HashMap()
            allPatterns.forEach { patternParts ->
                val shortcutChars = if (patternParts.size > 3) patternParts[3] as String else null
                if (shortcutChars != null) {
                    var c = shortcutChars.length
                    while (--c >= 0) {
                        shortcuts[shortcutChars[c]] = patternParts
                    }
                }
                val regex = patternParts[1] as Pattern
                val k = regex.pattern()
                if (regexKeys[k] == null) {
                    allRegexs.add(regex)
                    regexKeys[k] = Any()
                }
            }
//            var i = 0
//            val n = allPatterns.size
//            while (i < n) {
//                val patternParts = allPatterns[i]
//                val shortcutChars = if (patternParts.size > 3) patternParts[3] as String else null
//                if (shortcutChars != null) {
//                    var c = shortcutChars.length
//                    while (--c >= 0) {
//                        shortcuts[shortcutChars[c]] = patternParts
//                    }
//                }
//                val regex = patternParts[1] as Pattern
//                val k = regex.pattern()
//                if (regexKeys[k] == null) {
//                    allRegexs.add(regex)
//                    regexKeys[k] = Any()
//                }
//                ++i
//            }
            allRegexs.add(Pattern.compile("[\u0000-\\uffff]"))
            tokenizer = CombinePrefixPattern().combinePrefixPattern(allRegexs)
            nPatterns = fallthroughStylePatterns.size
        }
    }

    /** returns a function that produces a list of decorations from source text.
     *
     * This code treats ", ', and ` as string delimiters, and \ as a string
     * escape.  It does not recognize perl's qq() style strings.
     * It has no special handling for double delimiter escapes as in basic, or
     * the tripled delimiters used in python, but should work on those regardless
     * although in those cases a single string literal may be broken up into
     * multiple adjacent string literals.
     *
     * It recognizes C, C++, and shell style comments.
     *
     * @param options a set of optional parameters.
     * @return a function that examines the source code
     * in the input job and builds the decoration list.
     */
    @Throws(Exception::class)
    internal fun sourceDecorator(options: Map<String?, Any?>): CreateSimpleLexer {
        val shortcutStylePatterns: MutableList<List<Any?>> = ArrayList()
        val fallthroughStylePatterns: MutableList<List<Any?>> = ArrayList()
        if (Util.getVariableValueAsBoolean(options["tripleQuotedStrings"])) {
            // '''multi-line-string''', 'single-line-string', and double-quoted
            shortcutStylePatterns.add(
                listOf(
                    PR_STRING,
                    Pattern.compile("^(?:\\'\\'\\'(?:[^\\'\\\\]|\\\\[\\s\\S]|\\'{1,2}(?=[^\\']))*(?:\\'\\'\\'|$)|\\\"\\\"\\\"(?:[^\\\"\\\\]|\\\\[\\s\\S]|\\\"{1,2}(?=[^\\\"]))*(?:\\\"\\\"\\\"|$)|\\'(?:[^\\\\\\']|\\\\[\\s\\S])*(?:\\'|$)|\\\"(?:[^\\\\\\\"]|\\\\[\\s\\S])*(?:\\\"|$))"),
                    null,
                    "'\""
                )
            )
        } else if (Util.getVariableValueAsBoolean(options["multiLineStrings"])) {
            // 'multi-line-string', "multi-line-string"
            shortcutStylePatterns.add(
                listOf(
                    PR_STRING,
                    Pattern.compile("^(?:\\'(?:[^\\\\\\']|\\\\[\\s\\S])*(?:\\'|$)|\\\"(?:[^\\\\\\\"]|\\\\[\\s\\S])*(?:\\\"|$)|\\`(?:[^\\\\\\`]|\\\\[\\s\\S])*(?:\\`|$))"),
                    null,
                    "'\"`"
                )
            )
        } else {
            // 'single-line-string', "single-line-string"
            shortcutStylePatterns.add(
                listOf(
                    PR_STRING,
                    Pattern.compile("^(?:\\'(?:[^\\\\\\'\r\n]|\\\\.)*(?:\\'|$)|\\\"(?:[^\\\\\\\"\r\n]|\\\\.)*(?:\\\"|$))"),
                    null,
                    "\"'"
                )
            )
        }
        if (Util.getVariableValueAsBoolean(options["verbatimStrings"])) {
            // verbatim-string-literal production from the C# grammar.  See issue 93.
            fallthroughStylePatterns.add(
                listOf(
                    PR_STRING,
                    Pattern.compile("^@\\\"(?:[^\\\"]|\\\"\\\")*(?:\\\"|$)"),
                    null
                )
            )
        }
        val hc = options["hashComments"]
        if (Util.getVariableValueAsBoolean(hc)) {
            if (Util.getVariableValueAsBoolean(options["cStyleComments"])) {
                if (hc is Int && hc > 1) {  // multiline hash comments
                    shortcutStylePatterns.add(
                        listOf(
                            PR_COMMENT,
                            Pattern.compile("^#(?:##(?:[^#]|#(?!##))*(?:###|$)|.*)"),
                            null,
                            "#"
                        )
                    )
                } else {
                    // Stop C preprocessor declarations at an unclosed open comment
                    shortcutStylePatterns.add(
                        listOf(
                            PR_COMMENT,
                            Pattern.compile("^#(?:(?:define|e(?:l|nd)if|else|error|ifn?def|include|line|pragma|undef|warning)\\b|[^\r\n]*)"),
                            null,
                            "#"
                        )
                    )
                }
                // #include <stdio.h>
                fallthroughStylePatterns.add(
                    listOf(
                        PR_STRING,
                        Pattern.compile("^<(?:(?:(?:\\.\\.\\/)*|\\/?)(?:[\\w-]+(?:\\/[\\w-]+)+)?[\\w-]+\\.h(?:h|pp|\\+\\+)?|[a-z]\\w*)>"),
                        null
                    )
                )
            } else {
                shortcutStylePatterns.add(
                    listOf(
                        PR_COMMENT,
                        Pattern.compile("^#[^\r\n]*"),
                        null,
                        "#"
                    )
                )
            }
        }
        if (Util.getVariableValueAsBoolean(options["cStyleComments"])) {
            fallthroughStylePatterns.add(
                listOf(
                    PR_COMMENT,
                    Pattern.compile("^\\/\\/[^\r\n]*"),
                    null
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    PR_COMMENT,
                    Pattern.compile("^\\/\\*[\\s\\S]*?(?:\\*\\/|$)"),
                    null
                )
            )
        }
        val regexLiterals = options["regexLiterals"]
        if (Util.getVariableValueAsBoolean(regexLiterals)) {
            /**
             * @const
             */
            // Javascript treat true as 1
            val regexExcls = if (Util.getVariableValueAsInteger(regexLiterals) > 1) "" // Multiline regex literals
            else "\n\r"

            /**
             * @const
             */
            val regexAny = if (!regexExcls.isEmpty()) "." else "[\\S\\s]"

            /**
             * @const
             */
            val REGEX_LITERAL =  // A regular expression literal starts with a slash that is
            // not followed by * or / so that it is not confused with
                // comments.
                ("/(?=[^/*" + regexExcls + "])" // and then contains any number of raw characters,
                        + "(?:[^/\\x5B\\x5C" + regexExcls + "]" // escape sequences (\x5C),
                        + "|\\x5C" + regexAny // or non-nesting character sets (\x5B\x5D);
                        + "|\\x5B(?:[^\\x5C\\x5D" + regexExcls + "]"
                        + "|\\x5C" + regexAny + ")*(?:\\x5D|$))+" // finally closed by a /.
                        + "/")
            fallthroughStylePatterns.add(
                listOf(
                    "lang-regex",
                    Pattern.compile("^$REGEXP_PRECEDER_PATTERN($REGEX_LITERAL)")
                )
            )
        }
        val types = options["types"] as Pattern?
        if (Util.getVariableValueAsBoolean(types)) {
            fallthroughStylePatterns.add(listOf(PR_TYPE, types))
        }
        var keywords = options["keywords"] as String?
        if (keywords != null) {
            keywords = keywords.replace("^ | $".toRegex(), "")
            if (keywords.isNotEmpty()) {
                fallthroughStylePatterns.add(
                    listOf(
                        PR_KEYWORD,
                        Pattern.compile("^(?:" + keywords.replace("[\\s,]+".toRegex(), "|") + ")\\b"),
                        null
                    )
                )
            }
        }
        shortcutStylePatterns.add(
            listOf(
                PR_PLAIN,
                Pattern.compile("^\\s+"),
                null,
                """ 
	${Character.toString(0xA0.toChar())}"""
            )
        )

        // TODO(mikesamuel): recognize non-latin letters and numerals in idents
        fallthroughStylePatterns.add(
            listOf(
                PR_LITERAL,
                Pattern.compile("^@[a-z_$][a-z_$@0-9]*", Pattern.CASE_INSENSITIVE),
                null
            )
        )
        fallthroughStylePatterns.add(
            listOf(
                PR_TYPE,
                Pattern.compile("^(?:[@_]?[A-Z]+[a-z][A-Za-z_$@0-9]*|\\w+_t\\b)"),
                null
            )
        )
        fallthroughStylePatterns.add(
            listOf(
                PR_PLAIN,
                Pattern.compile("^[a-z_$][a-z_$@0-9]*", Pattern.CASE_INSENSITIVE),
                null
            )
        )
        fallthroughStylePatterns.add(
            listOf(
                PR_LITERAL,
                Pattern.compile(
                    "^(?:" // A hex number
                            + "0x[a-f0-9]+" // or an octal or decimal number,
                            + "|(?:\\d(?:_\\d+)*\\d*(?:\\.\\d*)?|\\.\\d\\+)" // possibly in scientific notation
                            + "(?:e[+\\-]?\\d+)?"
                            + ')' // with an optional modifier like UL for unsigned long
                            + "[a-z]*", Pattern.CASE_INSENSITIVE
                ),
                null,
                "0123456789"
            )
        )
        // Don't treat escaped quotes in bash as starting strings.
        // See issue 144.
        fallthroughStylePatterns.add(
            listOf(
                PR_PLAIN,
                Pattern.compile("^\\\\[\\s\\S]?"),
                null
            )
        )

        // The Bash man page says

        // A word is a sequence of characters considered as a single
        // unit by GRUB. Words are separated by metacharacters,
        // which are the following plus space, tab, and newline: { }
        // | & $ ; < >
        // ...

        // A word beginning with # causes that word and all remaining
        // characters on that line to be ignored.

        // which means that only a '#' after /(?:^|[{}|&$;<>\s])/ starts a
        // comment but empirically
        // $ echo {#}
        // {#}
        // $ echo \$#
        // $#
        // $ echo }#
        // }#

        // so /(?:^|[|&;<>\s])/ is more appropriate.

        // http://gcc.gnu.org/onlinedocs/gcc-2.95.3/cpp_1.html#SEC3
        // suggests that this definition is compatible with a
        // default mode that tries to use a single token definition
        // to recognize both bash/python style comments and C
        // preprocessor directives.

        // This definition of punctuation does not include # in the list of
        // follow-on exclusions, so # will not be broken before if preceeded
        // by a punctuation character.  We could try to exclude # after
        // [|&;<>] but that doesn't seem to cause many major problems.
        // If that does turn out to be a problem, we should change the below
        // when hc is truthy to include # in the run of punctuation characters
        // only when not followint [|&;<>].
        var punctuation = "^.[^\\s\\w.$@'\"`/\\\\]*"
        if (Util.getVariableValueAsBoolean(options["regexLiterals"])) {
            punctuation += "(?!\\s*/)"
        }
        fallthroughStylePatterns.add(
            listOf(
                PR_PUNCTUATION,
                Pattern.compile(punctuation),
                null
            )
        )
        return CreateSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns)
    }

    /** Maps language-specific file extensions to handlers.  */
    private var langHandlerRegistry: MutableMap<String, Any?> = HashMap()

    /** Register a language handler for the given file extensions.
     * @param handler a function from source code to a list
     * of decorations.  Takes a single argument job which describes the
     * state of the computation.   The single parameter has the form
     * `{
     * sourceCode: {string} as plain text.
     * decorations: {Array.<number|string>} an array of style classes
     * preceded by the position at which they start in
     * job.sourceCode in order.
     * The language handler should assigned this field.
     * basePos: {int} the position of source in the larger source chunk.
     * All positions in the output decorations array are relative
     * to the larger source chunk.
     * } `
     * @param fileExtensions
     */
    @Throws(Exception::class)
    internal fun registerLangHandler(handler: CreateSimpleLexer?, fileExtensions: List<String>) {
        var i = fileExtensions.size
        while (--i >= 0) {
            val ext = fileExtensions[i]
            if (langHandlerRegistry[ext] == null) {
                langHandlerRegistry[ext] = handler
            } else {
                throw Exception("cannot override language handler $ext")
            }
        }
    }

    /**
     * Register language handler. The clazz will not be instantiated
     * @param clazz the class of the language
     * @throws Exception cannot instantiate the object using the class,
     * or language handler with specified extension exist already
     */
    @Throws(Exception::class)
    fun register(clazz: Class<out Lang>,fileExtensions: List<String> = getFileExtensionsFromClass(clazz)) {
        var i = fileExtensions.size
        while (--i >= 0) {
            val ext = fileExtensions[i]
            langHandlerRegistry[ext] = clazz
        }
    }

    //todo this does not work
    @Throws(Exception::class)
    internal fun getFileExtensionsFromClass(clazz: Class<out Lang>): List<String> {
        val getExtensionsMethod = clazz.getMethod("getFileExtensions", null)
        return getExtensionsMethod.invoke(null, null) as List<String>
    }

    /**
     * Get the parser for the extension specified.
     * @param extension the file extension, if null, default parser will be returned
     * @param source the source code
     * @return the parser
     */
    fun langHandlerForExtension(extension: String?, source: String?): CreateSimpleLexer? {
        var extension = extension
        if (!(extension != null && langHandlerRegistry[extension] != null)) {
            // Treat it as markup if the first non whitespace character is a < and
            // the last non-whitespace character is a >.
            extension = if (Util.test(Pattern.compile("^\\s*<"), source)) "default-markup" else "default-code"
        }
        val handler = langHandlerRegistry[extension]
        return if (handler is CreateSimpleLexer) {
            handler
        } else {
            val _simpleLexer: CreateSimpleLexer
            try {
                val _lang = (handler as Class<Lang>).newInstance()
                _simpleLexer = CreateSimpleLexer(_lang.shortcutStylePatterns, _lang.fallthroughStylePatterns)
                val extendedLangs = _lang.extendedLangs
                for (_extendedLang in extendedLangs) {
                    register(_extendedLang.javaClass)
                }
                val fileExtensions = getFileExtensionsFromClass(handler)
                for (_extension in fileExtensions) {
                    langHandlerRegistry[_extension] = _simpleLexer
                }
            } catch (ex: Exception) {
                LOG.log(Level.SEVERE, null, ex)
                return null
            }
            _simpleLexer
        }
    }

    companion object {
        private val LOG = Logger.getLogger(Prettify::class.java.name)

        // Keyword lists for various languages.
        const val FLOW_CONTROL_KEYWORDS = "break,continue,do,else,for,if,return,while"
        const val C_KEYWORDS = (FLOW_CONTROL_KEYWORDS + "," + "auto,case,char,const,default,"
                + "double,enum,extern,float,goto,inline,int,long,register,short,signed,"
                + "sizeof,static,struct,switch,typedef,union,unsigned,void,volatile")
        const val COMMON_KEYWORDS = (C_KEYWORDS + "," + "catch,class,delete,false,import,"
                + "new,operator,private,protected,public,this,throw,true,try,typeof")
        const val CPP_KEYWORDS = (COMMON_KEYWORDS + "," + "alignof,align_union,asm,axiom,bool,"
                + "concept,concept_map,const_cast,constexpr,decltype,delegate,"
                + "dynamic_cast,explicit,export,friend,generic,late_check,"
                + "mutable,namespace,nullptr,property,reinterpret_cast,static_assert,"
                + "static_cast,template,typeid,typename,using,virtual,where")
        const val JAVA_KEYWORDS = (COMMON_KEYWORDS + ","
                + "abstract,assert,boolean,byte,extends,final,finally,implements,import,"
                + "instanceof,interface,null,native,package,strictfp,super,synchronized,"
                + "throws,transient")
        const val RUST_KEYWORDS = (FLOW_CONTROL_KEYWORDS + "," + "as,assert,const,copy,drop,"
                + "enum,extern,fail,false,fn,impl,let,log,loop,match,mod,move,mut,priv,"
                + "pub,pure,ref,self,static,struct,true,trait,type,unsafe,use")
        const val CSHARP_KEYWORDS = (JAVA_KEYWORDS + ","
                + "as,base,by,checked,decimal,delegate,descending,dynamic,event,"
                + "fixed,foreach,from,group,implicit,in,internal,into,is,let,"
                + "lock,object,out,override,orderby,params,partial,readonly,ref,sbyte,"
                + "sealed,stackalloc,string,select,uint,ulong,unchecked,unsafe,ushort,"
                + "var,virtual,where")
        const val COFFEE_KEYWORDS = ("all,and,by,catch,class,else,extends,false,finally,"
                + "for,if,in,is,isnt,loop,new,no,not,null,of,off,on,or,return,super,then,"
                + "throw,true,try,unless,until,when,while,yes")
        const val JSCRIPT_KEYWORDS = (COMMON_KEYWORDS + ","
                + "debugger,eval,export,function,get,null,set,undefined,var,with,"
                + "Infinity,NaN")
        const val PERL_KEYWORDS = ("caller,delete,die,do,dump,elsif,eval,exit,foreach,for,"
                + "goto,if,import,last,local,my,next,no,our,print,package,redo,require,"
                + "sub,undef,unless,until,use,wantarray,while,BEGIN,END")
        const val PYTHON_KEYWORDS = (FLOW_CONTROL_KEYWORDS + "," + "and,as,assert,class,def,del,"
                + "elif,except,exec,finally,from,global,import,in,is,lambda,"
                + "nonlocal,not,or,pass,print,raise,try,with,yield,"
                + "False,True,None")
        const val RUBY_KEYWORDS = (FLOW_CONTROL_KEYWORDS + "," + "alias,and,begin,case,class,"
                + "def,defined,elsif,end,ensure,false,in,module,next,nil,not,or,redo,"
                + "rescue,retry,self,super,then,true,undef,unless,until,when,yield,"
                + "BEGIN,END")
        const val SH_KEYWORDS = (FLOW_CONTROL_KEYWORDS + "," + "case,done,elif,esac,eval,fi,"
                + "function,in,local,set,then,until")
        const val ALL_KEYWORDS =
            (CPP_KEYWORDS + "," + CSHARP_KEYWORDS + "," + JSCRIPT_KEYWORDS + "," + PERL_KEYWORDS + ","
                    + PYTHON_KEYWORDS + "," + RUBY_KEYWORDS + "," + SH_KEYWORDS)
        val C_TYPES =
            Pattern.compile("^(DIR|FILE|vector|(de|priority_)?queue|list|stack|(const_)?iterator|(multi)?(set|map)|bitset|u?(int|float)\\d*)\\b")
        // token style names.  correspond to css classes
        /**
         * token style for a string literal
         */
        const val PR_STRING = "str"

        /**
         * token style for a keyword
         */
        const val PR_KEYWORD = "kwd"

        /**
         * token style for a comment
         */
        const val PR_COMMENT = "com"

        /**
         * token style for a type
         */
        const val PR_TYPE = "typ"

        /**
         * token style for a literal value.  e.g. 1, null, true.
         */
        const val PR_LITERAL = "lit"

        /**
         * token style for a punctuation string.
         */
        const val PR_PUNCTUATION = "pun"

        /**
         * token style for a plain text.
         */
        const val PR_PLAIN = "pln"

        /**
         * token style for an sgml tag.
         */
        const val PR_TAG = "tag"

        /**
         * token style for a markup declaration such as a DOCTYPE.
         */
        const val PR_DECLARATION = "dec"

        /**
         * token style for embedded source.
         */
        const val PR_SOURCE = "src"

        /**
         * token style for an sgml attribute name.
         */
        const val PR_ATTRIB_NAME = "atn"

        /**
         * token style for an sgml attribute value.
         */
        const val PR_ATTRIB_VALUE = "atv"

        /**
         * A class that indicates a section of markup that is not code, e.g. to allow
         * embedding of line numbers within code listings.
         */
        const val PR_NOCODE = "nocode"

        /**
         * A set of tokens that can precede a regular expression literal in
         * javascript
         * http://web.archive.org/web/20070717142515/http://www.mozilla.org/js/language/js20/rationale/syntax.html
         * has the full list, but I've removed ones that might be problematic when
         * seen in languages that don't support regular expression literals.
         *
         *
         * Specifically, I've removed any keywords that can't precede a regexp
         * literal in a syntactically legal javascript program, and I've removed the
         * "in" keyword since it's not a keyword in many languages, and might be used
         * as a count of inches.
         *
         *
         * The link above does not accurately describe EcmaScript rules since
         * it fails to distinguish between (a=++/b/i) and (a++/b/i) but it works
         * very well in practice.
         */
        private const val REGEXP_PRECEDER_PATTERN =
            "(?:^^\\.?|[+-]|[!=]=?=?|\\#|%=?|&&?=?|\\(|\\*=?|[+\\-]=|->|\\/=?|::?|<<?=?|>>?>?=?|,|;|\\?|@|\\[|~|\\{|\\^\\^?=?|\\|\\|?=?|break|case|continue|delete|do|else|finally|instanceof|return|throw|try|typeof)\\s*"

        /**
         * Apply the given language handler to sourceCode and add the resulting
         * decorations to out.
         * @param basePos the index of sourceCode within the chunk of source
         * whose decorations are already present on out.
         */
        protected fun appendDecorations(
            basePos: Int,
            sourceCode: String?,
            langHandler: CreateSimpleLexer?,
            out: MutableList<Any>
        ) {
            if (sourceCode == null) {
                throw NullPointerException("argument 'sourceCode' cannot be null")
            }
            val job = Job()
            job.setSourceCode(sourceCode)
            job.basePos = basePos
            langHandler!!.decorate(job)
            out.addAll(job.getDecorations())
        }
    }

    // CAVEAT: this does not properly handle the case where a regular
    // expression immediately follows another since a regular expression may
    // have flags for case-sensitivity and the like.  Having regexp tokens
    // adjacent is not valid in any language I'm aware of, so I'm punting.
    // TODO: maybe style special characters inside a regexp as punctuation.
    init {
        try {
            var decorateSourceMap: MutableMap<String?, Any?> = HashMap()
            decorateSourceMap["keywords"] = ALL_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["cStyleComments"] = true
            decorateSourceMap["multiLineStrings"] = true
            decorateSourceMap["regexLiterals"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("default-code"))
            var shortcutStylePatterns: MutableList<List<Any?>> = ArrayList()
            var fallthroughStylePatterns: MutableList<List<Any?>> = ArrayList()
            fallthroughStylePatterns.add(listOf(PR_PLAIN, Pattern.compile("^[^<?]+")))
            fallthroughStylePatterns.add(
                listOf(
                    PR_DECLARATION, Pattern.compile("^<!\\w[^>]*(?:>|$)")
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    PR_COMMENT, Pattern.compile("^<\\!--[\\s\\S]*?(?:-\\->|$)")
                )
            )
            // Unescaped content in an unknown language
            fallthroughStylePatterns.add(
                listOf(
                    "lang-", Pattern.compile("^<\\?([\\s\\S]+?)(?:\\?>|$)")
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-", Pattern.compile("^<%([\\s\\S]+?)(?:%>|$)")
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    PR_PUNCTUATION, Pattern.compile("^(?:<[%?]|[%?]>)")
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-", Pattern.compile("^<xmp\\b[^>]*>([\\s\\S]+?)<\\/xmp\\b[^>]*>", Pattern.CASE_INSENSITIVE)
                )
            )
            // Unescaped content in javascript.  (Or possibly vbscript).
            fallthroughStylePatterns.add(
                listOf(
                    "lang-js",
                    Pattern.compile("^<script\\b[^>]*>([\\s\\S]*?)(<\\/script\\b[^>]*>)", Pattern.CASE_INSENSITIVE)
                )
            )
            // Contains unescaped stylesheet content
            fallthroughStylePatterns.add(
                listOf(
                    "lang-css",
                    Pattern.compile("^<style\\b[^>]*>([\\s\\S]*?)(<\\/style\\b[^>]*>)", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-in.tag", Pattern.compile("^(<\\/?[a-z][^<>]*>)", Pattern.CASE_INSENSITIVE)
                )
            )
            registerLangHandler(
                CreateSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns),
                listOf("default-markup", "htm", "html", "mxml", "xhtml", "xml", "xsl")
            )
            shortcutStylePatterns = ArrayList()
            fallthroughStylePatterns = ArrayList()
            shortcutStylePatterns.add(
                listOf(
                    PR_PLAIN,
                    Pattern.compile("^[\\s]+"),
                    null,
                    " \t\r\n"
                )
            )
            shortcutStylePatterns.add(
                listOf(
                    PR_ATTRIB_VALUE,
                    Pattern.compile("^(?:\\\"[^\\\"]*\\\"?|\\'[^\\']*\\'?)"),
                    null,
                    "\"'"
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    PR_TAG, Pattern.compile("^^<\\/?[a-z](?:[\\w.:-]*\\w)?|\\/?>$", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    PR_ATTRIB_NAME,
                    Pattern.compile("^(?!style[\\s=]|on)[a-z](?:[\\w:-]*\\w)?", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-uq.val", Pattern.compile(
                        "^=\\s*([^>\\'\\\"\\s]*(?:[^>\\'\\\"\\s\\/]|\\/(?=\\s)))",
                        Pattern.CASE_INSENSITIVE
                    )
                )
            )
            fallthroughStylePatterns.add(listOf(PR_PUNCTUATION, Pattern.compile("^[=<>\\/]+")))
            fallthroughStylePatterns.add(
                listOf(
                    "lang-js", Pattern.compile("^on\\w+\\s*=\\s*\\\"([^\\\"]+)\\\"", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-js", Pattern.compile("^on\\w+\\s*=\\s*\\'([^\\']+)\\'", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-js", Pattern.compile("^on\\w+\\s*=\\s*([^\\\"\\'>\\s]+)", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-css", Pattern.compile("^style\\s*=\\s*\\\"([^\\\"]+)\\\"", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-css", Pattern.compile("^style\\s*=\\s*\\'([^\\']+)\\'", Pattern.CASE_INSENSITIVE)
                )
            )
            fallthroughStylePatterns.add(
                listOf(
                    "lang-css", Pattern.compile("^style\\s*=\\s\\*([^\\\"\\'>\\s]+)", Pattern.CASE_INSENSITIVE)
                )
            )
            registerLangHandler(
                CreateSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns),
                listOf("in.tag")
            )
            shortcutStylePatterns = ArrayList()
            fallthroughStylePatterns = ArrayList()
            fallthroughStylePatterns.add(listOf(PR_ATTRIB_VALUE, Pattern.compile("^[\\s\\S]+")))
            registerLangHandler(
                CreateSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns),
                listOf("uq.val")
            )
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = CPP_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["cStyleComments"] = true
            decorateSourceMap["types"] = C_TYPES
            registerLangHandler(
                sourceDecorator(decorateSourceMap),
                listOf("c", "cc", "cpp", "cxx", "cyc", "m")
            )
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = "null,true,false"
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("json"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = CSHARP_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["cStyleComments"] = true
            decorateSourceMap["verbatimStrings"] = true
            decorateSourceMap["types"] = C_TYPES
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("cs"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = JAVA_KEYWORDS
            decorateSourceMap["cStyleComments"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("java"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = SH_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["multiLineStrings"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("bash", "bsh", "csh", "sh"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = PYTHON_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["multiLineStrings"] = true
            decorateSourceMap["tripleQuotedStrings"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("cv", "py", "python"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = PERL_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["multiLineStrings"] = true
            decorateSourceMap["regexLiterals"] = 2 // multiline regex literals
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("perl", "pl", "pm"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = RUBY_KEYWORDS
            decorateSourceMap["hashComments"] = true
            decorateSourceMap["multiLineStrings"] = true
            decorateSourceMap["regexLiterals"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("rb", "ruby"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = JSCRIPT_KEYWORDS
            decorateSourceMap["cStyleComments"] = true
            decorateSourceMap["regexLiterals"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("javascript", "js"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = COFFEE_KEYWORDS
            decorateSourceMap["hashComments"] = 3 // ### style block comments
            decorateSourceMap["cStyleComments"] = true
            decorateSourceMap["multilineStrings"] = true
            decorateSourceMap["tripleQuotedStrings"] = true
            decorateSourceMap["regexLiterals"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), listOf("coffee"))
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = RUST_KEYWORDS
            decorateSourceMap["cStyleComments"] = true
            decorateSourceMap["multilineStrings"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), Arrays.asList("rc", "rs", "rust"))
            shortcutStylePatterns = ArrayList()
            fallthroughStylePatterns = ArrayList()
            fallthroughStylePatterns.add(Arrays.asList(PR_STRING, Pattern.compile("^[\\s\\S]+")))
            registerLangHandler(
                CreateSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns),
                Arrays.asList("regex")
            )
            /**
             * Registers a language handler for Protocol Buffers as described at
             * http://code.google.com/p/protobuf/.
             *
             * Based on the lexical grammar at
             * http://research.microsoft.com/fsharp/manual/spec2.aspx#_Toc202383715
             *
             * @author mikesamuel@gmail.com
             */
            decorateSourceMap = HashMap()
            decorateSourceMap["keywords"] = ("bytes,default,double,enum,extend,extensions,false,"
                    + "group,import,max,message,option,"
                    + "optional,package,repeated,required,returns,rpc,service,"
                    + "syntax,to,true")
            decorateSourceMap["types"] = Pattern.compile("^(bool|(double|s?fixed|[su]?int)(32|64)|float|string)\\b")
            decorateSourceMap["cStyleComments"] = true
            registerLangHandler(sourceDecorator(decorateSourceMap), Arrays.asList("proto"))
            register(LangAppollo::class.java, fileExtensions = LangAppollo.fileExtensions)
            register(LangBasic::class.java,fileExtensions = LangBasic.fileExtensions)
            register(LangClj::class.java,fileExtensions = LangClj.fileExtensions)
            register(LangCss::class.java,fileExtensions = LangCss.fileExtensions)
            register(LangDart::class.java,fileExtensions = LangDart.fileExtensions)
            register(LangErlang::class.java,fileExtensions = LangErlang.fileExtensions)
            register(LangGo::class.java,fileExtensions = LangGo.fileExtensions)
            register(LangHs::class.java,fileExtensions = LangHs.fileExtensions)
            register(LangLisp::class.java,fileExtensions = LangLisp.fileExtensions)
            register(LangLlvm::class.java,fileExtensions = LangLlvm.fileExtensions)
            register(LangLua::class.java,fileExtensions = LangLua.fileExtensions)
            register(LangMatlab::class.java,fileExtensions = LangMatlab.fileExtensions)
            register(LangMd::class.java,fileExtensions = LangMd.fileExtensions)
            register(LangMl::class.java,fileExtensions = LangMl.fileExtensions)
            register(LangMumps::class.java,fileExtensions = LangMumps.fileExtensions)
            register(LangN::class.java,fileExtensions = LangN.fileExtensions)
            register(LangPascal::class.java,fileExtensions = LangPascal.fileExtensions)
            register(LangR::class.java,fileExtensions = LangR.fileExtensions)
            register(LangRd::class.java,fileExtensions = LangRd.fileExtensions)
            register(LangScala::class.java,fileExtensions = LangScala.fileExtensions)
            register(LangSql::class.java,fileExtensions = LangSql.fileExtensions)
            register(LangTex::class.java,fileExtensions = LangTex.fileExtensions)
            register(LangVb::class.java,fileExtensions = LangVb.fileExtensions)
            register(LangVhdl::class.java,fileExtensions = LangVhdl.fileExtensions)
            register(LangTcl::class.java,fileExtensions = LangTcl.fileExtensions)
            register(LangWiki::class.java,fileExtensions = LangWiki.fileExtensions)
            register(LangXq::class.java,fileExtensions = LangXq.fileExtensions)
            register(LangYaml::class.java,fileExtensions = LangYaml.fileExtensions)
        } catch (ex: Exception) {
            LOG.log(Level.SEVERE, null, ex)
        }
    }
}