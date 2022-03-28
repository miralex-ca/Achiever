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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.databinding.FragmentArchiveBinding
import com.muralex.achiever.presentation.activities.search.SearchResultCallback
import com.muralex.achiever.presentation.components.GroupActionDialog
import com.muralex.achiever.presentation.fragments.home.HomeListAdapter
import com.muralex.achiever.presentation.utils.Constants
import com.muralex.achiever.presentation.utils.dataBindings
import com.muralex.achiever.presentation.utils.safeSlice
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArchiveFragment : Fragment(R.layout.fragment_archive) {

    private val binding by dataBindings(FragmentArchiveBinding::bind)
    private val viewModel: ArchiveViewModel by viewModels()

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

            if (action == Constants.Action.Click) openEdit(groupData)

            if (action == Constants.Action.LongClick) groupActionDialog.openDialog(groupData) { groupAction, group ->
                actionsFromDialog(groupAction, group)
            }

            if (action == Constants.Action.MenuClick) groupActionDialog.openDialog(groupData) { groupAction, group ->
                actionsFromDialog(groupAction, group)
            }
        }

        binding.rvArchiveList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArchiveList.addOnScrollListener(this@ArchiveFragment.onScrollListener)
        binding.rvArchiveList.adapter = listAdapter
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(binding.rvArchiveList)

        loadListData()
    }


    private fun actionsFromDialog(action: Constants.Action, group: GroupData) {
        if (action == Constants.Action.MoveToTop) openMoveToTop(group)
        if (action == Constants.Action.OpenGroup) openEdit(group)
        if (action == Constants.Action.Edit) openEdit(group)
        if (action == Constants.Action.Unarchive) unarchiveGroup(group)
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


    private fun openEdit(group: GroupData) {
        val bundle = bundleOf("item" to group.id)
        findNavController().navigate(R.id.action_nav_archive_to_groupEditFragment, bundle)
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



    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val layoutPosition = viewHolder.layoutPosition

            val group  = allDataList[layoutPosition]
            unarchiveGroup( group )

        }


        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (viewHolder != null) {

                val foregroundView: View =   (viewHolder as HomeListAdapter.ViewHolder).viewForeground

                getDefaultUIUtil().onSelected(foregroundView)
            }
        }

        override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            val foregroundView: View = (viewHolder as HomeListAdapter.ViewHolder).viewForeground

            getDefaultUIUtil().onDrawOver(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        }


        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            //super.clearView(recyclerView, viewHolder)
            val foregroundView: View = (viewHolder as HomeListAdapter.ViewHolder).viewForeground
            getDefaultUIUtil().clearView(foregroundView)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            // super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            val swipeClearHome: View = (viewHolder as HomeListAdapter.ViewHolder).swipeArchiveLeft
            val swipeArchive: View = (viewHolder as HomeListAdapter.ViewHolder).swipeArchiveRight

            if (dX < 0) {
                swipeClearHome.visibility = View.GONE
                swipeArchive.visibility = View.VISIBLE
            } else {
                swipeClearHome.visibility = View.VISIBLE
                swipeArchive.visibility = View.GONE
            }


            val foregroundView: View = (viewHolder as HomeListAdapter.ViewHolder).viewForeground

            getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )

        }

    }



}

