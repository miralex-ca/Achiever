package com.muralex.achiever.presentation.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.muralex.achiever.R
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.data.models.usemodels.ItemInGroup
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.REPEAT_PERIODS
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit


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

fun View.displayIf(visible: Boolean) {
    if (visible) visibility = View.VISIBLE
    else visibility = View.GONE
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

fun DataItem.changePinned(pinned: Int) : DataItem {
    return this.copy(pinned = pinned, pinnedTime = System.currentTimeMillis())
}

fun TextView.defineDescText(item: ItemInGroup?)  {
    item?.let {
       if (it.desc.trim().isNotEmpty()) {
           setTexNoLine(it.desc)
           visible()
       } else {
           gone()
       }
    }
}

fun TextView.setDueDateText(item: ItemInGroup?)  {

    item?.let {
        var desc = ""
        val dueDate = item.dueDate

        when {
            dueDate > 0 -> {
                desc = try {
                    if ( it.schedule.dueToday ) {
                        setTypeface(null, Typeface.NORMAL)
                        setTextColor(Color.parseColor("#FF9800"));
                    } else {
                        //setTypeface(null, Typeface.NORMAL)
                    }

                    context.getString(R.string.desc_due_date) + it.schedule.dueDateText

                } catch (e: Exception) {
                    ""
                }
            }
        }

        text = desc
    }
}

fun TextView.setCompletedText(item: ItemInGroup?)  {
    item?.let {
        text = context.getString(R.string.task_completed, it.schedule.completedDateText )
    }
}

fun TextView.setNextDateText(item: ItemInGroup?)  {
    item?.let {
        text = context.getString(R.string.task_next_date, it.schedule.nextDateText )
    }
}


fun TextView.checkDescVisibility() {
    if (text.isEmpty()) gone()
    else visible()
}

fun setEndOfDay(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 0)
}

fun setStartOfDay(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
}

fun timeToStartOfTheDay(time: Long) : Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    setStartOfDay(calendar)
    return calendar.timeInMillis
}

fun timeToEndOfTheDay(time: Long) : Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    setEndOfDay(calendar)
    return calendar.timeInMillis
}


fun processPeriod(repeat: String) : Long {

    val data = repeat.split("_")
    val count = data[0].toInt()
    val period  = data[1]

    val time = when (REPEAT_PERIODS.indexOf(period)) {
        0 -> {
            count * 60 * 60 * 24
        }
        1 -> count * 60 * 60 * 24 * 7
        2 -> count * 60 * 60 * 24 * 30
        3 -> count * 60 * 60 * 24 * 365
        else -> {
            0
        }
    }

    return time.toLong()
}

fun processPeriodString(repeat: String, periodValues: Array<String> ) : String {
    val data = repeat.split("_")

    val count = data[0].toInt()
    val period  = data[1]

    val index = REPEAT_PERIODS.indexOf(period)
    val periodText = periodValues[index]

    return  "Every $count $periodText"
}


fun convertStatusToInt(status: String): Int {
    var statusInt = 0

    when (status) {
        ITEM_STATUS[0] -> statusInt = 1
        ITEM_STATUS[1] -> statusInt = 0
        ITEM_STATUS[2] -> statusInt = 150 /// inactive
        ITEM_STATUS[3] -> statusInt = 20
        ITEM_STATUS[4] -> statusInt = 50
        ITEM_STATUS[5] -> statusInt = 90
    }
    return statusInt
}


fun convertStatusToProgressInt(status: String): Int {
    var statusInt = 0
    when (status) {
        ITEM_STATUS[3] -> statusInt = 10
        ITEM_STATUS[4] -> statusInt = 60
        ITEM_STATUS[5] -> statusInt = 100
    }
    return statusInt
}


fun TextInputLayout.setFieldArrow() {
    endIconDrawable =
        ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_drop_up, null)
}

fun TextInputLayout.resetFieldArrow() {
    endIconDrawable =
        ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_drop_down, null)
}


fun ImageView.setListSwipeArchiveIcon(item: GroupData?) {
    item?.let {
        var icon = R.drawable.ic_action_archive_swipe
        item.group?.let {
            if (it.archived  > 0) {
                icon = R.drawable.ic_action_unarchive_swipe
            }
        }
        setImageResource(icon)
    }
}

fun TextView.setArchiveText(item: GroupData?) {
    item?.let {
        text = context.getString(R.string.archive_text)
        item.group?.let {
            if (it.archived  > 0) {
                text = context.getString(R.string.unarchive_text)
            }
        }
    }
}

fun TextView.setGroupProgressText(item: GroupData?) {
    item?.let {
        text = context.getString(R.string.group_progress, it.progress.toString())
    }
}

fun TextView.setGroupTotalText(item: GroupData?) {
    item?.let {
        text = context.getString(R.string.group_total, it.totalItemsCount.toString())
    }
}

fun TextView.setGroupCompletedText(item: GroupData?) {
    item?.let {
        text = context.getString(R.string.group_completed, it.completedItems.toString())
    }
}

fun TextView.setGroupCompletedTotalText(item: GroupData?) {
    item?.let {
        text = context.getString(R.string.group_completed_total, it.completedItems.toString(),it.activeItems.toString() )
    }
}

fun checkAutoHintDisplay(data: DataItem, view: View) {
    val statusMode = data.statusMode
    val completionType = data.completionType

    if ( statusMode != Constants.ITEM_STATUS_MODE_MANUAL && completionType != Constants.COMPLETION_TYPE_NO_DATE) {
        view.visible()
    } else {
        view.gone()
    }
}


@SuppressLint("SimpleDateFormat")
fun convertMillisToTime(date: Long) : String {
    val formatter =  SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    return  formatter.format( date )
}




