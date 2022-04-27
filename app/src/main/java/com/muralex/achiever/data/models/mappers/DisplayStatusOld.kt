package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_DUE_DATE
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO_NEW
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_MANUAL
import timber.log.Timber
import java.util.*

class DisplayStatusOld {

    private var status = ""
    private var dueDate: Long = 0

    fun calculate(data: DataItem): String {

        status = data.status

        if (data.status == ITEM_STATUS[1] || data.status == ITEM_STATUS[2] || manualModeIsEnabled(
                data)
        )
            return data.status // return if urgent inactive or completed or manual

        if (data.completionType == COMPLETION_TYPE_DUE_DATE && data.completion > 0 && autoModeIsEnabled(
                data)
        ) {
            dueDate = data.completion
            if (isOverdue(dueDate, status)) setUrgentStatus()
        }

        if (data.completionType == Constants.COMPLETION_TYPE_REPEATED && data.repeatStart > 0 && autoModeIsEnabled(
                data)
        ) {

            val currentTime = System.currentTimeMillis()
            val repeatStartTime = data.repeatStart
            val completedTime = data.completedTime
            val repeatPeriodDuration = getRepeatPeriodDuration(data)


            val statusAllPeriod = currentTime - repeatStartTime
            val currentRepeatPeriodDuration = statusAllPeriod % repeatPeriodDuration
            val currentRepeatPeriodStart = currentTime - currentRepeatPeriodDuration

            val completedBeforeLastPeriod = completedTime < (repeatStartTime - repeatPeriodDuration)


            val cal = Calendar.getInstance()
            if (completedBeforeLastPeriod) {  /// then the task is overdue anyway
                cal.timeInMillis = repeatStartTime + repeatPeriodDuration - (60 * 1000)
            } else {
                cal.timeInMillis = completedTime
                setEndOfDay(cal) /// set the date of completion
                cal.timeInMillis = cal.timeInMillis + repeatPeriodDuration
            }

            setEndOfDay(cal)
            dueDate = cal.timeInMillis


            if (currentTime < repeatStartTime) {
                setInactiveStatus()  /// for tasks in future
            } else {

                var startingProgressTime = currentRepeatPeriodStart

                if (!completedBeforeLastPeriod) {
                    startingProgressTime = dueDate
                }

                val currentProgressDuration = currentTime - startingProgressTime

                var progress =
                    (  (repeatPeriodDuration - currentProgressDuration) * 100 / repeatPeriodDuration).toInt()

                if (completedBeforeLastPeriod) progress = 5

                Timber.d("progress: ${data.title} - ${progress} %")

                status = setStatusByProgress(progress)

                if (isOverdue(dueDate, status)) setUrgentStatus()


//                if (completedInCurrentPeriod && data.status == ITEM_STATUS[5]) {
//                    status = ITEM_STATUS[5]   /// set completed if done in current period
//
//                } else if (completedInLastPeriod || firstRepeatPeriod) {
//
//                    val progress = (currentRepeatPeriodDuration * 100 / repeatPeriodDuration).toInt()
//
//                    status = setStatusByProgress(progress)
//
//
//                } else {
//
//                    if (!firstRepeatPeriod || data.status != ITEM_STATUS[5] ) {
//
//                        if (data.statusMode == ITEM_STATUS_MODE_AUTO) {
//                            status = ITEM_STATUS[1]
//                        } else if (data.statusMode == ITEM_STATUS_MODE_AUTO_NEW) {
//                            status = ITEM_STATUS[0]
//                        }
//                    }
//                }
            }
        }

        return status
    }

    private fun setInactiveStatus() {
        status = ITEM_STATUS[2]
    }

    private fun setUrgentStatus() {
        status = ITEM_STATUS[1]
    }

    fun getDueDate(): Long = dueDate


    private fun setStatusByProgress(progress: Int): String {
        return when {
            progress > 91 -> ITEM_STATUS[5] // status completed
            progress in 45..90 -> ITEM_STATUS[4] // status in progress
            progress in 18..44 -> ITEM_STATUS[3] // status started
            progress in 5..17 -> ITEM_STATUS[0]  // status new
            else -> {
                ITEM_STATUS[1] // status urgent
            }
        }
    }

    private fun getRepeatPeriodDuration(data: DataItem) =
        try {
            processPeriod(data.repeat) * 1000
        } catch (e: Exception) {
            1
        }

    private fun isOverdue(dueDate: Long, status: String): Boolean {
        var overdue = false
        val currentTime = System.currentTimeMillis()
        if (currentTime > dueDate && status != ITEM_STATUS[5]) {
            overdue = true
        }
        return overdue
    }

    private fun autoModeIsEnabled(data: DataItem) =
        (data.statusMode == ITEM_STATUS_MODE_AUTO || data.statusMode == ITEM_STATUS_MODE_AUTO_NEW)

    private fun manualModeIsEnabled(data: DataItem) =
        data.statusMode == ITEM_STATUS_MODE_MANUAL

}
