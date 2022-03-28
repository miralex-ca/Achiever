package com.muralex.achiever.data.database

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PrepoulateWorker(
        context: Context,
        workerParams: WorkerParameters

) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        try {

            val database = AppDatabase.getInstance(applicationContext)

            applicationContext.assets.open(GROUPS_FILE).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val dataType = object : TypeToken<List<Group>>() {}.type
                    val groups: List<Group> = Gson().fromJson(jsonReader, dataType)
                    database.dataItemDAO.insertGroups(groups)
                }
            }

            applicationContext.assets.open(ITEMS_FILE).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val dataType = object : TypeToken<List<DataItem>>() {}.type
                    val items: List<DataItem> = Gson().fromJson(jsonReader, dataType)

                    Timber.d("Groups: ${items[0]}")

                    database.dataItemDAO.insertItems(items)

                }
            }

            Result.success()

        } catch (ex: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val GROUPS_FILE = "data/groups.json"
        private const val ITEMS_FILE = "data/items.json"


    }

}
