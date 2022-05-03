package com.muralex.achiever.utilities

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.mappers.DisplayStatus
import com.muralex.achiever.data.models.mappers.ItemInGroupMapper
import com.muralex.achiever.data.models.usemodels.*

object TestDoubles {

    private const val testId = "item_id"
    private const val testGroupId = "group_id"
    val testDataItem  =  DataItem(testId, "Data title", "Data desc", "Data text", "none", 0, 0, groupId = testGroupId)
    val testGroup  =  Group(testGroupId, "Group title", "Group desc", "", 0, 0, 0)
    val testGroupData = GroupData(testGroup)
    val testDataItemAndGroup = DataItemAndGroup(testDataItem, testGroup)
    val testPinnedItem = PinnedItem(testId, testDataItem.title, testGroup.image!!, testGroup.title,
        0, "", testDataItem.copy(pinned = 1), testGroup )

    val testGroupWithItemsInGroup = GroupWithItemsInGroup ( testGroup, emptyList(), emptyList(), testGroupData)

    val testItemInGroup = ItemInGroup(testDataItem.id, testDataItem.title,
        testDataItem.text, 0, "none", 0, testDataItem, TaskScheduleDisplay(false) )



}