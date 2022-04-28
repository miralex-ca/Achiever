package com.muralex.achiever.presentation.fragments.home

import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.domain.group_usecases.GetGroupsListUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_SCROLL_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_START_COUNT
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import com.muralex.achiever.utilities.getOrAwaitValueTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
class HomeViewModelTest : BaseUnitTest()  {

    private lateinit var homeViewModel: HomeViewModel
    private val getGroupsListUseCase: GetGroupsListUseCase = mock()
    private val updateGroupUseCase: UpdateGroupUseCase = mock()
    private val currentTime: CurrentTime = mock()
    private val expected = listOf(testGroupData)

    @Before
    fun setUp() {
        whenever( currentTime.getMillis()).thenReturn( testCurrentTime )
    }

    @Test
    fun noChangeListCountIfMaxIsNotReached() = runTest {
        mockSuccessfulCase()
        val listCountMaxNotReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD - 1
        homeViewModel.addListItems(listCountMaxNotReached)
        assertThat(homeViewModel.maxListSize).isEqualTo(TASK_LIST_START_COUNT)
    }

    @Test
    fun changeListCountIfMaxIsReached() = runTest {
        mockSuccessfulCase()
        val listCountMaxReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD + 1
        homeViewModel.addListItems(listCountMaxReached)
        assertThat(homeViewModel.maxListSize).isEqualTo(TASK_LIST_START_COUNT + GROUP_LIST_SCROLL_ADD)
    }
    
    @Test
    fun getGroupsListIfLifeDataEvent() = runTest {
        mockSuccessfulCase()
        homeViewModel.groupsLIst.getOrAwaitValueTest()
        verify(getGroupsListUseCase, times(1)).invoke()
    }

    @Test
    fun emitsDataFromGroupsListUseCase()  = runBlockingTest {
        mockSuccessfulCase()
        assertThat(homeViewModel.groupsLIst.getOrAwaitValueTest()).isEqualTo(expected)
    }

    @Test
    fun updateGroupSortTimeIfMovedUp() = runTest {
        val argument: ArgumentCaptor<Group> = ArgumentCaptor.forClass(Group::class.java)
        mockSuccessfulCase()
        homeViewModel.moveGroupToUp(testGroup)
        verify(updateGroupUseCase, times(1)).invoke(MockitoHelper.capture(argument))
        assertThat( argument.value.sort).isEqualTo(testCurrentTime)
    }

    @Test
    fun updateGroupArchivedAndSortTimeIfChangesArchived() = runTest {
        val argument: ArgumentCaptor<Group> = ArgumentCaptor.forClass(Group::class.java)
        mockSuccessfulCase()
        homeViewModel.archiveGroup(testGroup)
        verify(updateGroupUseCase, times(1)).invoke(MockitoHelper.capture(argument))
        assertThat(argument.value.archived).isEqualTo(1)
        assertThat( argument.value.sort).isEqualTo(testCurrentTime)
    }

    private companion object {
        val testGroup  =  TestDoubles.testGroup
        val testGroupData = TestDoubles.testGroupData
        const val testCurrentTime : Long = 20
    }

    private fun mockSuccessfulCase() {
        whenever(getGroupsListUseCase.invoke()).thenReturn(
            flow {
                emit(expected)
            }
        )
        homeViewModel = HomeViewModel(getGroupsListUseCase, updateGroupUseCase, currentTime)
    }


}