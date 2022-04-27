package com.muralex.achiever.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.notifications.InitAlarmScheduleWorker
import com.muralex.achiever.presentation.utils.Constants.DATABASE_NAME


@Database(entities = [Group::class, DataItem::class], version = 3,  exportSchema = false)
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
                .addMigrations(MIGRATION_2_3)
                .addCallback(
                    object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            prepopulate(context)
                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}


fun prepopulate(context: Context) {
    val request = OneTimeWorkRequestBuilder<PrepopulateWorker>().build()
    WorkManager.getInstance(context).enqueue(request)
}


val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE group_table "
                +"ADD COLUMN group_detail INT NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE group_table "
                +"ADD COLUMN group_progress_display INT NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE group_table "
                +"ADD COLUMN group_list_layout INT NOT NULL DEFAULT 0")
    }
}



