package com.muralex.achiever.data.database

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.presentation.utils.Constants.ITEM_COMPLETION_TYPE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class PrepopulateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataItemDAO: DataItemDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        try {
            applicationContext.assets.open(GROUPS_FILE).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val dataType = object : TypeToken<List<Group>>() {}.type
                    val groups: List<Group> = Gson().fromJson(jsonReader, dataType)

                    dataItemDAO.insertGroups(groups)
                }
            }

            applicationContext.assets.open(ITEMS_FILE).use { inputStream ->

                JsonReader(inputStream.reader()).use { jsonReader ->

                    val dataType = object : TypeToken<List<DataItem>>() {}.type
                    val items: List<DataItem> = Gson().fromJson(jsonReader, dataType)

                    val time = System.currentTimeMillis()

                    items.forEach {
                        if (it.completionType == ITEM_COMPLETION_TYPE[3])  {
                            it.repeatStart = time
                        }

                        if (it.completionType == ITEM_COMPLETION_TYPE[1])  {
                            it.completion = time + TimeUnit.DAYS.toMillis(3)
                        }
                    }

                   dataItemDAO.insertItems(items)
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
