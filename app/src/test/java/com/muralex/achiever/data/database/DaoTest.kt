package com.muralex.achiever.data.database

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.utilities.MainCoroutineScopeRule
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
    private lateinit var taskDao: DataItemDao


    @Before
    fun initDb() {
        appDatabase = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()

        taskDao = appDatabase.dataItemDAO
    }


    @After
    fun closeDb() {
        appDatabase.close()
        countingTaskExecutorRule.drainTasks(1, TimeUnit.SECONDS)
        assertThat(countingTaskExecutorRule.isIdle).isTrue()
    }

    @Test
    fun getTasks_WhenNoTaskInserted() = runBlocking {
        val tasks = taskDao.getAllGroups().first()
        assertThat(tasks).isEmpty()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val item = DataItem("id", "", "")
        taskDao.insert(item)
        val data = taskDao.getItemById("id").first()
        assertThat("id").isEqualTo(data.id)

    }






}