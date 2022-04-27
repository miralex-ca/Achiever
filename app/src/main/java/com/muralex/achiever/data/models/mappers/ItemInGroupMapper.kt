package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.usemodels.*
import com.muralex.achiever.presentation.utils.convertStatusToInt
import timber.log.Timber
import javax.inject.Inject

class ItemInGroupMapper @Inject constructor(
    private val displayStatus: DisplayStatus
    ) :
    Function1<List<DataItem>, List<ItemInGroup>> {

    override fun invoke(itemsList: List<DataItem>): List<ItemInGroup> {
        return itemsList.map { dataItem ->

            val currentStatus = displayStatus.calculate(dataItem)
            val taskSchedule = TaskScheduleDisplay(false)

            ItemInGroup(
                dataItem.id,
                dataItem.title,
                dataItem.desc,
                convertStatusToInt(currentStatus),
                currentStatus,
                displayStatus.getDueDate(),
                dataItem,
                taskSchedule,
                displayStatus.getNextDate()
            ).also {
                it.schedule = it.schedule.calculate(it)
            }
        }
    }
}
