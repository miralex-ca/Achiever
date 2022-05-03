package com.muralex.achiever.presentation.fragments.archive

import android.graphics.Canvas
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.databinding.FragmentArchiveBinding
import com.muralex.achiever.presentation.activities.search.SearchResultCallback
import com.muralex.achiever.presentation.uicomponents.GroupActionDialog
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.Constants.ITEM_ID_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveFragment : Fragment(R.layout.fragment_archive) {

    private val binding by dataBindings(FragmentArchiveBinding::bind)
    private val viewModel: ArchiveViewModel by viewModels()

    @Inject
    lateinit var groupActionDialog: GroupActionDialog
    @Inject
    lateinit var settings: SettingsHelper

    private lateinit var listAdapter: ArchiveListAdapter
    private lateinit var allDataList: List<GroupData>

    private var isScrolling = false
    private var isLoading = false

    private var emptyBox: View? = null
    private var loadProgress: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root
        emptyBox = binding.emptyArchiveList
        loadProgress = binding.loaderProgress
        initRecyclerView()
        setHasOptionsMenu(true)

    }

    private fun initRecyclerView() {

        listAdapter = ArchiveListAdapter()
        listAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        listAdapter.setOnItemClickListener { action, groupData ->
            setClickEvents(action, groupData)
        }

        binding.rvArchiveList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArchiveList.addOnScrollListener(this@ArchiveFragment.onScrollListener)
        binding.rvArchiveList.adapter = listAdapter
        if (settings.isSwipeEnabled()) {
            ItemTouchHelper(swipeCallBack).attachToRecyclerView(binding.rvArchiveList)
        }

        loadListData()
    }

    private fun setClickEvents(
        action: Action,
        groupData: GroupData,
    ) {
        if (action == Action.Click) openGroup(groupData)

        if (action == Action.LongClick || action == Action.MenuClick) {
            groupActionDialog.openDialog(groupData) { groupAction, group ->
                actionsFromDialog(groupAction, group)
            }
        }
    }

    private fun actionsFromDialog(action: Action, group: GroupData) {
        if (action == Action.MoveToTop) openMoveToTop(group)
        if (action == Action.OpenGroup) openGroup(group)
        if (action == Action.Edit) openEdit(group)
        if (action == Action.Unarchive) unarchiveGroup(group)
    }

    private fun unarchiveGroup(group: GroupData) {
        group.group?.let {
            viewModel.unarchiveGroup(it)
        }
    }

    private fun openMoveToTop(group: GroupData) {
        group.group?.let {
            viewModel.moveGroupToUp(it)
        }
    }

    private fun openGroup(group: GroupData) {
        val bundle = bundleOf(ITEM_ID_KEY to group.id)
        findNavController().navigate(R.id.action_nav_archive_to_groupDetailFragment, bundle)
    }

    private fun openEdit(group: GroupData) {
        val bundle = bundleOf(ITEM_ID_KEY to group.id)
        findNavController().navigate(R.id.action_nav_archive_to_groupEditFragment, bundle)
    }

    private fun loadListData() {
        viewModel.groupsLIst.observe(viewLifecycleOwner) {
           lifecycleScope.launch(Dispatchers.IO) {
               allDataList = it.sortedByDescending { group -> group.group?.sort }
               withContext(Dispatchers.Main) {
                   submitListWithLimit()
               }
           }
        }

        viewModel.loader.observe(viewLifecycleOwner) {
            loadProgress?.displayIf(it)
        }

        viewModel.displayEmptyList.observe(viewLifecycleOwner) { emptyList ->
            lifecycleScope.launch {
                delay(200)
                emptyBox?.displayIf(emptyList)
            }
        }
    }

    private fun submitListWithLimit() {
        val list = allDataList.safeSlice(viewModel.maxListSize)
        listAdapter.submitList(list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.archive_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                openSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun openSearch() {
        getContent.launch("")
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private val getContent = registerForActivityResult(SearchResultCallback()) {

        if (it[0] != SearchResultCallback.SEARCH_FAILED) {

            if (it[0] == SearchResultCallback.SEARCH_GROUP_RESULT_EXTRA) {
                val bundle = bundleOf(ITEM_ID_KEY to it[1])
                findNavController().navigate(R.id.action_nav_archive_to_groupDetailFragment, bundle)
            } else {
                val bundle = bundleOf(ITEM_ID_KEY to it[1])
                findNavController().navigate(R.id.action_nav_archive_to_editFragment, bundle)
            }
        }
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
            val layoutManager = binding.rvArchiveList.layoutManager as LinearLayoutManager
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


    private val swipeCallBack =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val layoutPosition = viewHolder.layoutPosition
                val group = allDataList[layoutPosition]
                unarchiveGroup(group)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (viewHolder != null) {
                    val foregroundView: View =
                        (viewHolder as ArchiveListAdapter.ViewHolder).viewForeground
                    getDefaultUIUtil().onSelected(foregroundView)
                }
            }

            override fun onChildDrawOver(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean,
            ) {
                val foregroundView: View = (viewHolder as ArchiveListAdapter.ViewHolder).viewForeground
                getDefaultUIUtil().onDrawOver(
                    c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive
                )
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
            ) {
                val foregroundView: View = (viewHolder as ArchiveListAdapter.ViewHolder).viewForeground
                getDefaultUIUtil().clearView(foregroundView)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {

                val swipeClearHome: View =
                    (viewHolder as ArchiveListAdapter.ViewHolder).swipeArchiveLeft
                val swipeArchive: View = viewHolder.swipeArchiveRight

                if (dX < 0) {
                    swipeClearHome.visibility = View.GONE
                    swipeArchive.visibility = View.VISIBLE
                } else {
                    swipeClearHome.visibility = View.VISIBLE
                    swipeArchive.visibility = View.GONE
                }

                val foregroundView: View = viewHolder.viewForeground

                getDefaultUIUtil().onDraw(
                    c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive
                )
            }
        }
}

