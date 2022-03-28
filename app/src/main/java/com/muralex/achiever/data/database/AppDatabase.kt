package com.muralex.achiever.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.notifications.InitAlarmScheduleWorker
import com.muralex.achiever.presentation.utils.Constants.DATABASE_NAME


@Database(entities = [Group::class, DataItem::class], version = 2,  exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val dataItemDAO: DataItemDao

    companion object {

        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            val request = OneTimeWorkRequestBuilder<PrepoulateWorker>()
                                .build()
                            WorkManager.getInstance(context).enqueue(request)

                            val alarms = OneTimeWorkRequestBuilder<InitAlarmScheduleWorker>()
                                    .build()

                            WorkManager.getInstance(context).enqueue(alarms)
                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}



