package com.muralex.achiever.data.models.usemodels

import android.text.format.DateUtils
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.convertStatusToInt
import com.muralex.achiever.presentation.utils.convertStatusToProgressInt
import timber.log.Timber

data class GroupData(
    val id: String?,
    var title: String?,
    var text: String?,
    var group: Group? = null,
    var totalItemsCount: Int = 0,
    var completedItems: Int = 0,
    var progress: Int = 0,
    var urgentItems: Int = 0,
    var inactiveItems: Int = 0,
    var activeItems: Int = 0,
    var todayItems: Int = 0
) {

    constructor(group: Group?) : this (
        group?.id,
        group?.title,
        group?.text,
        group
    )

    fun getStats(items: List<ItemInGroup>) {

        totalItemsCount = items.size

        completedItems = items.filter {
            it.displayStatus == ITEM_STATUS[5]
        }.size

        urgentItems = items.filter {
            it.displayStatus == ITEM_STATUS[1]
        }.size

        inactiveItems = items.filter {
            it.displayStatus == ITEM_STATUS[2]
        }.size

        activeItems = totalItemsCount - inactiveItems

        if (totalItemsCount > 0) progress = calculateProgress(items)

        todayItems = items.filter {
            it.schedule.dueToday
        }.size

    }

    private fun calculateProgress(items: List<ItemInGroup>) : Int {

        var totalCount = activeItems
        var progressCount = 0

        for (item in items) {
          progressCount +=  convertStatusToProgressInt(item.displayStatus)
        }
        if (totalCount < 1) totalCount = 1

       return progressCount / totalCount
    }

}

data class GroupWithItemsInGroup (
    val group: Group?,
    val items: List<ItemInGroup>,
    val dataItems: List<DataItem>,
    var groupData: GroupData? = null
)
