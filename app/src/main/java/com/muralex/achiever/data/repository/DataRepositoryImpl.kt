package com.muralex.achiever.data.repository

import com.muralex.achiever.data.database.DataItemDao
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.*
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.data.models.usemodels.GroupWithItemsInGroup
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.data.models.usemodels.SearchItem
import com.muralex.achiever.domain.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepositoryImpl(
    private val dao: DataItemDao,
    private val itemInGroupMapper: ItemInGroupMapper,
    private val groupStatsDataMapper: GroupStatsDataMapper,
    private val pinnedItemsMapper: PinnedItemsMapper,
    private val searchItemMapper: SearchItemMapper

    ) : DataRepository {

    override fun getGroupsList(): Flow<List<GroupData>> {

        return dao.getAllGroups().map {
            it.map { group ->
                val data = GroupDataMapper().invoke(group.group)
                data.getStats(itemInGroupMapper.invoke(group.items))
                data
            }
        }
    }

    override suspend fun getArchivedGroupsList(): Flow<List<GroupData>> {
        return dao.getAllArchivedGroups().map {
            it.map { group ->
                val data = GroupDataMapper().invoke(group.group)
                data.getStats(itemInGroupMapper.invoke(group.items))
                data
            }
        }
    }

    override fun getGroup(id: String): Flow<GroupData> {
        return dao.getGroupById(id).map {
            GroupDataMapper().invoke(it)
        }
    }

    override suspend fun createGroup(group: Group) {
        dao.insertGroup(group)
    }

    override suspend fun updateGroup(group: Group) {
        dao.updateGroup(group)
    }

    override suspend fun deleteGroup(group: Group, items: List<DataItem>) {
        dao.deleteGroup(group, items)
    }

    override suspend fun getDataItem(id: String): Flow<DataItem> {
        return dao.getItemById(id)
    }

    override suspend fun createDataItem(dataItem: DataItem) {
        dao.insert(dataItem)
    }

    override suspend fun updateItem(dataItem: DataItem) {
        dao.update(dataItem)
    }

    override suspend fun deleteDataItem(dataItem: DataItem) {
        dao.delete(dataItem)
    }

    override fun getGroupWithDataItems(id: String): Flow<GroupWithItemsInGroup> {
        return dao.getGroupWithDataItems(id).map {
            groupStatsDataMapper.invoke(it)
        }
    }

    override suspend fun getPinnedItemsList(): Flow<List<PinnedItem>> {
        return dao.getPinnedItemsAndGroups().map {
            pinnedItemsMapper.invoke(it)
        }
    }

    override suspend fun searchItemsByQuery(query: String): Flow<List<SearchItem>> {
        return dao.searchByQuery(query).map {
            searchItemMapper.invoke(it)
        }
    }


}