package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PinnedItemsMapperTest : BaseUnitTest() {

    private val displayStatus = mock<DisplayStatus>()
    private val pinnedItemsMapper = PinnedItemsMapper(displayStatus)
    private val  list : List<DataItemAndGroup> = listOf(TestDoubles.testDataItemAndGroup)

    @Test
    fun keepSameListSize() {
        whenever(displayStatus.calculate(any())).thenReturn("")
        val result = pinnedItemsMapper.invoke(list)
        assertEquals(list.size, result.size)
    }

}