package com.muralex.achiever.presentation.fragments.archive

import androidx.lifecycle.*
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.domain.group_usecases.GetArchivedGroupsListUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val getArchivedGroupsListUseCase: GetArchivedGroupsListUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase
): ViewModel() {

    val loader = MutableLiveData<Boolean>()

    var maxListSize = 10

    fun addListItems(sizeOfCurrentList: Int) {
        if (maxListSize < sizeOfCurrentList + 5) {
            maxListSize += 5
        }
    }

    val groupsLIst = liveData {

        loader.postValue(true)
        emitSource(
            getArchivedGroupsListUseCase.invoke()
                .onEach {
                    loader.postValue(false)
                }
                .asLiveData())
    }

    fun moveGroupToUp(group: Group) {
        viewModelScope.launch {
            val data = group.copy(sort = System.currentTimeMillis())
            updateGroupUseCase.invoke(data)
        }
    }

    fun unarchiveGroup(group: Group) {
        viewModelScope.launch {
            val data = group.copy(archived = 0, sort = System.currentTimeMillis())
            updateGroupUseCase.invoke(data)
        }
    }



}