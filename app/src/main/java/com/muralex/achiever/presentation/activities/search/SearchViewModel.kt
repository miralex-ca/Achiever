package com.muralex.achiever.presentation.activities.search

import androidx.lifecycle.*
import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.usemodels.SearchItem
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.domain.item_usecases.SearchItemsUseCase
import com.muralex.achiever.presentation.utils.changePinned
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchItemsUseCase: SearchItemsUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
    ) : ViewModel() {

    var maxListSize = INIT_MAX_SIZE

    private fun resetListMaxSize() {
        maxListSize = INIT_MAX_SIZE
    }

    val displayEmptyList = MutableLiveData<Boolean>()

    fun addListItems(sizeOfCurrentList: Int) {
        if ( maxListSize <  sizeOfCurrentList + PAGE_SIZE )
            maxListSize += PAGE_SIZE
    }

    fun searchByQuery(query: String): LiveData<List<SearchItem>> {

        resetListMaxSize()
        displayEmptyList.postValue(false)

        return liveData {

            if (query.isNotEmpty()) {
                searchItemsUseCase.execute(query)
                    .flowOn(Dispatchers.IO)
                    //.conflate()
                    .onEach {
                        if ( it.isEmpty() ) displayEmptyList.postValue(true)
                        else displayEmptyList.postValue(false)
                    }

                    .collectLatest {
                        emit(it)
                    }

            } else {
                emit(emptyList())
            }
        }
    }

    fun changeItemStatus(item: DataItem) {
        updateItem(item)
    }

    fun changePin(item: DataItem) {
        var pinned = 0
        if (item.pinned == 0) pinned = 1
        updateItem(item.changePinned(pinned))
    }

    private fun updateItem(item: DataItem) = viewModelScope.launch {
        updateItemUseCase.execute(item)
    }

    fun deleteItem(item: DataItem) {
        viewModelScope.launch {
            deleteItemUseCase.execute(item)
        }
    }


    companion object {
        const val INIT_MAX_SIZE = 15
        const val PAGE_SIZE = 20
    }

}