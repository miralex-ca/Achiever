package com.muralex.achiever.presentation.fragments.group_edit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tasker.presentation.components.imagepicker.ImagePickerDialog
import com.google.android.material.textfield.TextInputLayout
import com.muralex.achiever.R
import com.muralex.achiever.databinding.FragmentGroupEditBinding
import com.muralex.achiever.presentation.activities.search_images.SearchImageResultCallback
import com.muralex.achiever.presentation.uicomponents.ConfirmDialog
import com.muralex.achiever.presentation.utils.Constants.ITEM_NEW_LIST_ID
import com.muralex.achiever.presentation.utils.dataBindings
import com.muralex.achiever.presentation.utils.displayIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class GroupEditFragment : Fragment(R.layout.fragment_group_edit) {

    @Inject
    lateinit var confirmDialog: ConfirmDialog

    @Inject
    lateinit var imagePickerDialog: ImagePickerDialog

    private val binding by dataBindings(FragmentGroupEditBinding::bind)
    private val viewModel: GroupEditViewModel by viewModels()

    private var itemId = "1"
    private var saveButton: MenuItem? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemId = requireArguments().getString("item", "1")
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        setHasOptionsMenu(true)

        binding.imgSelect.setOnClickListener {
            openImagePicker()
        }

        binding.btnDeleteItem.setOnClickListener {
            confirmDeletion()
        }

        binding.btnOpenGroup.setOnClickListener {
            openGroup()
        }

        binding.progressSwitchBox.setOnClickListener {
            viewModel.changeDisplayProgress()
        }

        binding.detailsSwitchBox.setOnClickListener {
            viewModel.changeDisplayDetails()
        }

        binding.etTitle.addTextChangedListener {
            binding.titleLabel.disableErrorMessage()
        }

        binding.etListDesc.addTextChangedListener {
            binding.tilListDesc.disableErrorMessage()
        }

        initItemData()

    }


    private fun setToolbarTitle(isCreated: Boolean) {
        val title = if (!isCreated) {
            getString(R.string.create_new_list)
        } else {
            getString(R.string.edit_list)
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun openImagePicker() {
        imagePickerDialog.apply {
            setOnItemClicker { selected ->
                if (selected == "url") {
                    openSearch()
                } else {
                    selectImage(selected)
                }
            }
            setImages(resources.getStringArray(R.array.item_pics_list).toList())
            open()
        }
    }


    private fun selectImage(image: String) {
        viewModel.setImage(image)
        viewModel.checkChanges()
    }

    private fun initItemData() {

        viewModel.isCreated.observe(viewLifecycleOwner) {
            setToolbarTitle(it)
            checkOpenButtonDisplay()
        }

        viewModel.getItem(itemId)

        viewModel.formTitle.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.formText.observe(viewLifecycleOwner) { viewModel.checkChanges() }
        viewModel.isDataChanged.observe(viewLifecycleOwner) { displayChanges(it) }

        viewModel.formDetailDisplay.observe(viewLifecycleOwner) {
            binding.detailsSwitch.isChecked = it <= 0
            viewModel.checkChanges()
        }

        viewModel.formProgressDisplay.observe(viewLifecycleOwner) {
            binding.progressSwitch.isChecked = it <= 0
            viewModel.checkChanges()
        }

        viewModel.currentLiveState.observe(viewLifecycleOwner) {
            viewModel.checkChanges()
            handleErrorFields(it.data as GroupEditViewModel.ValidationState.InvalidFields)
        }

        if (viewModel.isNewGroup()) {
            showKeyboard(binding.etTitle)
        }


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
            viewModel.saveItem()
            closeKeyboard()
        }
    }

    private fun confirmDeletion() {
        confirmDialog.open(getString(R.string.confirm_deletion),
            getString(R.string.confirm_delete_msg)) {
            deleteItem()
        }
    }

    private fun checkOpenButtonDisplay() {
        val groupId = viewModel.getSavedGroupId()
        binding.btnOpenGroup.displayIf(groupId != ITEM_NEW_LIST_ID)
    }

    private fun openGroup() {
        val groupId = viewModel.getSavedGroupId()
        val bundle = bundleOf("item" to groupId)
        findNavController(). popBackStack()
        findNavController().navigate(R.id.groupDetailFragment, bundle)
    }

    private fun deleteItem() {
        lifecycleScope.launch {
            delay(200)
            viewModel.deleteItem()
            findNavController().popBackStack(R.id.groupDetailFragment, false)
                .and(findNavController().popBackStack())
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


    private fun handleErrorFields(data: GroupEditViewModel.ValidationState.InvalidFields) {
        val validationFields = initValidationFields()
        data.invalidFields.forEach {
            val stringErrorMessage = getString(it.second)
            validationFields[it.first]?.error = stringErrorMessage
        }
    }

    private fun initValidationFields() = mapOf(
        GroupEditViewModel.INPUT_ITEM_TITLE.first to binding.titleLabel,
        GroupEditViewModel.INPUT_ITEM_DESC.first to binding.tilListDesc
    )

    private fun TextInputLayout.disableErrorMessage() {
        this.error = null
        this.isErrorEnabled = false
    }

    private fun openSearch() {
        getContent.launch("")
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private val getContent = registerForActivityResult(SearchImageResultCallback()) {
        if (it[0] != SearchImageResultCallback.SEARCH_IMAGE_FAILED) {
            if (it.size > 0) selectImage(it[0])
        }
    }


}