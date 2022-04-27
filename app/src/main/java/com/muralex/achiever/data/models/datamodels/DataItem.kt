package com.muralex.achiever.data.models.datamodels

import androidx.room.*
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_NO_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED_AFTER
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEAT_PERIOD
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODES
import com.muralex.achiever.presentation.utils.Constants.REPEAT_PERIOD_DEFAULT


@Entity(tableName = "data_table")
data class DataItem(
    @PrimaryKey
    @ColumnInfo(name = "dataitem_id")
    var id: String,
    @ColumnInfo(name = "dataitem_title")
    var title: String,
    @ColumnInfo(name = "dataitem_desc")
    var desc: String = "",
    @ColumnInfo(name = "dataitem_text")
    var text: String = "",
    @ColumnInfo(name = "dataitem_status")
    var status: String = "none",
    @ColumnInfo(name = "dataitem_pinned")
    var pinned: Int = 0,
    @ColumnInfo(name = "dataitem_pinned_time")
    var pinnedTime: Long = 0,
    @ColumnInfo(name = "dataitem_completion_type")
    var completionType: String = COMPLETION_TYPE_NO_DATE,
    @ColumnInfo(name = "dataitem_status_mode")
    var statusMode: String = ITEM_STATUS_MODES[0],
    @ColumnInfo(name = "dataitem_completion")
    var completion: Long = 0,
    @ColumnInfo(name = "dataitem_completed_time")
    var completedTime: Long = 0,
    @ColumnInfo(name = "dataitem_repeat_start")
    var repeatStart: Long = 0,
    @ColumnInfo(name = "dataitem_repeat")
    var repeat: String = REPEAT_PERIOD_DEFAULT,
    @ColumnInfo(name = "dataitem_sort")
    var sort: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "dataitem_created")
    var created: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "group_id")
    var groupId: String = ""
)

data class DataItemAndGroup(
    @Embedded val dataItem: DataItem,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "group_id"
    )
    val group: Group
)

fun DataItem.hasCompletedStatus() = this.status == ITEM_STATUS[5]
fun DataItem.setCompletedStatus()  {
    this.status = ITEM_STATUS[5]
}

fun DataItem.hasInactiveStatus() = this.status == ITEM_STATUS[2]
fun DataItem.setInactiveStatus() {
    this.status = ITEM_STATUS[2]
}

fun DataItem.hasUrgentStatus() =  this.status == ITEM_STATUS[1]
fun DataItem.setUrgentStatus() {
    this.status = ITEM_STATUS[1]
}

fun DataItem.hasNewStatus() =  this.status == ITEM_STATUS[0]
fun DataItem.setNewStatus() {
    this.status = ITEM_STATUS[0]
}

fun DataItem.hasInProgressStatus() =  this.status == ITEM_STATUS[4]
fun DataItem.setInProgressStatus() {
    this.status = ITEM_STATUS[4]
}

fun DataItem.hasStartedStatus() =  this.status == ITEM_STATUS[3]
fun DataItem.setStartedStatus() {
    this.status = ITEM_STATUS[3]
}

fun DataItem.isRepeated(): Boolean {
    return completionType == COMPLETION_TYPE_REPEATED || completionType == COMPLETION_TYPE_REPEAT_PERIOD
            || completionType == COMPLETION_TYPE_REPEATED_AFTER
}

fun DataItem.isScheduled() : Boolean {
    var isScheduled = false
    if (completionType != COMPLETION_TYPE_NO_DATE) isScheduled = true
    if (hasInactiveStatus()) isScheduled = false
    return isScheduled
}