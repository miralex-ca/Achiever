package com.muralex.achiever.presentation.fragments.item_edit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.muralex.achiever.R
import com.muralex.achiever.data.models.mappers.CurrentTime
import com.muralex.achiever.databinding.FragmentItemEditBinding
import com.muralex.achiever.presentation.uicomponents.CompletionTypeDialog
import com.muralex.achiever.presentation.uicomponents.ConfirmDialog
import com.muralex.achiever.presentation.uicomponents.RepeatDialog
import com.muralex.achiever.presentation.uicomponents.StatusModeDialog
import com.muralex.achiever.presentation.uicomponents.StatusModeDialog.Companion.DUEDATE_OPTIONS
import com.muralex.achiever.presentation.uicomponents.StatusModeDialog.Companion.REPEAT_OPTIONS
import com.muralex.achiever.presentation.uicomponents.dialog_statuspicker.StatusPickerDialog
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_NO_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED_AFTER
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEAT_PERIOD
import com.muralex.achiever.presentation.utils.Constants.ITEM_COMPLETION_TYPE
import com.muralex.achiever.presentation.utils.Constants.ITEM_NEW_ID
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODES
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_MODE_MANUAL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class EditItemFragment : Fragment(R.layout.fragment_item_edit) {

    private val binding by dataBindings(FragmentItemEditBinding::bind)
    private val viewModel: EditViewModel by viewModels()

    @Inject
    lateinit var currentTime: CurrentTime

    @Inject
    lateinit var repeatDialog: RepeatDialog

    private var itemId = "1"
    private var groupId = "1"
    private var saveButton: MenuItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemId = requireArguments().getString("item", "1")
        groupId = requireArguments().getString("group", "1")
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        binding.btnDeleteItem.setOnClickListener { confirmDeletion() }
        binding.tidSelectStatus.setOnClickListener { openStatusPicker() }
        binding.etDate.setOnClickListener { openDatePicker() }

        binding.etRepeatDate.setOnClickListener {
            openRepeatDatePicker()
        }

        binding.etCompletedDate.setOnClickListener {
            openCompletionDatePicker()
        }

        binding.tidRepeatTask.setOnClickListener { openRepeatDialog() }

        binding.tidStatusMode.setOnClickListener { openStatusModeDialog() }

        binding.tidCompletionType.setOnClickListener { openCompletionTypeDialog() }

        binding.etTitle.addTextChangedListener {
            binding.titleLabel.disableErrorMessage()
        }

        binding.etDesc.addTextChangedListener {
            binding.tilDesc.disableErrorMessage()
        }

        binding.etItemText.addTextChangedListener {
            binding.tilItemText.disableErrorMessage()
        }

        initItemData()

        setHasOptionsMenu(true)
        setToolbarTitle()

    }

    private fun openRepeatDialog() {
        binding.root.findFocus()?.clearFocus()
        binding.tilRepeatTask.setFieldArrow()

        repeatDialog.apply {
            setOnDismissAction { binding.tilRepeatTask.resetFieldArrow() }
            open(viewModel.formRepeat.value) {
                it.replace("_", " ")
                viewModel.formRepeat.value = it
            }
        }
    }

    private fun openStatusModeDialog() {

        binding.root.findFocus()?.clearFocus()
        binding.tilStatusMode.setFieldArrow()

        StatusModeDialog(requireActivity()).apply {
            setOnDismissAction { binding.tilStatusMode.resetFieldArrow() }
            val currentSelectedMode = viewModel.formStatusMode.value
            val checkedOption =
                if (ITEM_STATUS_MODES.contains(currentSelectedMode)) ITEM_STATUS_MODES.indexOf(
                    currentSelectedMode)
                else 0

            val type =
                when (viewModel.formCompletionType.value) {
                    COMPLETION_TYPE_REPEATED -> REPEAT_OPTIONS
                    COMPLETION_TYPE_REPEAT_PERIOD -> REPEAT_OPTIONS
                    COMPLETION_TYPE_REPEATED_AFTER -> REPEAT_OPTIONS
                    else -> DUEDATE_OPTIONS
                }

            open(getString(R.string.dialog_status_mode_title),
                type,
                checkedOption) { selectedPosition ->
                if (selectedPosition in ITEM_STATUS_MODES.indices) {
                    viewModel.formStatusMode.value = ITEM_STATUS_MODES[selectedPosition]
                }
            }
        }
    }

    private fun openCompletionTypeDialog() {

        val completionTypeAdapter = ArrayAdapter(requireContext(), R.layout.select_edit_list_item,
            resources.getStringArray(R.array.set_completion_type_values))
        (binding.actvCompletionType as? AutoCompleteTextView)?.setAdapter(completionTypeAdapter)
        (binding.actvCompletionType as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->
            viewModel.formCompletionType.value = ITEM_COMPLETION_TYPE[position]
            viewModel.setCompletionValues()
        }

        binding.root.findFocus()?.clearFocus()
        binding.tilCompletionType.setFieldArrow()

        CompletionTypeDialog(requireActivity()).apply {

            setOnDismissAction { binding.tilCompletionType.resetFieldArrow() }

            val currentSelectedType = viewModel.formCompletionType.value


            val checkedOption =
                if (ITEM_COMPLETION_TYPE.contains(currentSelectedType)) ITEM_COMPLETION_TYPE.indexOf(
                    currentSelectedType)
                else 0

            val type = 0

            open(getString(R.string.dialog_status_mode_title), checkedOption) { selectedPosition ->
                if (selectedPosition in ITEM_COMPLETION_TYPE.indices) {
                    viewModel.formCompletionType.value = ITEM_COMPLETION_TYPE[selectedPosition]
                    viewModel.setCompletionValues()
                }
            }
        }
    }




    private fun initItemData() {

        viewModel.getItem(itemId)

        viewModel.formTitle.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.formDesc.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.formText.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.formDate.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.formRepeatStart.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.formCompletedDate.observe(viewLifecycleOwner) { viewModel.checkChanges() }

        viewModel.isDataChanged.observe(viewLifecycleOwner) { displayChanges(it) }

        viewModel.formStatusMode.observe(viewLifecycleOwner) {
            var index = 0
            if (ITEM_STATUS_MODES.contains(it)) index = ITEM_STATUS_MODES.indexOf(it)

            val options = resources.getStringArray(R.array.set_status_mode_values)
            binding.tidStatusMode.setText(options[index])
            viewModel.checkChanges()
            addNoteIfAutoMode()
        }

        viewModel.formCompletionType.observe(viewLifecycleOwner) {

            var index = 0
            if (ITEM_COMPLETION_TYPE.contains(it)) index = ITEM_COMPLETION_TYPE.indexOf(it)
            val options = resources.getStringArray(R.array.set_completion_type_values)

            if (!options.indices.contains(index)) index = 0

            (binding.actvCompletionType as? AutoCompleteTextView)?.setText(
                options[index], false
            )

            binding.tidCompletionType.setText(options[index])

            viewModel.checkChanges()
            addNoteIfAutoMode()
        }

        viewModel.formRepeat.observe(viewLifecycleOwner) {
            val periodValues = resources.getStringArray(R.array.period_repeat_values)

            var periodString = ""
            try {
                periodString = processPeriodString(it, periodValues)
            } catch (e: Exception) {

            }
            binding.tidRepeatTask.setText(periodString)
            viewModel.checkChanges()
        }

        viewModel.currentLiveState.observe(viewLifecycleOwner) {
            viewModel.checkChanges()
            handleErrorFields(it.data as EditViewModel.ValidationState.InvalidFields)
        }

        if (viewModel.isNewItem) {
            showKeyboard(binding.etTitle)
        }

        viewModel.newItemCreated.observe(viewLifecycleOwner) { created ->
            if (viewModel.isNewItem && created) {
                findNavController().popBackStack()
            }
        }
    }

    private fun setToolbarTitle() {
        val title = if (itemId == ITEM_NEW_ID) getString(R.string.create_new_item)
        else getString(R.string.edit_item)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun addNoteIfAutoMode() {

        val statusMode = viewModel.formStatusMode.value
        val completionType = viewModel.formCompletionType.value

        if (statusMode != ITEM_STATUS_MODE_MANUAL && completionType != COMPLETION_TYPE_NO_DATE) {
            binding.autoStatusHint.visible()
        } else {
            binding.autoStatusHint.gone()
        }
    }

    private fun displayNoteIfAutoMode(): Boolean {
        val statusMode = viewModel.formStatusMode.value
        val completionType = viewModel.formCompletionType.value
        return statusMode != ITEM_STATUS_MODE_MANUAL && completionType != COMPLETION_TYPE_NO_DATE
    }

    private fun showKeyboard(view: View) {
        Handler(Looper.myLooper()!!).postDelayed({
            val inputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            view.requestFocus()
            inputMethodManager.showSoftInput(view, 0)
        }, 100)
    }

    private fun displayChanges(changed: Boolean) {
        saveButton?.isVisible = changed
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        saveButton = menu.findItem(R.id.action_save_item)
        setSaveButtonOnClick()
        viewModel.checkChanges()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setSaveButtonOnClick() {

        saveButton?.actionView?.findViewById<TextView>(R.id.button_save)?.setOnClickListener {
            viewModel.saveItem(groupId)
            closeKeyboard()
        }
    }

    private fun closeKeyboard() {
        binding.root.findFocus()?.clearFocus()
        val view: View? = requireActivity().findViewById(android.R.id.content)
        view?.let {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun confirmDeletion() {
        ConfirmDialog(requireContext()).open(getString(R.string.confirm_deletion),
            getString(R.string.confirm_delete_msg)) {
            deleteItem()
        }
    }


    private fun deleteItem() {
        lifecycleScope.launch {
            delay(300)
            viewModel.deleteItem()
            findNavController().popBackStack()
        }
    }

    private fun openStatusPicker() {
        binding.root.findFocus()?.clearFocus()
        StatusPickerDialog(requireContext())
            .openDialog(
                ITEM_STATUS.toTypedArray(),
                displayNoteIfAutoMode(),
                viewModel.formStatus.value
            ) { selectedStatus: String -> callBackFromStatusDialog(selectedStatus) }
        closeKeyboard()
    }

    private fun callBackFromStatusDialog(status: String) {
        viewModel.updateItemStatus(status)
    }

    /////// fields validation and errors display and reset

    private fun handleErrorFields(data: EditViewModel.ValidationState.InvalidFields) {
        val validationFields = initValidationFields()
        data.invalidFields.forEach {
            val stringErrorMessage = getString(it.second)
            validationFields[it.first]?.error = stringErrorMessage
        }
    }

    private fun initValidationFields() = mapOf(
        EditViewModel.INPUT_ITEM_TITLE.first to binding.titleLabel,
        EditViewModel.INPUT_ITEM_DESC.first to binding.tilDesc,
        EditViewModel.INPUT_ITEM_TEXT.first to binding.tilItemText
    )

    private fun TextInputLayout.disableErrorMessage() {
        this.error = null
        this.isErrorEnabled = false
    }

    /////////////////////////////////////////////////////
    //////////////////////////////////////////

    private fun openDatePicker() {
        binding.root.findFocus()?.clearFocus()
        val cal = Calendar.getInstance()
        val savedDate = viewModel.formDate.value

        savedDate?.let {
            if (it > 0) {
                try {
                    val dateInMillis = it
                    cal.timeInMillis = dateInMillis
                } catch (e: Exception) {
                    Timber.e("$it can't be converted")
                }
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setEndOfDay(cal)
                updateDateInView(cal)
            }

        val dpd = DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun updateDateInView(cal: Calendar) {
        viewModel.updateItemDate(cal.timeInMillis)
    }

    private fun openRepeatDatePicker() {
        binding.root.findFocus()?.clearFocus()
        val cal = Calendar.getInstance()
        val savedDate = viewModel.formRepeatStart.value

        savedDate?.let {
            if (it > 0) {
                cal.timeInMillis = it
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setStartOfDay(cal)
                updateRepeatDateInView(cal)
            }

        val dpd = DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun updateRepeatDateInView(cal: Calendar) {
        viewModel.updateItemRepeatDate(cal.timeInMillis)
    }


    private fun openCompletionDatePicker() {
        binding.root.findFocus()?.clearFocus()
        val cal = Calendar.getInstance()
        val savedDate = viewModel.formCompletedDate.value

        savedDate?.let {
            if (it > 0) {
                cal.timeInMillis = it
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setStartOfDay(cal)
                updateCompletionDateInView(cal.timeInMillis)
            }

        val dpd = DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH),
        )

        dpd.datePicker.maxDate = currentTime.getMillis()

        dpd.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear") { _, _ ->
            updateCompletionDateInView(0)
        }

        dpd.getButton(DialogInterface.BUTTON_NEGATIVE)?.visibility = View.GONE
        dpd.show()

    }

    private fun updateCompletionDateInView(time: Long) {
        viewModel.updateItemCompletionDate(time)
    }


}