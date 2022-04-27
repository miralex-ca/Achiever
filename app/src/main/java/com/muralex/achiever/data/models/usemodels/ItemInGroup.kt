package com.muralex.achiever.data.models.usemodels

import android.text.format.DateUtils
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.isRepeated
import com.muralex.achiever.data.models.datamodels.isScheduled
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class ItemInGroup(
    val id: String,
    val title: String,
    val desc: String,
    val sortByStatus: Int,
    val displayStatus: String,
    val dueDate: Long = 0,
    val data: DataItem,
    var schedule: TaskScheduleDisplay,
    val nextDueDate: Long = 0,

    )


data class TaskScheduleDisplay(
    var isScheduled: Boolean = false
) {

    var isRepeated: Boolean = false
    var dueToday: Boolean = false
    var displayCompletedTime: Boolean = false
    var displayDueDate: Boolean = true
    var displayNextDate: Boolean = false
    var completedDateText: String = ""
    var dueDateText: String = ""
    var nextDateText: String = ""
    var displayDateSpace: Boolean = false

    init {
        reset()
    }

    private fun reset() {
        isScheduled = false
        isRepeated = false
        displayCompletedTime = false
        dueToday = false
        displayDueDate = true
        completedDateText = ""
        dueDateText = ""
        displayDateSpace = false

    }

    fun calculate(itemInGroup: ItemInGroup): TaskScheduleDisplay {
        reset()
        isScheduled = checkScheduled(itemInGroup.data)
        isRepeated = checkRepeated(itemInGroup.data)
        dueToday = isDueToday(itemInGroup)

        completedDateText = itemInGroup.completeDateString()
        dueDateText = itemInGroup.getDueDateString()
        nextDateText = getNextDateString(itemInGroup)

        displayCompletedTime = checkCompletedDisplay(itemInGroup)

        return this
    }


    private fun checkCompletedDisplay(itemInGroup: ItemInGroup): Boolean {
        var display = false

        if (itemInGroup.displayCompletedStatus()) {
            display = true
            displayDueDate = false
            if (isRepeated) displayNextDate = true
        }

        if (itemInGroup.dueDate < System.currentTimeMillis() && !dueToday && isRepeated ) {
            dueDateText = getDateText(itemInGroup.dueDate)
            if (displayDueDate) displayDateSpace = true
            displayNextDate = true
        }

        if (itemInGroup.data.completedTime < 1) display = false
        if (itemInGroup.nextDueDate < 1) displayNextDate = false

        return display
    }


    private fun checkScheduled(data: DataItem) = data.isScheduled()
    private fun checkRepeated(data: DataItem) = data.isRepeated()

    private fun isDueToday(item: ItemInGroup): Boolean {
        var dueToday = false
        if (DateUtils.isToday(item.dueDate) && !item.displayUrgentStatus()) dueToday = true
        if (item.displayUrgentStatus()) dueToday = false
        if (item.displayInactiveStatus()) dueToday = false
        if (item.displayCompletedStatus()) dueToday = false

        return dueToday
    }

    private fun ItemInGroup.completeDateString(): String {
        return if (!isRepeated) getMediumDateString(dueDate)
        else getDateText(data.completedTime)
    }

    private fun ItemInGroup.getDueDateString(): String {
        return getMediumDateString(this.dueDate)
    }

    private fun getMediumDateString(time: Long): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return dateFormat.format(time)
    }

    private fun getNextDateString(itemGroup: ItemInGroup): String {
        return getDateText(itemGroup.nextDueDate)
    }
}


private fun ItemInGroup.displayUrgentStatus(): Boolean {
    return displayStatus == ITEM_STATUS[1]
}

private fun ItemInGroup.displayInactiveStatus(): Boolean {
    return displayStatus == ITEM_STATUS[2]
}

private fun ItemInGroup.displayCompletedStatus(): Boolean {
    return displayStatus == ITEM_STATUS[5]
}

fun getDateText(time: Long): String {
    val date: String = if (isThisYear(time)) {
        getShortestDateText(time)
    } else {
        getShortDateWithYearText(time)
    }
    return date
}

fun getShortestDateText(time: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
    return dateFormat.format(time)
}

fun getShortDateWithYearText(time: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
    return dateFormat.format(time)
}


fun isThisYear(time: Long): Boolean {
    val currentTime: Calendar = Calendar.getInstance()
    currentTime.timeInMillis = System.currentTimeMillis()
    val comparedTime: Calendar = Calendar.getInstance()
    comparedTime.timeInMillis = time
    return (currentTime.get(Calendar.YEAR) == comparedTime.get(Calendar.YEAR))
}

fun isTheSameDay(time: Long, anotherTime: Long): Boolean {
    val firstTime: Calendar = Calendar.getInstance()
    firstTime.timeInMillis = time
    val comparedTime: Calendar = Calendar.getInstance()
    comparedTime.timeInMillis = anotherTime
    return (firstTime.get(Calendar.YEAR) == comparedTime.get(Calendar.YEAR))
            && (firstTime.get(Calendar.DAY_OF_YEAR) == comparedTime.get(Calendar.DAY_OF_YEAR))
}


