package com.muralex.achiever.presentation.fragments.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.databinding.FragmentHomeBinding
import com.muralex.achiever.presentation.activities.search.SearchResultCallback
import com.muralex.achiever.presentation.components.GroupActionDialog
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.dataBindings
import com.muralex.achiever.presentation.utils.safeSlice
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by dataBindings(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var groupActionDialog: GroupActionDialog

    private lateinit var listAdapter: HomeListAdapter
    private lateinit var allDataList: List<GroupData>

    private var isScrolling = false
    private var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root

        initRecyclerView()

        setHasOptionsMenu(true)
    }

    private fun initRecyclerView() {

        listAdapter = HomeListAdapter()
        listAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        listAdapter.setOnItemClickListener { action, groupData ->

            if (action == Action.Click) openEdit(groupData)

            if (action == Action.LongClick) groupActionDialog.openDialog(groupData) { groupAction, group ->
                actionsFromDialog(groupAction, group)
            }

            if (action == Action.MenuClick) groupActionDialog.openDialog(groupData) { groupAction, group ->
                actionsFromDialog(groupAction, group)
            }
        }

        binding.rvHomeList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHomeList.addOnScrollListener(this@HomeFragment.onScrollListener)
        binding.rvHomeList.adapter = listAdapter

        loadListData()
    }


    private fun actionsFromDialog(action: Action, group: GroupData) {
        if (action == Action.MoveToTop) openMoveToTop(group)
        if (action == Action.OpenGroup) openEdit(group)
        if (action == Action.Edit) openEdit(group)
        if (action == Action.Archive) archiveGroup(group)
    }

    private fun archiveGroup(group: GroupData) {
        group.group?.let {
            viewModel.archiveGroup(it)
        }
    }

    private fun openMoveToTop(group: GroupData) {
        group.group?.let {
            viewModel.moveGroupToUp(it)
        }
    }


    private fun openEdit(group: GroupData) {
        val bundle = bundleOf("item" to group.id)
        findNavController().navigate(R.id.action_nav_home_to_groupEditFragment, bundle)
    }

    private fun loadListData() {
        viewModel.groupsLIst.observe(viewLifecycleOwner) {
            allDataList = it.sortedByDescending { it.group?.sort }
            submitListWithLimit()
        }
    }

    private fun submitListWithLimit() {
        val list = allDataList.safeSlice(viewModel.maxListSize)
        listAdapter.submitList(list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_add_item -> {
                createGroup()
                return true
            }

            R.id.action_search -> {
                openSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createGroup() {
        val bundle = bundleOf("item" to "new")
        findNavController().navigate(R.id.action_nav_home_to_groupEditFragment, bundle)
    }

    private fun openSearch() {
        getContent.launch("")
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private val getContent = registerForActivityResult(SearchResultCallback()) {
        /// handle search results
//        if (it[0] != SEARCH_FAILED) {
//
//        }
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
            val layoutManager = binding.rvHomeList.layoutManager as LinearLayoutManager
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
}

