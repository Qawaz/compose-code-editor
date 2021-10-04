// Copyright (C) 2011 Chan Wai Shing
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
import java.util.ArrayList

/**
 * Lang class for Java Prettify.
 * Note that the method [.getFileExtensions] should be overridden.
 *
 * @author Chan Wai Shing <cws1989></cws1989>@gmail.com>
 */
abstract class Lang {
    /**
     * Similar to those in JavaScript prettify.js.
     */
    internal var shortcutStylePatterns: List<List<Any?>>

    /**
     * Similar to those in JavaScript prettify.js.
     */
    internal var fallthroughStylePatterns: List<List<Any?>>

    /**
     * See [LangCss] for example.
     */
    internal var extendedLangs: List<Lang>

    fun getShortcutStylePatterns(): List<List<Any?>> {
        val returnList: MutableList<List<Any?>> = ArrayList()
        for (shortcutStylePattern in shortcutStylePatterns) {
            returnList.add(ArrayList(shortcutStylePattern))
        }
        return returnList
    }

    fun setShortcutStylePatterns(shortcutStylePatterns: List<List<Any?>>) {
        val cloneList: MutableList<List<Any?>> = ArrayList()
        for (shortcutStylePattern in shortcutStylePatterns) {
            cloneList.add(ArrayList(shortcutStylePattern))
        }
        this.shortcutStylePatterns = cloneList
    }

    fun getFallthroughStylePatterns(): List<List<Any?>> {
        val returnList: MutableList<List<Any?>> = ArrayList()
        for (fallthroughStylePattern in fallthroughStylePatterns) {
            returnList.add(ArrayList(fallthroughStylePattern))
        }
        return returnList
    }

    fun setFallthroughStylePatterns(fallthroughStylePatterns: List<List<Any?>>) {
        val cloneList: MutableList<List<Any?>> = ArrayList()
        for (fallthroughStylePattern in fallthroughStylePatterns) {
            cloneList.add(ArrayList(fallthroughStylePattern))
        }
        this.fallthroughStylePatterns = cloneList
    }

    /**
     * Get the extended languages list.
     * @return the list
     */
    fun getExtendedLangs(): List<Lang> {
        return ArrayList(extendedLangs)
    }

    /**
     * Set extended languages. Because we cannot register multiple languages
     * within one [prettify.lang.Lang], so it is used as an solution. See
     * [prettify.lang.LangCss] for example.
     *
     * @param extendedLangs the list of [prettify.lang.Lang]s
     */
    fun setExtendedLangs(extendedLangs: List<Lang>?) {
        this.extendedLangs = ArrayList(extendedLangs)
    }

    companion object {
        /**
         * This method should be overridden by the child class.
         * This provide the file extensions list to help the parser to determine which
         * [Lang] to use. See JavaScript prettify.js.
         *
         * @return the list of file extensions
         */
        val fileExtensions: List<String>
            get() = ArrayList()
    }

    abstract fun getFileExtensions() : List<String>

    /**
     * Constructor.
     */
    init {
        shortcutStylePatterns = ArrayList()
        fallthroughStylePatterns = ArrayList()
        extendedLangs = ArrayList()
    }
}