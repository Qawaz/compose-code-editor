package com.wakaztahir.codeeditor.prettify

import com.wakaztahir.codeeditor.parser.ParseResult
import com.wakaztahir.codeeditor.parser.Parser
import com.wakaztahir.codeeditor.prettify.parser.Job
import com.wakaztahir.codeeditor.prettify.parser.Prettify
import java.util.*

/**
 * The prettify parser for syntax highlight.
 * @author Chan Wai Shing <cws1989></cws1989>@gmail.com>
 */
class PrettifyParser : Parser {
    /**
     * The prettify parser.
     */
    private var prettify: Prettify = Prettify()

    override fun parse(fileExtension: String?, content: String?): List<ParseResult> {
        val job = Job(0, content)
        prettify.langHandlerForExtension(fileExtension, content)?.decorate(job)
        val decorations = job.decorations
        val returnList: MutableList<ParseResult> = ArrayList()

        // apply style according to the style list
        var i = 0
        val iEnd = decorations.size
        while (i < iEnd) {
            val endPos = if (i + 2 < iEnd) decorations[i + 2] as Int else content?.length
            val startPos = decorations[i] as Int
            if (endPos != null) {
                returnList.add(
                    ParseResult(
                        startPos, endPos - startPos, Arrays.asList(
                            (
                                decorations[i + 1] as String
                            )
                        )
                    )
                )
            }
            i += 2
        }
        return returnList
    }
}