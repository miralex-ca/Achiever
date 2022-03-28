package com.muralex.achiever.domain


import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.GroupData
import kotlinx.coroutines.flow.Flow


interface DataRepository {

    fun getGroupsList() : Flow<List<GroupData>>

    suspend fun getArchivedGroupsList() : Flow<List<GroupData>>

    fun getGroup(id: String) : Flow<GroupData>
    suspend fun createGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)

}