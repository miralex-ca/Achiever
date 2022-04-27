package com.muralex.achiever.presentation.fragments.pinned


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.domain.item_usecases.GetPinnedItemsUseCase
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_SCROLL_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_START_COUNT
import com.muralex.achiever.presentation.utils.changePinned
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor (
    private val getPinnedItemsUseCase: GetPinnedItemsUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
    ) : ViewModel() {


    val displayEmptyList = MutableLiveData<Boolean>()

    var maxListSize = TASK_LIST_START_COUNT

    fun addListItems(sizeOfCurrentList: Int) {
        if (maxListSize < sizeOfCurrentList + TASK_LIST_LEFT_BEFORE_ADD) {
            maxListSize += TASK_LIST_SCROLL_ADD
        }
    }

    fun getItems() = liveData {

        displayEmptyList.postValue(false)

        getPinnedItemsUseCase.execute()
            .conflate()
            .onEach {
                if ( it.isEmpty() ) displayEmptyList.postValue(true)
                else displayEmptyList.postValue(false)
            }
            .collect {

            emit( it.sortedWith(
                compareBy<PinnedItem> { item -> item.statusSort }
                    .thenByDescending{ item -> item.data.pinnedTime })
            )
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

    fun checkTodo(item: DataItem) {
        var status = Constants.ITEM_STATUS[5]

        if (item.status == Constants.ITEM_STATUS[5]) status = Constants.ITEM_STATUS[0]

        updateItem(item.copy(status = status))
    }

    fun changePin(item: DataItem) {
        var pinned = 0
        if (item.pinned == 0) pinned = 1
        updateItem(item.changePinned(pinned))
    }




}