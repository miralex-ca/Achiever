package com.muralex.achiever.utilities

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.data.models.usemodels.PinnedItem

object TestDoubles {
    private const val testId = "item_id"
    val testDataItem  =  DataItem(testId, "", "", "", "", 0, 0)
    private const val testGroupId = "group_id"
    val testGroup  =  Group(testGroupId, "", "", "", 0, 0, 0)
    val testGroupData = GroupData(testGroup)
    val testDataItemAndGroup = DataItemAndGroup(testDataItem, testGroup)
    val testPinnedItem = PinnedItem(testId, "", "", "", 0, "", testDataItem, testGroup )


}