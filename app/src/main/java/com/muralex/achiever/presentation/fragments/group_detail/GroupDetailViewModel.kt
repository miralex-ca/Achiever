package com.muralex.achiever.presentation.fragments.group_detail

import androidx.lifecycle.*
import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.ItemInGroup
import com.muralex.achiever.domain.group_usecases.GetGroupWithItemsUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_SCROLL_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_START_COUNT
import com.muralex.achiever.presentation.utils.changePinned
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val getGroupWithItemsUseCase: GetGroupWithItemsUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase
    ) : ViewModel() {

    private lateinit var groupId: String
    private var groupItem: Group? = null

    var maxListSize = TASK_LIST_START_COUNT

    fun addListItems(sizeOfCurrentList: Int) {
        if (maxListSize < sizeOfCurrentList + TASK_LIST_LEFT_BEFORE_ADD) {
            maxListSize += TASK_LIST_SCROLL_ADD
        }
    }

    fun setGroupId (id: String) {
        groupId = id
    }

    fun getGroup() = liveData {
        getGroupWithItemsUseCase.execute(groupId).collect {
            val group = it.copy(
                items = it.items.sortedWith(
                    compareBy<ItemInGroup> { item -> item.sortByStatus}
                        .thenByDescending{ item -> item.data.sort })
            )
            groupItem = it.group
            emit(group)
        }
    }

    fun changeItemStatus(item: DataItem) {
        updateItem(item)
    }

    private fun updateItem(item: DataItem) = viewModelScope.launch {
        updateItemUseCase.execute(item)
    }

    fun deleteItem(item: DataItem) {
        viewModelScope.launch {
            deleteItemUseCase.execute(item)
        }
    }

    fun checkTodo(item: ItemInGroup) {
        val data = item.data.copy()
        if (item.displayStatus == ITEM_STATUS[5]) {
            data.status = ITEM_STATUS[0]
        } else  {
            data.status = ITEM_STATUS[5]
            data.completedTime = System.currentTimeMillis()
        }
        updateItem(data)
    }

    fun changePin(item: DataItem) {
        var pinned = 0
        if (item.pinned == 0) pinned = 1
        updateItem( item.changePinned(pinned) )
    }

    fun archiveGroup(action: Action ) {
        val archiveIndex = if (action == Action.Unarchive) 0 else 1
        viewModelScope.launch {
            groupItem?.let {
                val data = it.copy(archived = archiveIndex, sort = System.currentTimeMillis())
                updateGroupUseCase.invoke(data)
            }
        }
    }


    fun listIsArchived(): Boolean {
        var archived = false
        groupItem?.let {
            if (it.archived > 0) archived = true
        }
        return archived
    }

}