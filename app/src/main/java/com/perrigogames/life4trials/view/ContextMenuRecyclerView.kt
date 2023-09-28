package com.perrigogames.life4trials.view

import android.content.Context
import android.util.AttributeSet
import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ContextMenuRecyclerView @JvmOverloads constructor(context: Context,
                                                        attrs: AttributeSet? = null,
                                                        defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    private var mContextMenuInfo: RecyclerViewContextMenuInfo? = null

    override fun getContextMenuInfo(): ContextMenu.ContextMenuInfo? {
        return mContextMenuInfo
    }

    override fun showContextMenuForChild(originalView: View): Boolean {
        val longPressPosition = getChildAdapterPosition(originalView)
        if (longPressPosition >= 0) {
            val longPressId = adapter!!.getItemId(longPressPosition)
            mContextMenuInfo = RecyclerViewContextMenuInfo(longPressPosition, longPressId)
            return super.showContextMenuForChild(originalView)
        }
        return false
    }

    class RecyclerViewContextMenuInfo(val position: Int, val id: Long) : ContextMenu.ContextMenuInfo
}