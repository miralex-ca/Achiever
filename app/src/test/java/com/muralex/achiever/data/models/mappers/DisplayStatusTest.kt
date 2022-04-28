package com.muralex.achiever.data.models.mappers

import android.annotation.SuppressLint
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.datamodels.*
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_DUE_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_NO_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED_AFTER
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO_NEW
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class DisplayStatusTest  {

    lateinit var displayStatus: DisplayStatus
    private val currentTime: CurrentTime = mock()

    @Before
    fun setUp() {
        val  testMoment = "2020/10/10 10:10:10"
        whenever( currentTime.getMillis()).thenReturn(convertTimeToMillis(testMoment))
        displayStatus = DisplayStatus(currentTime)
    }

    @Test
    fun dueDate_itemOverDue_statusUrgent() {
        val item = getTestDataItemDueDate()
        item.completion = convertTimeToMillis("2020/10/10 00:10:10")
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasUrgentStatus()).isTrue()
    }

    @Test
    fun dueDate_completionAhead_statusNotChanged() {
        val item = getTestDataItemDueDate()
        val statusAtStart = item.status
        item.completion = convertTimeToMillis("2020/10/10 10:10:20")
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(statusAtStart).isEqualTo(item.status)
    }

//////////////////////////////////   repeat date

    @Test
    fun repeatDate_startInFuture_statusInactive() {
        val item = getTestDataItemRepeat()
        println("status: ${item.status}")
        item.repeatStart = convertTimeToMillis("2020/10/11 00:00:00")
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasInactiveStatus()).isTrue()
    }

    @Test
    fun repeatDate_completedInCurrentPeriod_statusCompleted() {
        val item = getTestDataItemRepeat()
        item.setCompletedStatus()
        item.repeatStart = convertTimeToMillis("2020/10/08 10:10:10")

        item.completedTime = convertTimeToMillis("2020/10/09 10:10:10")
        item.repeat = "5_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasCompletedStatus()).isTrue()
    }

    @Test
    fun repeatDate_completedPreviouslyDueDateToday_statusNew() {
        val item = getTestDataItemRepeat()
        item.setCompletedStatus()
        item.repeatStart = convertTimeToMillis("2020/10/08 10:10:10")
        item.completedTime = convertTimeToMillis("2020/10/09 10:10:10")
        item.repeat = "2_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }

    @Test
    fun repeatDate_completedBeforePreviouslyDueDateToday_statusUrgent() {
        val item = getTestDataItemRepeat()
        item.setCompletedStatus()
        item.repeatStart = convertTimeToMillis("2020/10/08 10:10:10")
        item.completedTime = convertTimeToMillis("2020/10/07 10:10:10")
        item.repeat = "2_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasUrgentStatus()).isTrue()
    }

    @Test
    fun repeatDate_completedBeforeStartAndFirstDay_statusNew() {
        val item = getTestDataItemRepeat()
        item.setCompletedStatus()
        item.completedTime = convertTimeToMillis("2020/10/09 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/10 10:10:10")
        item.repeat = "2_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }

    @Test
    fun repeatDate_statusBlueAndFirstDay_statusInProgress() {
        val item = getTestDataItemRepeat()
        item.setInProgressStatus()
        item.repeatStart = convertTimeToMillis("2020/10/10 10:10:10")
        item.repeat = "2_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasInProgressStatus()).isTrue()
    }

    @Test
    fun repeatDateAndStatusModeNew_completedBeforePreviouslyDueDateToday_statusNew() {
        val item = getTestDataItemRepeat()
        item.statusMode = ITEM_STATUS_MODE_AUTO_NEW
        item.setCompletedStatus()
        item.repeatStart = convertTimeToMillis("2020/10/08 10:10:10")
        item.completedTime = convertTimeToMillis("2020/10/07 10:10:10")
        item.repeat = "2_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }


    @Test
    fun repeatDateAndStatusModeNew_OverDue_statusNew() {
        val item = getTestDataItemRepeat()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/10/04 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/05 10:10:10")
        item.repeat = "5_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasUrgentStatus()).isTrue()
    }

    @Test
    fun repeatAndStatusModeNew_OverDue_statusUrgent() {
        val item = getTestDataItemRepeat()
        item.setCompletedStatus()
        item.statusMode = ITEM_STATUS_MODE_AUTO_NEW
        item.completedTime = convertTimeToMillis("2020/10/04 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/05 10:10:10")
        item.repeat = "5_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }


  ///////////////////////////////////////////// repeat after
    @Test
    fun repeatAfter_completedOverdue_statusUrgent() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/10/01 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/01 10:10:10")
        item.repeat = "10_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }


    @Test
    fun repeatAfter_startInFuture_statusInactive() {
        val item = getTestDataItemRepeatAfter()
        println("status: ${item.status}")
        item.repeatStart = convertTimeToMillis("2020/10/11 00:00:00")
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasInactiveStatus()).isTrue()
    }

    @Test
    fun repeatAfter_completed10Percent_statusNew() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/10/01 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/01 10:10:10")
        item.repeat = "10_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }

    @Test
    fun repeatAfter_completed80Percent_statusProgress() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/10/07 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/01 10:10:10")
        item.repeat = "10_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasInProgressStatus()).isTrue()
    }

    @Test
    fun repeatAfter_completed30Percent_statusStarted() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/10/03 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/01 10:10:10")
        item.repeat = "10_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasStartedStatus()).isTrue()
    }

    @Test
    fun repeatAfter_completed05Percent_statusUrgent() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/09/26 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/09/25 10:10:10")
        item.repeat = "14_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasUrgentStatus()).isTrue()
    }

    @Test
    fun repeatAfterAndStatusModeNew_OverDue_statusNew() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.statusMode = ITEM_STATUS_MODE_AUTO_NEW
        item.completedTime = convertTimeToMillis("2020/10/03 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/02 10:10:10")
        item.repeat = "5_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasNewStatus()).isTrue()
    }


    @Test
    fun repeatAfter_OverDue_statusUrgent() {
        val item = getTestDataItemRepeatAfter()
        item.setCompletedStatus()
        println("status: ${item.status}")
        item.completedTime = convertTimeToMillis("2020/10/03 10:10:10")
        item.repeatStart = convertTimeToMillis("2020/10/02 10:10:10")
        item.repeat = "5_day"
        item.status = displayStatus.calculate(item)
        println("status: ${item.status}")
        assertThat(item.hasUrgentStatus()).isTrue()
    }



    @Test
    fun completion_noDate_statusNotChanged() {
        val item = getTestDataItem()
        item.completionType = COMPLETION_TYPE_NO_DATE
        val status = item.status
        displayStatus.calculate(item)
        assertThat(status).isEqualTo(item.status)
    }

    @Test
    fun validateTimeConversionFromMillis() {
        val timeString = "2014/10/29 18:10:45"
        val timeFromString = convertTimeToMillis(timeString)
        val stringFromTime = convertMillisToTime(timeFromString)
        println("time: $timeFromString")
        println("time: ${ convertMillisToTime(timeFromString)}")
        assertThat(timeString).isEqualTo(stringFromTime)
    }


    private fun getTestDataItem() : DataItem {
        return DataItem("id", "title")
    }

    private fun getTestDataItemDueDate() : DataItem {
        val item = getTestDataItem()
        item.completionType = COMPLETION_TYPE_DUE_DATE
        item.statusMode = ITEM_STATUS_MODE_AUTO
        return item
    }


    private fun getTestDataItemRepeat() : DataItem {
        val item = getTestDataItem()
        item.completionType = COMPLETION_TYPE_REPEATED
        item.statusMode = ITEM_STATUS_MODE_AUTO
        return item
    }

    private fun getTestDataItemRepeatAfter() : DataItem {
        val item = getTestDataItem()
        item.completionType = COMPLETION_TYPE_REPEATED_AFTER
        item.statusMode = ITEM_STATUS_MODE_AUTO
        item.repeatStart = convertTimeToMillis("2020/10/10 10:10:10")
        item.repeat = "1_day"
        return item
    }


    private fun convertTimeToMillis(date: String) : Long {
        val localDateTime: LocalDateTime = LocalDateTime.parse(date,
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

        return localDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertMillisToTime(date: Long) : String {
        val formatter =  SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return  formatter.format( date )
    }







}