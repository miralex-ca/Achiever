package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.utilities.BaseUnitTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PinnedItemsMapperTest : BaseUnitTest() {

    private val displayStatus = mock<DisplayStatus>()
    private val pinnedItemsMapper = PinnedItemsMapper(displayStatus)
    private val  list : List<DataItemAndGroup> = listOf(testDataItemAndGroup)

    @Test
    fun keepSameListSize() {
        whenever(displayStatus.calculate(any())).thenReturn("")
        val result = pinnedItemsMapper.invoke(list)
        assertEquals(list.size, result.size)
    }

    private companion object {
        private const val testId = "item_id"
        private val testDataItem  =  DataItem(testId, "", "", "", "", 0, 0)
        private const val testGroupId = "group_id"
        private val testGroup  =  Group(testGroupId, "", "", "", 0, 0, 0)
        val testDataItemAndGroup = DataItemAndGroup(testDataItem, testGroup)
    }


}