package com.muralex.achiever.presentation.fragments.group_detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.R
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.ItemInGroup
import com.muralex.achiever.databinding.FragmentGroupDetailBinding
import com.muralex.achiever.presentation.uicomponents.ConfirmDialog
import com.muralex.achiever.presentation.uicomponents.dialog_item_actions.ItemActionDialog
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.Constants.ITEM_ID_KEY
import com.muralex.achiever.presentation.utils.SettingsHelper
import com.muralex.achiever.presentation.utils.dataBindings
import com.muralex.achiever.presentation.utils.safeSlice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GroupDetailFragment : Fragment(R.layout.fragment_group_detail) {

    private val binding by dataBindings(FragmentGroupDetailBinding::bind)
    private val viewModel: GroupDetailViewModel by viewModels()

    @Inject
    lateinit var confirmDialog: ConfirmDialog
    @Inject
    lateinit var itemActionDialog: ItemActionDialog

    @Inject
    lateinit var settings: SettingsHelper

    private lateinit var listAdapter: ItemsListAdapter
    private lateinit var allDataList: List<GroupDetailItem>

    private var itemId = "1"
    private var isScrolling = false
    private var isLoading = false


    private var archiveMenuItem: MenuItem? = null
    private var unarchiveMenuItem: MenuItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root
        itemId = requireArguments().getString(ITEM_ID_KEY, "1")
        viewModel.setGroupId(itemId)
        initRecyclerView()
        setHasOptionsMenu(true)
    }

    private fun initRecyclerView() {
        listAdapter = ItemsListAdapter()
        listAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        listAdapter.setOnItemClickListener { action, item ->
            if (action == Action.Click) openItemDetail(item.data)
            if (action == Action.LongClick) openActionsDialog(item)
            if (action == Action.TodoStatus) checkTodo(item)
        }

        listAdapter.setOnItemGroupListener { action, group ->
            if (action == Action.OpenGroup) openGroupDetail(group.id.toString())
        }

        binding.rvItemsList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItemsList.addOnScrollListener(this@GroupDetailFragment.onScrollListener)
        binding.rvItemsList.adapter = listAdapter
        binding.rvItemsList.itemAnimator = ItemAnimator()

        loadListData()
    }

    private fun openActionsDialog(item: ItemInGroup) {
        itemActionDialog
            .openDialog(item.data, item.displayStatus ) { action: Action, returnedItem: DataItem ->
                callBackFromStatusDialog(action, returnedItem)
            }
    }

    private fun callBackFromStatusDialog(action: Action, item: DataItem) {

        if (action == Action.EditStatus) changeItemStatus(item)
        if (action == Action.Edit) openItemDetail(item)
        if (action == Action.Pin) changePin(item)

        if (action == Action.Delete) {
            lifecycleScope.launch {
                delay(100)
                deleteTask(item)
            }
        }
    }

    private fun changePin(item: DataItem) {
        viewModel.changePin(item)
    }

    private fun checkTodo(item: ItemInGroup) {
        viewModel.checkTodo(item)
    }

    private fun changeItemStatus(item: DataItem) {
        viewModel.changeItemStatus(item)
    }

    private fun deleteTask(item: DataItem) {
        val confirmDeletion = settings.isDeleteConfirmEnabled()
        if (confirmDeletion) confirmItemDeletion(item)
        else deleteItem(item)
    }

    private fun confirmItemDeletion(dataItem: DataItem) {
        lifecycleScope.launch {
            delay(150)
            confirmDialog.open(getString(R.string.confirm_deletion),
                    getString(R.string.confirm_delete_msg)) {
                    deleteItem(dataItem)
                }
        }
    }

    private fun deleteItem(dataItem: DataItem) {
        lifecycleScope.launch {
            delay(200)
            viewModel.deleteItem(dataItem)
        }
    }

    private fun loadListData() {

        viewModel.getGroup().observe(viewLifecycleOwner) {

            val list = it.items.sortedBy { it.sortByStatus }

            var detailDisplay = true

            it.group?.let {
               if ( it.displayDetail  > 0) detailDisplay = false
            }

           var allList   = if (detailDisplay) {

               listOf( GroupDetailItem.GroupDetailInfo(it.groupData!!)) + list.map { item ->
                   GroupDetailItem.TaskInfo(item)
                    }
               } else {
                list.map { item ->
                   GroupDetailItem.TaskInfo(item)
               }
           }

            if (list.isEmpty()) {
                allList = allList + listOf( GroupDetailItem.GroupDetailEmptyList(true))
            }

            allDataList = allList

            submitListWithLimit()
            setContent(it.group)
            checkArchiveItemIcon()

        }
    }

    private fun submitListWithLimit() {
        val list = allDataList.safeSlice(viewModel.maxListSize)
        listAdapter.submitList(list)

    }

    private fun setContent(item: Group?) {
        if (binding != null) {
            binding.floatingActionButton.setOnClickListener {
                createItem()
            }
            lifecycleScope.launch {
                delay(250)
                binding.floatingActionButton.show()
            }
        }
    }

    private fun createItem() {
        val bundle = bundleOf("item" to "new", "group" to itemId)
        findNavController().navigate(R.id.action_groupDetailFragment_to_editFragment, bundle)
    }

    private fun openGroupDetail(groupId: String) {
        val bundle = bundleOf("item" to groupId)
        findNavController().navigate(R.id.action_groupDetailFragment_to_groupEditFragment, bundle)
    }

    private fun openItemDetail(item: DataItem) {
        val bundle = bundleOf("item" to item.id)
        findNavController().navigate(R.id.action_groupDetailFragment_to_editFragment, bundle)
    }


    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.rvItemsList.layoutManager as LinearLayoutManager
            val sizeOfCurrentList = layoutManager.itemCount
            val visibleItems = layoutManager.childCount
            val topPosition = layoutManager.findFirstVisibleItemPosition()

            val hasReachedToEnd = topPosition + visibleItems >= sizeOfCurrentList - 3
            val shouldAddPages = !isLoading && hasReachedToEnd && isScrolling

            if (shouldAddPages) {
                viewModel.addListItems(sizeOfCurrentList)
                submitListWithLimit()

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group, menu)
        archiveMenuItem = menu.findItem(R.id.action_archive)
        unarchiveMenuItem = menu.findItem(R.id.action_unarchive)
        checkArchiveItemIcon()

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun checkArchiveItemIcon() {
        archiveMenuItem?.isVisible = !viewModel.listIsArchived()
        unarchiveMenuItem?.isVisible = viewModel.listIsArchived()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_unarchive -> {
                confirmDialog.open(
                    "Unarchive the list",
                    "\nDo you want to unarchive the item?") {
                    unarchiveGroup()
                }
                return true
            }

            R.id.action_archive -> {
                confirmDialog.open(
                    "Archive the list",
                    "\nDo you want to archive the list?") {
                    archiveGroup()
                }
                return true
            }

            R.id.action_edit -> {
                openGroupDetail(itemId)
                return true
            }

            R.id.action_add_task-> {
                createItem()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun unarchiveGroup() {
        viewModel.archiveGroup(Action.Unarchive)
    }

    private fun archiveGroup() {
        viewModel.archiveGroup(Action.Archive)
    }


}