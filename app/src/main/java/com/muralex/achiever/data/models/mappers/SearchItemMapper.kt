package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.usemodels.SearchItem
import javax.inject.Inject

class SearchItemMapper @Inject constructor(
    private val displayStatus : DisplayStatus
        ): Function1<List<DataItemAndGroup>, List<SearchItem>> {
    override fun invoke(itemsList: List<DataItemAndGroup>): List<SearchItem> {
        return itemsList.map {
            val currentStatus = displayStatus.calculate(it.dataItem)

            SearchItem(
                it.dataItem
            ).also {item ->
                item.image = it.group.image.toString()
                item.data = it.dataItem
                item.groupData = it.group
                item.displayStatus = currentStatus
            }
        }
    }
}
