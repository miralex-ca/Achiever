package com.muralex.achiever.presentation.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.google.android.material.textfield.TextInputEditText


fun <T> List<T>.safeSlice(maxSize: Int) : MutableList<T> {
    val limit = if (maxSize > this.size) this.size else maxSize
    val maxIndex = if (limit > 0) limit else 0


    return this.subList(0, maxIndex).toMutableList()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


@SuppressLint("ClickableViewAccessibility")
fun TextInputEditText.setScrollable() {
    setOnTouchListener { v, event ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> {
                v.parent.requestDisallowInterceptTouchEvent(false)
                false
            }
            else -> false
        }
    }

}

