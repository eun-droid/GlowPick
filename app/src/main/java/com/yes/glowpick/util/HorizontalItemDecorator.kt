package com.yes.glowpick.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecorator(private val heightDp: Int) : RecyclerView.ItemDecoration() {

    @Override
    override fun getItemOffsets(outRect: Rect, view: View, parent : RecyclerView, state : RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val heightPx = heightDp.toPx
        val curPosition = parent.getChildAdapterPosition(view)

        if (curPosition == 0) {
            outRect.left = (heightPx * 2).toInt()
        }

        if (curPosition == state.itemCount - 1) {
            outRect.right = (heightPx * 2).toInt()
        } else {
            outRect.right = heightPx.toInt()
        }
    }
}