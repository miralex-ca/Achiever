package com.muralex.achiever.presentation.fragments.item_edit


import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noter.domain.usecases.GetItemUseCase
import com.example.tasker.domain.item_usecases.UpdateItemUseCase
import com.muralex.achiever.R
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.data.models.mappers.DisplayStatus
import com.muralex.achiever.domain.item_usecases.CreateItemUseCase
import com.muralex.achiever.domain.item_usecases.DeleteItemUseCase
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_DUE_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_NO_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED_AFTER
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEAT_PERIOD
import com.muralex.achiever.presentation.utils.Constants.ITEM_COMPLETION_TYPE
import com.muralex.achiever.presentation.utils.Constants.ITEM_DESC_LIMIT
import com.muralex.achiever.presentation.utils.Constants.ITEM_NEW_ID
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODES
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_AUTO
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_MANUAL
import com.muralex.achiever.presentation.utils.Constants.ITEM_TEXT_LIMIT
import com.muralex.achiever.presentation.utils.Constants.ITEM_TITLE_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class EditViewModel @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val getItemUseCase: GetItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val displayStatus: DisplayStatus,
    private val currentTime: CurrentTime
) : ViewModel() {

    private var savedItem: DataItem? = null
    private var _isNewItem: Boolean = false
    val isNewItem: Boolean
        get() = _isNewItem

    val isCreated = MutableLiveData<Boolean>().apply { value = false }

    val newItemCreated = MutableLiveData<Boolean>().apply { value = false }


    private var dataLoaded = false

    val formTitle = MutableLiveData<String>()
    val formDesc = MutableLiveData<String>()
    val formText = MutableLiveData<String>()
    val formStatus = MutableLiveData<String>()
    val formDate = MutableLiveData<Long>()
    val formRepeatStart = MutableLiveData<Long>()
    val formCompletedDate = MutableLiveData<Long>()

    val formStatusMode = MutableLiveData<String>()
    val formCompletionType = MutableLiveData<String>()

    val formRepeat = MutableLiveData<String>()
    val isDataChanged = MutableLiveData<Boolean>()

    val displayAutoStatus = MutableLiveData<String>()

    val titleLimit = ITEM_TITLE_LIMIT
    val descLimit = ITEM_DESC_LIMIT
    val textLimit = ITEM_TEXT_LIMIT

    private var _currentLiveState = MutableLiveData<ViewState<ValidationState>>()
    val currentLiveState: LiveData<ViewState<ValidationState>>
        get() = _currentLiveState

    fun getItem(id: String) {

        isDataChanged.value = false

        if (id == ITEM_NEW_ID) {
            _isNewItem = true
            val newId = System.currentTimeMillis().toString()
            val data = DataItem(newId, "", "")

            onItemLoaded(data)

        } else {
            isCreated.value = true
            viewModelScope.launch {
                val flowData = getItemUseCase.execute(id)
                flowData.collect {
                    savedItem = it
                    onItemLoaded(it)
                }
            }
        }
    }

    fun checkChanges() {
        savedItem?.let {
            isDataChanged.value =
                it.title != formTitle.value || it.desc != formDesc.value || it.text != formText.value
                        || it.status != formStatus.value || it.completion != formDate.value || it.statusMode != formStatusMode.value
                        || it.completionType != formCompletionType.value || it.repeat != formRepeat.value
                        || it.repeatStart != formRepeatStart.value || it.completedTime != formCompletedDate.value

            checkDisplayStatus()
        }

        if (_isNewItem) isDataChanged.value = true


    }

    private fun onItemLoaded(item: DataItem?) {
        item?.let {
            formTitle.value = it.title
            formDesc.value = it.desc
            formText.value = it.text
            formStatus.value = it.status
            formDate.value = it.completion
            formStatusMode.value = it.statusMode
            formCompletionType.value = it.completionType
            formRepeat.value = it.repeat
            formRepeatStart.value = it.repeatStart
            formCompletedDate.value = it.completedTime

            if (!ITEM_STATUS_MODES.contains(formStatusMode.value))
                formStatusMode.value = ITEM_STATUS_MODES[0]

            if (!ITEM_COMPLETION_TYPE.contains(formCompletionType.value))
                formCompletionType.value = ITEM_COMPLETION_TYPE[0]

            dataLoaded = true
            checkDisplayStatus()
        }
    }

    fun saveItem(groupId: String) {

        val currentTitle = formTitle.value!!
        val currentDesc = formDesc.value!!
        val currentText = formText.value!!

        if (validateFields(currentTitle, currentDesc, currentText)) {

            if (_isNewItem) {
                val newId = currentTime.getNewItemId()
                val item = DataItem(newId, currentTitle, currentDesc, currentText)
                item.groupId = groupId
                item.created = currentTime.getMillis()

                val newItem = addDataFromForm(item)
                createItem(newItem)

            } else {
                savedItem?.let {
                    val item = addDataFromForm(it)
                    updateItem(item)
                }
            }
        }
    }

    private fun checkDisplayStatus() {
        if (!dataLoaded) return
        val statusMode = formStatusMode.value
        val completionType = formCompletionType.value

        if ( statusMode != ITEM_STATUS_MODE_MANUAL && completionType != COMPLETION_TYPE_NO_DATE) {
            val data = addDataFromForm(DataItem("","",""))
            val display = displayStatus.calculate(data)
            displayAutoStatus.value = display
        }
    }

    private fun addDataFromForm(dataItem: DataItem) : DataItem {

        val currentTitle = formTitle.value!!
        val currentDesc = formDesc.value!!
        val currentText = formText.value!!
        val currentStatusMode = formStatusMode.value!!
        val currentCompletionType = formCompletionType.value!!
        val currentRepeat = formRepeat.value!!
        val currentStatus = formStatus.value!!
        val completionTime = formDate.value!!
        val completedTime= formCompletedDate.value!!

        val repeatStartTime = if ((formCompletionType.value == COMPLETION_TYPE_REPEATED || formCompletionType.value == COMPLETION_TYPE_REPEAT_PERIOD
                    || formCompletionType.value == COMPLETION_TYPE_REPEATED_AFTER)
            && formRepeatStart.value!! > 0
        ) {
            formRepeatStart.value!!
        } else {
            0
        }

      return  dataItem.copy(
          title = currentTitle,
          desc = currentDesc,
          text = currentText,
          completion = completionTime,
          statusMode = currentStatusMode,
          completionType = currentCompletionType,
          repeat = currentRepeat,
          repeatStart = repeatStartTime,
          status = currentStatus,
          completedTime = completedTime
      )
    }

    private fun createItem(item: DataItem) = viewModelScope.launch {
        createItemUseCase.execute(item)
        savedItem = item
        newItemCreated.value = true

        checkChanges()
    }

    fun updateItemStatus(status: String) {

        if (status == ITEM_STATUS[5]) formCompletedDate.value = System.currentTimeMillis()
        else {
            formCompletedDate.value  = 0
            savedItem?.let {
                formCompletedDate.value = savedItem?.completedTime
            }
        }

        formStatus.value = status
        checkChanges()
    }


    private fun updateItem(item: DataItem) = viewModelScope.launch {
        savedItem = item
        updateItemUseCase.execute(item)
        checkChanges()
    }

    fun deleteItem() {
        viewModelScope.launch {
            savedItem?.let {
                deleteItemUseCase.execute(it)
            }
        }
    }

    private fun validateFields(title: String, desc: String, text: String): Boolean {
        val invalidFields = arrayListOf<Pair<String, Int>>()

        if (TextUtils.isEmpty(title)) invalidFields.add(INPUT_ITEM_TITLE_EMPTY)
        if (title.length > titleLimit) invalidFields.add(INPUT_ITEM_TITLE_LONG)

        if (desc.length > descLimit) invalidFields.add(INPUT_ITEM_DESC)
        if (text.length > textLimit) invalidFields.add(INPUT_ITEM_TEXT)

        if (invalidFields.isNotEmpty()) {
            _currentLiveState.value = ViewState(ViewState.Status.ERROR,
                data = ValidationState.InvalidFields(invalidFields))
            return false
        }
        return true
    }

    fun updateItemDate(date: Long) {
        formDate.value = date
    }

    fun updateItemRepeatDate(date: Long) {
        formRepeatStart.value = date
    }

    fun updateItemCompletionDate(date: Long) {
        formCompletedDate.value = date
    }

    fun setCompletionValues() {

        if (formCompletionType.value == COMPLETION_TYPE_DUE_DATE) {
            val cal = Calendar.getInstance()
            setEndOfDay(cal)
            var time = cal.timeInMillis
            savedItem?.let { if (it.completion > 0) time = it.completion }
            formDate.value = time
            formStatusMode.value = ITEM_STATUS_MODE_AUTO

        } else if (formCompletionType.value == COMPLETION_TYPE_REPEATED || formCompletionType.value == COMPLETION_TYPE_REPEAT_PERIOD
                || formCompletionType.value == COMPLETION_TYPE_REPEATED_AFTER) {

            val cal = Calendar.getInstance()
            setStartOfDay(cal)
            var time = cal.timeInMillis
            savedItem?.let { if (it.repeatStart > 0) time = it.repeatStart }
            formRepeatStart.value = time
            formStatusMode.value = ITEM_STATUS_MODE_AUTO
        }
    }

    sealed class ValidationState {
        class InvalidFields(val invalidFields: List<Pair<String, Int>>) :
            ValidationState()
    }

    companion object {
        val INPUT_ITEM_TITLE = "INPUT_ITEM_TITLE" to R.string.invalid_item_title
        val INPUT_ITEM_DESC = "INPUT_ITEM_DESC" to R.string.invalid_item_desc
        val INPUT_ITEM_TEXT = "INPUT_ITEM_TEXT" to R.string.invalid_item_text

        val INPUT_ITEM_TITLE_EMPTY = INPUT_ITEM_TITLE.first to R.string.invalid_item_title_empty
        val INPUT_ITEM_TITLE_LONG = INPUT_ITEM_TITLE.first to R.string.invalid_item_title_long
    }


}