package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.datamodels.GroupWithDataItems
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.data.models.usemodels.GroupWithItemsInGroup
import javax.inject.Inject

class GroupStatsDataMapper @Inject constructor(
    private val itemInGroupMapper: ItemInGroupMapper
): Function1<GroupWithDataItems?, GroupWithItemsInGroup> {

    override fun invoke(groupWithData: GroupWithDataItems?): GroupWithItemsInGroup {

        val group = if (groupWithData==null) Group("", "", "", "")
        else groupWithData.group
        val items: List<DataItem> = if (groupWithData==null) emptyList() else groupWithData.items
        val groupData =  GroupData(group)

        val itemsInGroup = itemInGroupMapper.invoke(items)

        groupData.getStats(itemsInGroup)

       return GroupWithItemsInGroup (
            group,
            itemsInGroup,
            items,
            groupData
        )
    }

}