package com.muralex.achiever.data.database

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.utilities.MainCoroutineScopeRule
import com.muralex.achiever.utilities.TestDoubles
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class DaoTest {

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineScopeRule()

    private lateinit var appDatabase: AppDatabase
    private lateinit var dao: DataItemDao

    @Before
    fun initDb() {
        appDatabase = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        dao = appDatabase.dataItemDAO
    }

    @After
    fun closeDb() {
        appDatabase.close()
        countingTaskExecutorRule.drainTasks(1, TimeUnit.SECONDS)
        assertThat(countingTaskExecutorRule.isIdle).isTrue()
    }

    @Test
    fun getTasks_WhenNoTaskInserted() = runBlocking {
        val tasks = dao.getAllGroups().first()
        assertThat(tasks).isEmpty()
    }

    @Test
    fun insertItem_dataItem_readItemWithTheSameId() = runBlocking {
        dao.insert(testDataItem)
        val data = dao.getItemById(testDataItemId).first()
        assertThat(testDataItemId).isEqualTo(data.id)
    }

    @Test
    fun insertGroup_readGroupWithTheSameId() = runBlocking {
        dao.insertGroup(testGroup)
        val data = dao.getGroupById(testGroupId).first()
        assertThat(testGroupId).isEqualTo(data.id)
    }

    @Test
    fun insertGroups_getInsertedGroups() = runBlocking {
        val group2Id = System.currentTimeMillis().toString()
        val groups = listOf(testGroup.copy(id=group2Id))
        dao.insertGroups(groups)
        val data = dao.getGroupById(group2Id).first()
        assertThat(group2Id).isEqualTo(data.id)
    }

    @Test
    fun updateGroup_changedTitle_readGroupWithTheTitle() = runBlocking {
        val testTitle = "test_title"
        dao.insertGroup(testGroup)
        dao.updateGroup(testGroup.copy(title = testTitle))
        val data = dao.getGroupById(testGroupId).first()
        assertThat(data.title).isEqualTo(testTitle)
    }

    @Test
    fun deleteItem_getItem_resultIsNull() = runBlocking {
        dao.insert(testDataItem)
        dao.delete(testDataItem)
        val data = dao.getItemById(testDataItemId).first()
        assertThat(data).isNull()
    }

    @Test
    fun deleteGroup_getGroup_resultIsNull() = runBlocking {
        dao.insertGroup(testGroup)
        dao.deleteGroup(testGroup, emptyList())
        val data = dao.getGroupById(testGroupId).first()
        assertThat(data).isNull()
    }

    @Test
    fun getPinnedItems_insertPinned_foundPinned() = runBlocking {
        dao.insert(testDataItem.copy(pinned = 1))
        val list = dao.getPinnedItems().first()
        assertThat(list.size).isGreaterThan(0)
    }

    @Test
    fun getPinnedItemsAndGroups_insertPinned_foundPinned() = runBlocking {
        dao.insertGroup(testGroup)
        dao.insert(testDataItem.copy(pinned = 1))
        val list = dao.getPinnedItemsAndGroups().first()
        assertThat(list.size).isGreaterThan(0)
    }

    companion object {
        private val testDataItemId = TestDoubles.testDataItem.id
        private val testGroupId = TestDoubles.testGroup.id
        private val testGroup = TestDoubles.testGroup
        private val testDataItem = TestDoubles.testDataItem
        private val testGroupWithItemsInGroup = TestDoubles.testGroupWithItemsInGroup
        private val testItemInGroup = TestDoubles.testItemInGroup

    }





}