package com.muralex.achiever.presentation.fragments.group_detail

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ItemAnimator : DefaultItemAnimator() {
    override fun animateChange (
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {
        return if (oldHolder is ItemsListAdapter.GroupDetailViewHolder ) {
            dispatchChangeFinished(oldHolder, true)
            dispatchChangeFinished(newHolder, false)
            false
        } else {
            super.animateChange(oldHolder, newHolder, fromLeft, fromTop, toLeft, toTop)
        }
    }

}