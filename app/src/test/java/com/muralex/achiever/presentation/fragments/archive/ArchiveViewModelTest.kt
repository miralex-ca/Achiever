package com.muralex.achiever.presentation.fragments.archive

import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.datamodels.DataItemAndGroup
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.mappers.PinnedItemsMapperTest
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.domain.group_usecases.GetArchivedGroupsListUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_START_COUNT
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.BaseUnitTest.MockitoHelper.capture
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
class ArchiveViewModelTest : BaseUnitTest() {

    private lateinit var archiveViewModel: ArchiveViewModel
    private val getArchivedGroupsListUseCase: GetArchivedGroupsListUseCase = mock()
    private val updateGroupUseCase: UpdateGroupUseCase = mock()
    private val currentTime: CurrentTime = mock()
    private val expected = listOf(testGroupData)


    @Before
    fun setUp() {
        whenever( currentTime.getMillis()).thenReturn(testCurrentTime)
    }

    @Test
    fun noChangeListCountIfMaxIsNotReached() = runTest {
        mockSuccessfulCase()
        val listCountMaxNotReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD - 1
        archiveViewModel.addListItems(listCountMaxNotReached)
        assertThat(archiveViewModel.maxListSize).isEqualTo(TASK_LIST_START_COUNT)
    }

    @Test
    fun changeListCountIfMaxIsReached() = runTest {
        mockSuccessfulCase()
        val listCountMaxReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD + 1
        archiveViewModel.addListItems(listCountMaxReached)
        assertThat(archiveViewModel.maxListSize)
            .isEqualTo(TASK_LIST_START_COUNT + Constants.GROUP_LIST_SCROLL_ADD)
    }

    @Test
    fun getGroupsListIfLifeDataEvent() = runTest {
        mockSuccessfulCase()
        archiveViewModel.groupsLIst.getOrAwaitValueTest()
        verify(getArchivedGroupsListUseCase, times(1)).invoke()
    }

    @Test
    fun emitsDataFromGroupsListUseCase()  = runBlockingTest {
        mockSuccessfulCase()
        assertThat(archiveViewModel.groupsLIst.getOrAwaitValueTest()).isEqualTo(expected)
    }

    @Test
    fun updateGroupSortTimeIfMovedUp() = runTest {
        val argument: ArgumentCaptor<Group> = ArgumentCaptor.forClass(Group::class.java)
        mockSuccessfulCase()
        archiveViewModel.moveGroupToUp(testGroup)
        verify(updateGroupUseCase, times(1)).invoke(capture(argument))
        assertThat( argument.value.sort).isEqualTo(testCurrentTime)
    }

    @Test
    fun updateGroupArchivedAndSortTimeIfChangesArchived() = runTest {
        val argument: ArgumentCaptor<Group> = ArgumentCaptor.forClass(Group::class.java)
        mockSuccessfulCase()

        archiveViewModel.unarchiveGroup(testGroup)
        verify(updateGroupUseCase, times(1)).invoke(capture(argument))
        assertThat(argument.value.archived).isEqualTo(0)
        assertThat( argument.value.sort).isEqualTo(testCurrentTime)
    }


    private fun mockSuccessfulCase()  = runTest {
        whenever(getArchivedGroupsListUseCase.invoke()).thenReturn(
            flow {
                emit(expected)
            }
        )

        archiveViewModel = ArchiveViewModel(getArchivedGroupsListUseCase, updateGroupUseCase, currentTime)
    }

    private companion object {
        val testGroup  =  TestDoubles.testGroup
        val testGroupData = TestDoubles.testGroupData
        const val testCurrentTime : Long = 20
    }



}