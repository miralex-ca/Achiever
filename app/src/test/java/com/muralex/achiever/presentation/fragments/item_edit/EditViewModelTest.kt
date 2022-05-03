package com.muralex.achiever.presentation.fragments.item_edit

import com.example.noter.domain.usecases.GetItemUseCase
import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.mappers.DisplayStatus
import com.muralex.achiever.domain.item_usecases.CreateItemUseCase
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.ITEM_NEW_LIST_ID
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class EditViewModelTest : BaseUnitTest() {

    private lateinit var editViewModel: EditViewModel
    private val createItemUseCase: CreateItemUseCase = mock()
    private val getItemUseCase: GetItemUseCase = mock()
    private val updateItemUseCase: UpdateItemUseCase = mock()
    private val deleteItemUseCase: DeleteItemUseCase = mock()
    private val displayStatus: DisplayStatus = mock()
    private val currentTime: CurrentTime = mock()
    private val testCurrentTime : Long = 20
    private val expected = TestDoubles.testDataItem


    @Before
    fun setUp() {
        editViewModel = EditViewModel(createItemUseCase, getItemUseCase,
            updateItemUseCase, deleteItemUseCase, displayStatus, currentTime)
        whenever( currentTime.getMillis()).thenReturn(testCurrentTime)
        whenever( currentTime.getNewItemId()).thenReturn(testCurrentTime.toString())
    }

    @Test
    fun getItem_UseCaseExecute_groupMemorized() = runTest {
        mockSuccessfulCase()
        verify(getItemUseCase, times(1)).execute(any())
        assertThat(editViewModel.isCreated.value).isEqualTo(true)
        assertThat(editViewModel.isNewItem).isEqualTo(false)
    }


    @Test
    fun getGroup_GroupIsNew_defaultState() = runTest {
        mockNewItemCase()
        assertThat(editViewModel.isCreated.value).isEqualTo(false)
        assertThat(editViewModel.isNewItem).isEqualTo(true)
    }

    @Test
    fun getItem_checkChangesReturnFalse() = runTest {
        mockSuccessfulCase()
        editViewModel.checkChanges()
        assertThat(editViewModel.isDataChanged.value).isEqualTo(false)
    }

    @Test
    fun getItem_changeValue_checkChangesReturnTrue() = runTest {
        mockSuccessfulCase()
        editViewModel.formTitle.value = editViewModel.formTitle.value+"change"
        editViewModel.checkChanges()
        assertThat(editViewModel.isDataChanged.value).isEqualTo(true)
    }

    @Test
    fun saveItem_updateGroupUseCaseIsInvoked() = runTest {
        mockSuccessfulCase()
        editViewModel.saveItem(expected.id)
        verify(updateItemUseCase, times(1)).execute(any())
    }

    @Test
    fun saveItem_emptyTitle_updateGroupUseCaseNoteInvoked() = runTest {
        mockSuccessfulCase()
        editViewModel.formTitle.value = " "
        editViewModel.saveItem(expected.id)
        verify(updateItemUseCase, times(0)).execute(any())
        val data = editViewModel.currentLiveState.value?.data as EditViewModel.ValidationState.InvalidFields
        assertThat(data.invalidFields.size).isGreaterThan(0)
    }

    @Test
    fun saveItem_ItemIsNew_createUseCaseIsInvoked() = runTest {
        mockNewItemCase()
        editViewModel.formTitle.value = "test"
        editViewModel.saveItem(ITEM_NEW_LIST_ID)
        verify(createItemUseCase, times(1)).execute(any())
    }

    @Test
    fun deleteItem_deleteGroupUseCaseInvoked() = runTest {
        mockSuccessfulCase()
        editViewModel.deleteItem()
        verify(deleteItemUseCase, times(1)).execute(expected)
    }

    @Test
    fun updateItemDate_dateIsUpdated() = runTest {
        mockSuccessfulCase()
        val date = 1L
        editViewModel.updateItemDate(date)
        assertThat(editViewModel.formDate.value).isEqualTo(date)
    }

    @Test
    fun updateItemRepeatDate_RepeatDateIsUpdated() = runTest {
        mockSuccessfulCase()
        val date = 1L
        editViewModel.updateItemRepeatDate(date)
        assertThat(editViewModel.formRepeatStart.value).isEqualTo(date)
    }

    @Test
    fun updateItemCompletionDate_CompletionDateIsUpdated() = runTest {
        mockSuccessfulCase()
        val date = 1L
        editViewModel.updateItemCompletionDate(date)
        assertThat(editViewModel.formCompletedDate.value).isEqualTo(date)
    }

    @Test
    fun setCompletionValues_CompletionTypeDueDate_StatusModIsAuto() = runTest {
        mockSuccessfulCase()
        editViewModel.formCompletionType.value = Constants.COMPLETION_TYPE_DUE_DATE
        editViewModel.setCompletionValues()
        assertThat(editViewModel.formStatusMode.value).isEqualTo(Constants.ITEM_STATUS_MODE_AUTO)
    }


    private fun mockSuccessfulCase() = runTest {
        whenever(getItemUseCase.execute(any())).thenReturn(
            flow { emit(expected) }
        )
        editViewModel.getItem(expected.id)
    }

    private fun mockNewItemCase() = runTest {
        editViewModel.getItem(ITEM_NEW_LIST_ID)
    }








}