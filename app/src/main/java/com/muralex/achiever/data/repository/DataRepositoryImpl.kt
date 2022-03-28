package com.muralex.achiever.data.repository

import com.muralex.achiever.data.database.DataItemDao
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.GroupDataListMapper
import com.muralex.achiever.data.models.mappers.GroupDataMapper
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.domain.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepositoryImpl (private val dao: DataItemDao) : DataRepository {

    override fun getGroupsList(): Flow<List<GroupData>> {
        return dao.getAllGroups().map {
           GroupDataListMapper().invoke(it)
        }
    }

    override suspend fun getArchivedGroupsList(): Flow<List<GroupData>> {
        return dao.getAllArchivedGroups().map {
            GroupDataListMapper().invoke(it)
        }
    }

    override fun getGroup(id: String): Flow<GroupData> {
        return  dao.getGroupById(id).map {
            GroupDataMapper().invoke(it)
        }
    }

    override suspend fun createGroup(group: Group) {
        dao.insertGroup(group)
    }

    override suspend fun updateGroup(group: Group) {
       dao.updateGroup(group)
    }

    override suspend fun deleteGroup(group: Group) {
        dao.deleteGroup(group, emptyList())
    }


}