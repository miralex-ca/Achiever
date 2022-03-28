package com.muralex.achiever.presentation.fragments.group_edit

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.R
import com.muralex.achiever.domain.group_usecases.CreateGroupUseCase
import com.muralex.achiever.domain.group_usecases.DeleteGroupUseCase
import com.muralex.achiever.domain.group_usecases.GetGroupUseCase
import com.muralex.achiever.domain.group_usecases.UpdateGroupUseCase


import com.muralex.achiever.presentation.utils.Constants.GROUP_DESC_LIMIT
import com.muralex.achiever.presentation.utils.Constants.GROUP_TITLE_LIMIT
import com.muralex.achiever.presentation.utils.Constants.SAMPLE_IMAGE
import com.muralex.achiever.presentation.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroupEditViewModel @Inject constructor (
    private val getGroupUseCase: GetGroupUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel() {

    private var savedItem: Group? = null
    private var _isNewItem: Boolean = false

    val formTitle = MutableLiveData<String>()
    val formText = MutableLiveData<String>()
    val formImage = MutableLiveData<String>()
    val isDataChanged = MutableLiveData<Boolean>()

    val isCreated = MutableLiveData<Boolean>().apply { value = true }


    private val titleLimit = GROUP_TITLE_LIMIT
    private val textLimit = GROUP_DESC_LIMIT

    private var _currentLiveState = MutableLiveData<ViewState<ValidationState>>()
    val currentLiveState: LiveData<ViewState<ValidationState>>
        get() = _currentLiveState


    fun getItem(id: String) {

        isDataChanged.value = false

        if (id == "new") {
            _isNewItem = true
            isCreated.value = false
            val newId = System.currentTimeMillis().toString()
            val data = Group(newId, "", "", SAMPLE_IMAGE)
            onItemLoaded(data)

        } else {

            val flowData = getGroupUseCase.invoke(id)

            viewModelScope.launch {

                flowData.collect {
                    it?.let {
                        //savedGroupAndItems = it
                        savedItem = it.group
                        onItemLoaded(it.group)
                    }
                }
            }
        }
    }


    fun isNewGroup() : Boolean {
        return _isNewItem
    }

    fun checkChanges() {
        savedItem?.let {
            isDataChanged.value =
                it.title != formTitle.value ||   it.text != formText.value
                || it.image != formImage.value
        }

        if (_isNewItem) isDataChanged.value = true
    }


    private fun onItemLoaded(item: Group?) {
        item?.let {
            formTitle.value = it.title
            formText.value = it.text
            formImage.value = it.image!!
        }
    }

    fun saveItem() {

        val currentTitle = formTitle.value!!
        val currentText = formText.value!!
        val currentImage = formImage.value!!

        if (validateFields(currentTitle, currentText)) {

            if (_isNewItem) {

                val newId = System.currentTimeMillis().toString()
                val item = Group(newId, currentTitle, currentText, currentImage)

                createItem(item)

            } else {
                savedItem?.let {
                    val item = it.copy(
                        title = currentTitle,
                        text = currentText,
                        image = currentImage
                    )

                    updateItem(item)
                }
            }
        }
    }


    private fun createItem(item: Group) = viewModelScope.launch {
        createGroupUseCase.invoke(item)
        savedItem = item
        _isNewItem = false
        isCreated.value = true
        checkChanges()
    }

    private fun updateItem(item: Group) = viewModelScope.launch {
        savedItem = item
        updateGroupUseCase.invoke(item)
        checkChanges()
    }

    fun setImage(image: String) {
        formImage.value = image
    }


    fun deleteItem() {
        viewModelScope.launch  {
            savedItem?.let {
                deleteGroupUseCase.execute(it)
            }
        }
    }


    private fun validateFields(title: String, text: String): Boolean {
        val invalidFields = arrayListOf<Pair<String, Int>>()
        if (TextUtils.isEmpty(title)) invalidFields.add(INPUT_ITEM_TITLE_EMPTY)
        if (title.length > titleLimit) invalidFields.add(INPUT_ITEM_TITLE_LONG)

        if (text.length > textLimit) invalidFields.add(INPUT_ITEM_TEXT)

        if (invalidFields.isNotEmpty()) {
            _currentLiveState.value = ViewState(ViewState.Status.ERROR,
                data = ValidationState.InvalidFields(invalidFields))
            return false
        }
        return true
    }



    sealed class ValidationState {
        class InvalidFields(val invalidFields: List<Pair<String, Int>>) :
            ValidationState()
    }

    companion object {
        val INPUT_ITEM_TITLE = "INPUT_ITEM_TITLE" to R.string.invalid_item_title
        val INPUT_ITEM_TEXT = "INPUT_ITEM_TEXT" to R.string.invalid_item_text
        val INPUT_ITEM_TITLE_EMPTY = INPUT_ITEM_TITLE.first to R.string.invalid_item_title_empty
        val INPUT_ITEM_TITLE_LONG = INPUT_ITEM_TITLE.first to R.string.invalid_item_title_long
    }


}