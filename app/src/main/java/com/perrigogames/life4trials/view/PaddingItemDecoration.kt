package com.perrigogames.life4trials.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * A [RecyclerView.ItemDecoration] designed to easily construct with simple padding values.
 */
class PaddingItemDecoration(var left: Int, var top: Int, var right: Int, var bottom: Int): RecyclerView.ItemDecoration() {

    constructor(size: Int): this(size, size, size, size)

    constructor(horiz: Int, vert: Int): this(horiz, vert, horiz, vert)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(left, top, right, bottom)
    }
}