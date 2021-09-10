package com.wakaztahir.codeeditor.adapters

import android.content.Context
import com.wakaztahir.codeeditor.views.DiffModel
import com.wakaztahir.codeeditor.views.LineDiffView

/**
 * @class CodeWithDiffsAdapter
 *
 * Code content adapter with ability to add diffs (additions & deletions) in footer.
 *
 * @author Kirill Biakov
 */
open class CodeWithDiffsAdapter : AbstractCodeAdapter<DiffModel> {

    constructor(context: Context) : super(context)

    constructor(context: Context, code: String) : super(context, code)

    constructor(context: Context, options: Options) : super(context, options)

    /**
     * Create footer view.
     *
     * @param entity Note content
     * @param isFirst Is first footer
     */
    override fun createFooter(context: Context, entity: DiffModel, isFirst: Boolean) =
            LineDiffView.create(context, entity)
}
