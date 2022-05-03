package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.presentation.utils.convertStatusToInt
import com.muralex.achiever.presentation.utils.removeLineBreaks
import javax.inject.Inject

class PinnedItemsMapper @Inject constructor(
    private val displayStatus : DisplayStatus
        ): Function1<List<DataItemAndGroup>, List<PinnedItem>> {

    override fun invoke(itemsList: List<DataItemAndGroup>): List<PinnedItem> {

        return itemsList.map {
            val currentStatus = displayStatus.calculate(it.dataItem)
            PinnedItem(
                it.dataItem.id,
                removeLineBreaks(it.dataItem.title),
                it.group.image!!,
                removeLineBreaks(it.group.title),
                convertStatusToInt(it.dataItem.status),
                currentStatus,
                it.dataItem,
                it.group
            )
        }
    }
}
