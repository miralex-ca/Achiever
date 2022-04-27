package com.muralex.achiever.presentation.fragments.home

import androidx.lifecycle.*
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.domain.group_usecases.GetGroupsListUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_SCROLL_ADD
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_START_COUNT
import com.muralex.achiever.presentation.utils.Constants.HOME_LIST_INIT_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGroupsListUseCase: GetGroupsListUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val currentTime: CurrentTime
): ViewModel() {

    private var loadDelay = HOME_LIST_INIT_DELAY

    val loader = MutableLiveData<Boolean>().apply {
        value = true
    }

    val displayEmptyList = MutableLiveData<Boolean>().apply {
        value = false
    }

    var maxListSize = GROUP_LIST_START_COUNT

    fun addListItems(sizeOfCurrentList: Int) {
        if (maxListSize < sizeOfCurrentList + GROUP_LIST_LEFT_BEFORE_ADD) {
            maxListSize += GROUP_LIST_SCROLL_ADD
        }
    }

    val groupsLIst = liveData {
        displayEmptyList.postValue(false)
        withContext(Dispatchers.IO) {
            delay(loadDelay)
            loadDelay=0
            emitSource(
                getGroupsListUseCase.invoke()
                    .conflate()
                    .onEach {
                        loader.postValue(false)
                        if ( it.isEmpty() ) displayEmptyList.postValue(true)
                        else displayEmptyList.postValue(false)
                    }
                    .asLiveData())
        }
    }

    fun moveGroupToUp(group: Group) {
        viewModelScope.launch {
            val data = group.copy(sort = currentTime.getMillis())
            updateGroupUseCase.invoke(data)
        }
    }

    fun archiveGroup(group: Group) {
        viewModelScope.launch {
            val data = group.copy(archived = 1, sort = currentTime.getMillis())
            updateGroupUseCase.invoke(data)
        }
    }

}