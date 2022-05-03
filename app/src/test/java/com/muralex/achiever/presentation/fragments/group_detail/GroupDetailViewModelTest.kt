package com.muralex.achiever.presentation.fragments.group_detail

import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.usemodels.ItemInGroup
import com.muralex.achiever.domain.group_usecases.GetGroupWithItemsUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.presentation.fragments.archive.ArchiveViewModelTest
import com.muralex.achiever.presentation.fragments.pinned.DashboardViewModel
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_START_COUNT
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import com.muralex.achiever.utilities.getOrAwaitValueTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class GroupDetailViewModelTest : BaseUnitTest() {

    private lateinit var groupDetailViewModel: GroupDetailViewModel
    private val getGroupWithItemsUseCase: GetGroupWithItemsUseCase = mock()
    private val updateItemUseCase: UpdateItemUseCase = mock()
    private val deleteItemUseCase: DeleteItemUseCase = mock()
    private val updateGroupUseCase: UpdateGroupUseCase = mock()
    private val testDataItem = TestDoubles.testDataItem
    private val testGroupId = TestDoubles.testGroup.id
    private val expected = TestDoubles.testGroupWithItemsInGroup
    private val currentTime: CurrentTime = mock()
    private val testCurrentTime : Long = 20
    private val testItemInGroup = TestDoubles.testItemInGroup

    @Before
    fun setUp() {
        groupDetailViewModel = GroupDetailViewModel(getGroupWithItemsUseCase, updateItemUseCase,
            deleteItemUseCase,updateGroupUseCase, currentTime)
        groupDetailViewModel.setGroupId(testGroupId)
        whenever( currentTime.getMillis()).thenReturn(testCurrentTime)
    }

    @Test
    fun noChangeListCountIfMaxIsNotReached() = runTest {
        val listCountMaxNotReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD - 1
        groupDetailViewModel.addListItems(listCountMaxNotReached)
        assertThat(groupDetailViewModel.maxListSize).isEqualTo(TASK_LIST_START_COUNT)
    }

    @Test
    fun changeListCountIfMaxIsReached() = runTest {
        val listCountMaxReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD + 1
        groupDetailViewModel.addListItems(listCountMaxReached)
        assertThat(groupDetailViewModel.maxListSize)
            .isEqualTo(TASK_LIST_START_COUNT + Constants.GROUP_LIST_SCROLL_ADD)
    }

    @Test
    fun getGroupIfLifeDataEvent() = runTest {
        mockSuccessfulCase()
        verify(getGroupWithItemsUseCase, times(1)).execute(testGroupId)
    }

    @Test
    fun setGroupId_getGroupWithTheSetId() = runTest {
        mockSuccessfulCase()
        verify(getGroupWithItemsUseCase, times(1)).execute(testGroupId)
    }

    @Test
    fun archiveGroup_actionArchive_updateGroupWithGroup() = runTest {
        val argument: ArgumentCaptor<Group> = ArgumentCaptor.forClass(Group::class.java)
        mockSuccessfulCase()
        groupDetailViewModel.archiveGroup(Action.Archive)
        verify(updateGroupUseCase, times(1)).invoke(MockitoHelper.capture(argument))
        assertThat(argument.value.archived).isEqualTo(1)
        assertThat( argument.value.sort).isEqualTo(testCurrentTime)
    }

    @Test
    fun changeItemStatus_passItem_updateItemUseCaseWithItem() = runTest {
        groupDetailViewModel.changeItemStatus(testDataItem)
        verify(updateItemUseCase, times(1)).execute(testDataItem)
    }

    @Test
    fun changePin_itemNotPinned_updateItemUseCaseWithItem() = runTest {
        val argument: ArgumentCaptor<DataItem> = ArgumentCaptor.forClass(DataItem::class.java)
        mockSuccessfulCase()
        groupDetailViewModel.changePin(testDataItem)
        verify(updateItemUseCase, times(1)).execute(MockitoHelper.capture(argument))
        assertThat(argument.value.pinned ).isEqualTo(1)
    }

    @Test
    fun listIsArchived_groupNotArchived_returnFalse() = runTest {
        mockSuccessfulCase()
        assertThat(groupDetailViewModel.listIsArchived()).isEqualTo(false)
    }

    @Test
    fun deleteItem_passItem_deleteItemUseCaseWithItem() = runTest {
        groupDetailViewModel.deleteItem(testDataItem)
        verify(deleteItemUseCase, times(1)).execute(testDataItem)
    }

    @Test
    fun checkTodo_passItemInGroup_updateItem() = runTest {
        mockSuccessfulCase()
        groupDetailViewModel.checkTodo(testItemInGroup)
        verify(updateItemUseCase, times(1)).execute(any())
    }

    private fun mockSuccessfulCase() = runTest {
        whenever(getGroupWithItemsUseCase.execute(any())).thenReturn(
            flow { emit(expected) }
        )
        groupDetailViewModel.getGroup().getOrAwaitValueTest()
    }


}