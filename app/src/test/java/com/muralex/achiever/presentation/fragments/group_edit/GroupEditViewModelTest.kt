package com.muralex.achiever.presentation.fragments.group_edit

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.domain.group_usecases.CreateGroupUseCase
import com.muralex.achiever.domain.group_usecases.DeleteGroupUseCase
import com.muralex.achiever.domain.group_usecases.GetGroupWithItemsUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase
import com.muralex.achiever.presentation.fragments.group_detail.GroupDetailViewModel
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.Constants.ITEM_NEW_LIST_ID
import com.muralex.achiever.utilities.BaseUnitTest
import com.muralex.achiever.utilities.TestDoubles
import com.muralex.achiever.utilities.getOrAwaitValueTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class GroupEditViewModelTest : BaseUnitTest() {

    private lateinit var groupEditViewModel: GroupEditViewModel
    private val getGroupWithItemsUseCase: GetGroupWithItemsUseCase = mock()
    private val createGroupUseCase: CreateGroupUseCase = mock()
    private val updateGroupUseCase: UpdateGroupUseCase = mock()
    private val deleteGroupUseCase: DeleteGroupUseCase = mock()
    private val currentTime: CurrentTime = mock()
    private val testCurrentTime : Long = 20
    private val expected = TestDoubles.testGroupWithItemsInGroup
    private val testGroupId = TestDoubles.testGroup.id

    @Before
    fun setUp() {
        groupEditViewModel = GroupEditViewModel(getGroupWithItemsUseCase, createGroupUseCase,
            updateGroupUseCase, deleteGroupUseCase, currentTime)
        whenever( currentTime.getMillis()).thenReturn(testCurrentTime)
    }

    @Test
    fun getGroup_UseCaseExecute_groupMemorized() = runTest {
        mockSuccessfulCase()
        verify(getGroupWithItemsUseCase, times(1)).execute(any())
        assertThat(groupEditViewModel.isCreated.value).isEqualTo(true)
        assertThat(groupEditViewModel.getSavedGroupId()).isEqualTo(testGroupId)
        assertThat(groupEditViewModel.isNewGroup()).isEqualTo(false)
    }

    @Test
    fun getGroup_GroupIsNew_defaultState() = runTest {
        mockNewGroupCase()
        assertThat(groupEditViewModel.isNewGroup()).isEqualTo(true)
        assertThat(groupEditViewModel.isDataChanged.value).isEqualTo(false)
        assertThat(groupEditViewModel.isCreated.value).isEqualTo(false)
    }

    @Test
    fun getGroup_checkChangesReturnFalse() = runTest {
        mockSuccessfulCase()
        groupEditViewModel.checkChanges()
        assertThat(groupEditViewModel.isDataChanged.value).isEqualTo(false)
    }

    @Test
    fun getGroup_changeValue_checkChangesReturnTrue() = runTest {
        mockSuccessfulCase()
        groupEditViewModel.formTitle.value = groupEditViewModel.formTitle.value+"change"
        groupEditViewModel.checkChanges()
        assertThat(groupEditViewModel.isDataChanged.value).isEqualTo(true)
    }

    @Test
    fun saveItem_updateGroupUseCaseIsInvoked() = runTest {
        mockSuccessfulCase()
        groupEditViewModel.saveItem()
        verify(updateGroupUseCase, times(1)).invoke(any())
    }

    @Test
    fun saveItem_longTitle_updateGroupUseCaseNoteInvoked() = runTest {
        mockSuccessfulCase()
        var title = "x"
        repeat(200) { title += "x" }
        groupEditViewModel.formTitle.value = title
        groupEditViewModel.saveItem()
        verify(updateGroupUseCase, times(0)).invoke(any())
        val data = groupEditViewModel.currentLiveState.value?.data as GroupEditViewModel.ValidationState.InvalidFields
        assertThat(data.invalidFields.size).isGreaterThan(0)
    }

    @Test
    fun saveItem_emptyTitle_updateGroupUseCaseNoteInvoked() = runTest {
        mockSuccessfulCase()
        val title = " "
        groupEditViewModel.formTitle.value = title
        groupEditViewModel.saveItem()
        verify(updateGroupUseCase, times(0)).invoke(any())
        val data = groupEditViewModel.currentLiveState.value?.data as GroupEditViewModel.ValidationState.InvalidFields
        assertThat(data.invalidFields.size).isGreaterThan(0)
    }

    @Test
    fun saveItem_GroupIsNew_createGroupUseCaseIsInvoked() = runTest {
        mockNewGroupCase()
        groupEditViewModel.formTitle.value = "test"
        groupEditViewModel.saveItem()
        verify(createGroupUseCase, times(1)).invoke(any())
    }

    @Test
    fun changeDisplayDetails_displayIsTrue_displayChangedToFalse() = runTest {
        mockSuccessfulCase()
        groupEditViewModel.formDetailDisplay.value = 1
        groupEditViewModel.changeDisplayDetails()
        assertThat(groupEditViewModel.formDetailDisplay.value).isEqualTo(0)
    }

    @Test
    fun changeDisplayProgress_displayIsTrue_displayChangedToFalse() = runTest {
        mockSuccessfulCase()
        groupEditViewModel.formProgressDisplay.value = 1
        groupEditViewModel.changeDisplayProgress()
        assertThat(groupEditViewModel.formDetailDisplay.value).isEqualTo(0)
    }

    @Test
    fun setImage_passValue_imageChanged() = runTest {
        mockSuccessfulCase()
        val image = "image"
        groupEditViewModel.setImage(image)
        assertThat(groupEditViewModel.formImage.value).isEqualTo(image)
    }

    @Test
    fun deleteItem_deleteGroupUseCaseInvoked() = runTest {
        mockSuccessfulCase()
        groupEditViewModel.deleteItem()
        verify(deleteGroupUseCase, times(1)).execute(expected.group!!, emptyList())
    }

    private fun mockSuccessfulCase() = runTest {
        whenever(getGroupWithItemsUseCase.execute(any())).thenReturn(
            flow { emit(expected) }
        )
        groupEditViewModel.getItem(testGroupId)
    }

    private fun mockNewGroupCase() = runTest {
        groupEditViewModel.getItem(ITEM_NEW_LIST_ID)
    }



}