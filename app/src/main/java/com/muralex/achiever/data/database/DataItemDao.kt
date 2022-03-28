package com.muralex.achiever.data.database

import androidx.room.*
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.datamodels.GroupWithDataItems
import kotlinx.coroutines.flow.Flow

@Dao
interface DataItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(group: List<Group>)

    @Update
    suspend fun updateGroup(group: Group)

    @Delete
    suspend fun deleteGroup(group: Group, items: List<DataItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DataItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<DataItem>)

    @Update
    suspend fun update(item: DataItem)

    @Delete
    suspend fun delete(item: DataItem)

    @Query("SELECT * from group_table WHERE group_archived = 0")
    fun getAllGroups(): Flow<List<Group>>

    @Query("SELECT * from group_table WHERE group_archived > 0")
    fun getAllArchivedGroups(): Flow<List<Group>>

    @Transaction
    @Query("SELECT * FROM group_table WHERE group_id = :groupId")
    fun getGroupWithDataItems(groupId: String): Flow<GroupWithDataItems>

    @Transaction
    @Query("SELECT * FROM data_table")
    fun getItemsAndGroups(): List<DataItemAndGroup>

    @Transaction
    @Query("SELECT * FROM data_table WHERE dataitem_pinned > 0")
    fun getPinnedItemsAndGroups(): Flow<List<DataItemAndGroup>>

    @Query("SELECT * from group_table WHERE group_id = :groupId")
    fun getGroupById(groupId: String): Flow<Group>

    @Query("SELECT * from data_table WHERE dataitem_id = :itemId")
    fun getItemById(itemId: String): Flow<DataItem>

    @Query("SELECT * from data_table WHERE dataitem_pinned > 0")
    fun getPinnedItems(): Flow<List<DataItem>>

    @Query("SELECT * from data_table a left join group_table b ON a.group_id = b.group_id WHERE a.dataitem_title LIKE '%'||:query||'%' " +
            "OR a.dataitem_desc LIKE '%'||:query||'%' OR a.dataitem_text LIKE '%'||:query||'%' OR b.group_title LIKE '%'||:query||'%'")
    fun searchByQuery(query: String): Flow<List<DataItemAndGroup>>


}