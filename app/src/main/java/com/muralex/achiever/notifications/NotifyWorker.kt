package com.muralex.achiever.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.domain.group_usecases.GetGroupsListUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotifyWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getGroupsListUseCase: GetGroupsListUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val items = getGroupsListUseCase.invoke().first()
        val urgent = countUrgent(items)
        val dueToday = countDueToday(items)
        val message = getNotificationMessage(dueToday, urgent)
        NotificationHelper.createNotificationFromWorker(context, message)
        return Result.success()
    }

    private fun getNotificationMessage(dueToday: Int, urgent: Int): String {
        val todayTasks =
            context.resources.getQuantityString(R.plurals.number_of_tasks_today, dueToday, dueToday)
        val urgentTasks =
            context.resources.getQuantityString(R.plurals.number_of_urgent_tasks, urgent, urgent)

        return if (urgent == 0 && dueToday == 0) context.getString(R.string.notification_message)
        else context.getString(R.string.notification_info, todayTasks, urgentTasks)
    }

    private fun countDueToday(items: List<GroupData>): Int {
        var dueToday = 0
        items.filter { it.todayItems > 0 }.forEach {
            dueToday += it.todayItems
        }
        return dueToday
    }

    private fun countUrgent(items: List<GroupData>): Int {
        var urgent = 0
        items.filter { it.urgentItems > 0 }.forEach {
            urgent += it.urgentItems
        }
        return urgent
    }

}

