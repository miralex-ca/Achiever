package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.hasCompletedStatus
import com.muralex.achiever.data.models.datamodels.hasInactiveStatus
import com.muralex.achiever.data.models.datamodels.hasUrgentStatus
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_DUE_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED_AFTER
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEAT_PERIOD
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO_NEW
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_MANUAL
import com.muralex.achiever.presentation.utils.convertMillisToTime
import com.muralex.achiever.presentation.utils.processPeriod
import com.muralex.achiever.presentation.utils.timeToEndOfTheDay
import com.muralex.achiever.presentation.utils.timeToStartOfTheDay
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DisplayStatus @Inject constructor(
    private val currentTime: CurrentTime,
) {
    private var status = ""
    private var dueDate: Long = 0
    private var nextDueDate: Long = 0

    private fun reinitStats() {
        status = ""
        dueDate = 0
        nextDueDate = 0
    }

    fun calculate(data: DataItem): String {
        reinitStats()

        status = data.status



        if (data.completionType == COMPLETION_TYPE_DUE_DATE && data.completion > 0
        ) {
            dueDate = data.completion
            if (isOverdue(dueDate, status)) setUrgentStatus()
        }


        if (data.completionType == COMPLETION_TYPE_REPEATED ) {

            val statusTime = StatusTimeData(data, currentTime).apply {
                calculateRepeatDate()
            }

            dueDate = statusTime.dueDate
            nextDueDate = statusTime.nextDate

            if (data.repeatStart > statusTime.currentTime) {
                setInactiveStatus()
                return status
            }

            if (data.hasCompletedStatus() && statusTime.completedAfterRepeatStart ) {
                if (statusTime.completedInCurrentPeriod) return status

                /// first day of period is today
                if (statusTime.currentPeriod.first in statusTime.currentDate) {
                    if (statusTime.completedInPreviousPeriod) setNewStatus()
                    if (statusTime.completedBeforePreviousPeriod) {
                        setOverdueStatus(data)
                    }
                } else {
                    setOverdueStatus(data)
                }

            } else {
                /// not completed or completed before the repeat time start
                if (data.hasCompletedStatus()) setNewStatus()

                /// first day of period is today
                if ( statusTime.currentPeriod.first in statusTime.currentDate   ) {
                    // started earlier
                    if (data.repeatStart <  statusTime.currentPeriod.first) {
                        setOverdueStatus(data)
                    }
                }  else {
                    if (data.statusMode == ITEM_STATUS_MODE_AUTO) setUrgentStatus()
                }
            }
        }

        if (data.completionType == COMPLETION_TYPE_REPEAT_PERIOD ) {

            val statusTime = StatusTimeData(data, currentTime).apply {
                calculateRepeatTime()
            }

            dueDate = statusTime.dueDate
            nextDueDate = statusTime.nextDate

            if (data.repeatStart > statusTime.currentTime) {
                setInactiveStatus()
                return status
            }

            if (data.hasCompletedStatus() && statusTime.completedInCurrentPeriod) {
                return status
            } else {
                if (data.hasCompletedStatus()) {
                    if (statusTime.completedBeforePreviousPeriod) setUrgentStatus()
                    if (statusTime.completedInPreviousPeriod || statusTime.completedTimeUnknown) setNewStatus()
                } else {

                    // started earlier than current period
                    if (data.repeatStart <  statusTime.currentPeriod.first) {
                        setOverdueStatus(data)
                    }

                }
            }
        }


        if (data.completionType == COMPLETION_TYPE_REPEATED_AFTER ) {

            val statusTime = StatusTimeData(data, currentTime).apply {
                calculateRepeatAfterTime()
            }

            dueDate = statusTime.dueDate
            nextDueDate =  dueDate

            if (data.repeatStart > statusTime.currentTime) {
                setInactiveStatus()
                return status
            }
            val progress = calculateProgressFromStatusTime(data, statusTime)


            status = setStatusByProgress(progress, data.statusMode == ITEM_STATUS_MODE_AUTO)

            if (!statusTime.completedInPreviousPeriod && data.statusMode == ITEM_STATUS_MODE_AUTO_NEW)
                setNewStatus()
        }

        if (data.hasUrgentStatus() || data.hasInactiveStatus() || manualModeIsEnabled(data))
            return data.status

        return status
    }

    private fun setOverdueStatus(data: DataItem) {
        if (data.statusMode == ITEM_STATUS_MODE_AUTO_NEW) setNewStatus()
        else setUrgentStatus()
    }

    private fun calculateProgressFromStatusTime(data: DataItem, statusTime: StatusTimeData): Int {

        var progress = 0
       // var duration: Long = 0

        if (data.hasCompletedStatus() && statusTime.completedInPreviousPeriod) {
            val startingProgressTime = statusTime.currentPeriod.first
            val currentProgressDuration = statusTime.currentTime - startingProgressTime
            var repeatPeriodDuration = statusTime.repeatPeriodDuration
            if (repeatPeriodDuration.toInt() == 0) repeatPeriodDuration = 1
            progress =
                ((repeatPeriodDuration - currentProgressDuration) * 100 / repeatPeriodDuration).toInt()

          //  duration = TimeUnit.MILLISECONDS.toDays(currentProgressDuration)
        }

        //Timber.d("data: ${data.title} - ${progress} - ${duration}")

        return progress
    }


    private fun setNewStatus() {
        status = ITEM_STATUS[0]
    }

    private fun setInactiveStatus() {
        status = ITEM_STATUS[2]
    }

    private fun setUrgentStatus() {
        status = ITEM_STATUS[1]
    }

    class StatusTimeData(val data: DataItem, timeNow: CurrentTime) {
        var currentTime: Long = 0
        private var repeatStartTime: Long = 0
        private var completedTime: Long = 0
        var repeatPeriodDuration: Long = 0
        var currentPeriod: LongRange = LongRange(0, 0)
        var currentDate: LongRange = LongRange(0, 0)

        private var previousPeriod: LongRange = LongRange(0, 0)
        var completedInCurrentPeriod = false
        var completedInPreviousPeriod = false
        var completedBeforePreviousPeriod = false
        var completedBeforeCurrentPeriod = false
        var completedAfterRepeatStart = false
        var completedTimeUnknown = true
        var dueDate: Long = 0
        var nextDate: Long = 0

        init {
            currentTime = timeNow.getMillis()
            if (data.completedTime > 0) completedTimeUnknown = false
            repeatStartTime = data.repeatStart
            completedAfterRepeatStart = data.completedTime >= data.repeatStart
            completedTime = data.completedTime
            repeatPeriodDuration = getRepeatPeriodDuration(data)
            currentDate = timeToStartOfTheDay(currentTime)..timeToEndOfTheDay(currentTime)
        }


        fun calculateRepeatDate() {

            val statusAllPeriodDuration = currentTime - repeatStartTime
            val currentPeriodDuration = statusAllPeriodDuration % repeatPeriodDuration
            val currentPeriodStart = getCurrentPeriodStart(currentPeriodDuration)
            val previousPeriodStart = currentPeriodStart - repeatPeriodDuration

            currentPeriod = currentPeriodStart..(currentPeriodStart+repeatPeriodDuration)
            previousPeriod = (previousPeriodStart) until currentPeriodStart
            completedInCurrentPeriod = completedTime in currentPeriod
            completedInPreviousPeriod = completedTime in previousPeriod
            completedBeforeCurrentPeriod = completedTime < currentPeriodStart
            completedBeforePreviousPeriod = completedTime < previousPeriodStart

            dueDate = if ( repeatStartTime > currentTime ) {
                repeatStartTime
            } else {
                currentPeriodStart //+ repeatPeriodDuration - 60 *(1000)
            }

            nextDate =  dueDate + repeatPeriodDuration

        }


        fun calculateRepeatTime() {
            val statusAllPeriod = currentTime - repeatStartTime
            val currentPeriodDuration = statusAllPeriod % repeatPeriodDuration
            val currentPeriodStart = getCurrentPeriodStart(currentPeriodDuration)
            val previousPeriodStart = currentPeriodStart - repeatPeriodDuration
            currentPeriod = currentPeriodStart..(currentPeriodStart+repeatPeriodDuration)
            previousPeriod = (previousPeriodStart)..currentPeriodStart
            completedInCurrentPeriod = completedTime in currentPeriod
            completedInPreviousPeriod = completedTime in previousPeriod
            completedBeforePreviousPeriod = completedTime < previousPeriodStart

            dueDate = currentPeriodStart + repeatPeriodDuration - 60 * (1000)
            nextDate =  dueDate + repeatPeriodDuration
        }

        fun calculateRepeatAfterTime() {

            var currentPeriodStart = timeToStartOfTheDay(currentTime)
            dueDate = getDueDateFromTime(currentTime)
            currentPeriod = currentPeriodStart..dueDate

            //if (data.hasCompletedStatus()) {
                var completedAt = data.completedTime
                if (completedTimeUnknown) completedAt = repeatStartTime
                val recentPeriod = (currentTime - repeatPeriodDuration)..currentTime
                val recentlyCompleted = completedAt in recentPeriod
                completedInPreviousPeriod = recentlyCompleted

                if (recentlyCompleted) {
                    currentPeriodStart =
                        timeToStartOfTheDay(completedAt + TimeUnit.DAYS.toMillis(1))
                    dueDate = getDueDateAfterCompleted(currentPeriodStart)
                    currentPeriod = currentPeriodStart..dueDate
                }
           // }
        }

        private fun getDueDateAfterCompleted(currentPeriodStart: Long): Long {
            val plannedDueDate = currentPeriodStart + repeatPeriodDuration - 60 * (1000)
            return getDueDateFromTime(plannedDueDate)
        }

        private fun getDueDateFromTime(time: Long): Long {
            return timeToEndOfTheDay(time)
        }

        private fun getCurrentPeriodStart(currentPeriodDuration: Long): Long {
            var currentPeriodStart = currentTime - currentPeriodDuration
            //adjust for daylight change
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = currentPeriodStart
            if (calendar.get(Calendar.HOUR_OF_DAY) in 22..23) currentPeriodStart += TimeUnit.HOURS.toMillis(
                3)
            return timeToStartOfTheDay(currentPeriodStart)
        }

        private fun getRepeatPeriodDuration(data: DataItem) =
            try {
                processPeriod(data.repeat) * 1000
            } catch (e: Exception) {
                1
            }
    }

    fun getDueDate(): Long = dueDate
    fun getNextDate(): Long = nextDueDate

    private fun setStatusByProgress(progress: Int, urgentOverdue: Boolean): String {
        return when {
            progress > 91 -> ITEM_STATUS[5] // status completed
            progress in 45..90 -> ITEM_STATUS[4] // status in progress
            progress in 21..44 -> ITEM_STATUS[3] // status started
            progress in 5..20 -> ITEM_STATUS[0]  // status new
            else -> {
                if (urgentOverdue)  ITEM_STATUS[1] // status urgent
                else ITEM_STATUS[0]
            }
        }
    }

    private fun isOverdue(dueDate: Long, status: String): Boolean {
        var overdue = false
        val currentTime = currentTime.getMillis()
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

class CurrentTime @Inject constructor() {
    fun getMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getNewItemId(): String {
        return System.currentTimeMillis().toString()
    }
}




