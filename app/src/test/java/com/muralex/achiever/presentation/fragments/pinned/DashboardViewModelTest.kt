package com.muralex.achiever.presentation.fragments.pinned

import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.domain.item_usecases.GetPinnedItemsUseCase
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_LEFT_BEFORE_ADD
import com.muralex.achiever.presentation.utils.Constants.GROUP_LIST_SCROLL_ADD
import com.muralex.achiever.presentation.utils.Constants.TASK_LIST_START_COUNT
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import com.muralex.achiever.utilities.getOrAwaitValueTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DashboardViewModelTest : BaseUnitTest()   {

    private lateinit var dashboardViewModel: DashboardViewModel

    private val testPinnedItem = TestDoubles.testPinnedItem
    private val getPinnedItemsUseCase: GetPinnedItemsUseCase = mock()
    private val updateItemUseCase: UpdateItemUseCase = mock()
    private val deleteItemUseCase: DeleteItemUseCase = mock()
    private val expected = listOf(testPinnedItem)

    @Test
    fun noChangeListCountIfMaxIsNotReached() = runTest {
        mockSuccessfulCase()
        val listCountMaxNotReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD - 1
        dashboardViewModel.addListItems(listCountMaxNotReached)
        assertThat(dashboardViewModel.maxListSize).isEqualTo(TASK_LIST_START_COUNT)
    }

    @Test
    fun changeListCountIfMaxIsReached() = runTest {
        mockSuccessfulCase()
        val listCountMaxReached = TASK_LIST_START_COUNT - GROUP_LIST_LEFT_BEFORE_ADD + 1
        dashboardViewModel.addListItems(listCountMaxReached)
        assertThat(dashboardViewModel.maxListSize)
            .isEqualTo(TASK_LIST_START_COUNT + GROUP_LIST_SCROLL_ADD)
    }


    @Test
    fun getListIfLifeDataEvent() = runTest {
        mockSuccessfulCase()
        val result = dashboardViewModel.getItems().getOrAwaitValueTest()
        verify(getPinnedItemsUseCase, times(1)).execute()
        assertThat(result).isEqualTo(expected)
    }


    private fun mockSuccessfulCase() = runTest {
        whenever(getPinnedItemsUseCase.execute()).thenReturn(
            flow {
                emit(expected)
            }
        )

        dashboardViewModel = DashboardViewModel(getPinnedItemsUseCase, updateItemUseCase,deleteItemUseCase)
    }


}