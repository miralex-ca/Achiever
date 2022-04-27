package com.muralex.achiever.domain


import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.data.models.usemodels.GroupWithItemsInGroup
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.data.models.usemodels.SearchItem
import kotlinx.coroutines.flow.Flow


interface DataRepository {

    fun getGroupsList() : Flow<List<GroupData>>

    suspend fun getArchivedGroupsList() : Flow<List<GroupData>>
    fun getGroupWithDataItems(id: String) : Flow<GroupWithItemsInGroup>

    fun getGroup(id: String) : Flow<GroupData>
    suspend fun createGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group, items: List<DataItem>)

    suspend fun getDataItem(id: String) : Flow<DataItem>
    suspend fun createDataItem(dataItem: DataItem)
    suspend fun updateItem(dataItem: DataItem)
    suspend fun deleteDataItem(dataItem: DataItem)

    suspend fun getPinnedItemsList(): Flow<List<PinnedItem>>

    suspend fun searchItemsByQuery(query: String): Flow<List<SearchItem>>



}