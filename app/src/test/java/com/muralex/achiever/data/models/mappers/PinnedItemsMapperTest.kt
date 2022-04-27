package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.domain.DataRepository
import com.muralex.achiever.utils.BaseUnitTest
import kotlinx.coroutines.flow.flow
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PinnedItemsMapperTest : BaseUnitTest() {

    private val displayStatus = mock<DisplayStatus>()
    private val mapper = PinnedItemsMapper(displayStatus)
    private val  list : List<DataItemAndGroup> = listOf(FAKE_DATA_GROUP)

    @Test
    fun keepSameListSize() {
        whenever(displayStatus.calculate(any())).thenReturn("")
        val result = mapper.invoke(list)
        assertEquals(list.size, result.size)
    }


    companion object {
        private const val FAKE_ID = "item_id"
        private val FAKE_DATA  =  DataItem(FAKE_ID, "", "", "", "", 0, 0)
        private const val FAKE_GROUP_ID = "group_id"
        private val FAKE_GROUP  =  Group(FAKE_GROUP_ID, "", "", "", 0, 0, 0)
        ///val FAKE_PINNED_DATA = PinnedItem(FAKE_ID, "", "", "", 0, "", FAKE_DATA, FAKE_GROUP )
        val FAKE_DATA_GROUP = DataItemAndGroup(FAKE_DATA, FAKE_GROUP)
    }


}