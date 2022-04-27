package com.muralex.achiever.presentation.fragments.pinned

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.databinding.FragmentDashboardBinding
import com.muralex.achiever.presentation.activities.search.SearchResultCallback
import com.muralex.achiever.presentation.components.ConfirmDialog
import com.muralex.achiever.presentation.components.dialog_item_actions.ItemActionDialog
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.Action
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding by dataBindings(FragmentDashboardBinding::bind)
    private val viewModel: DashboardViewModel by viewModels()

    @Inject
    lateinit var confirmDialog: ConfirmDialog
    @Inject
    lateinit var settings: SettingsHelper
    @Inject
    lateinit var itemActionDialog: ItemActionDialog

    private lateinit var listAdapter: PinnedItemsListAdapter
    private lateinit var allDataList: List<PinnedItem>

    private var isScrolling = false
    private var isLoading = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setHasOptionsMenu(true)
    }


    private fun initRecyclerView() {
        listAdapter = PinnedItemsListAdapter()
        listAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        listAdapter.setOnItemClickListener { action, item ->
            if (action == Action.Click) openItemDetail(item.data)
            if (action == Action.LongClick) openActionsDialog(item)
            if (action == Action.TodoStatus) checkTodo(item.data)
            if (action == Action.OpenGroup) openGroup(item.groupData)
        }

        binding.rvDashboardList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDashboardList.adapter = listAdapter
        binding.rvDashboardList.addOnScrollListener(this@DashboardFragment.onScrollListener)
        if (settings.isSwipeEnabled()) {
            ItemTouchHelper(swipeCallBack).attachToRecyclerView(binding.rvDashboardList)
        }
        loadListData()
    }


    private fun openActionsDialog(item: PinnedItem) {
        itemActionDialog.openDialog(item.data, item.displayStatus) { action: Action, reternedItem: DataItem ->
            callBackFromActionsDialog(action, reternedItem)
        }
    }

    private fun callBackFromActionsDialog(action: Action, item: DataItem) {
        if (action == Action.EditStatus) changeItemStatus(item)
        if (action == Action.Edit) openItemDetail(item)
        if (action == Action.Pin) changePin(item)
        if (action == Action.Delete) {
            Handler(Looper.myLooper()!!).postDelayed({
                confirmItemDeletion(item)
            }, 100)
        }
    }

    private fun changePin(item: DataItem) {
        viewModel.changePin(item)
    }

    private fun checkTodo(item: DataItem) {
        viewModel.checkTodo(item)
    }

    private fun changeItemStatus(item: DataItem) {
        viewModel.changeItemStatus(item)
    }

    private fun confirmItemDeletion(dataItem: DataItem) {
        lifecycleScope.launch {
            delay(150)
            confirmDialog.open(getString(R.string.confirm_deletion), getString(R.string.confirm_delete_msg)) {
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

    private fun openItemDetail(item: DataItem) {
        val bundle = bundleOf("item" to item.id)
        findNavController().navigate(R.id.action_nav_dashboard_to_editFragment, bundle)
    }

    private fun openGroup(groupData: Group) {
        val bundle = bundleOf("item" to groupData.id)
        findNavController().navigate(R.id.action_nav_dashboard_to_groupDetailFragment, bundle)
    }

    private fun loadListData() {
        viewModel.getItems().observe(viewLifecycleOwner) {
            allDataList = it
            submitListWithLimit()
        }

        viewModel.displayEmptyList.observe(viewLifecycleOwner) { emptyList ->
            lifecycleScope.launch {
                delay(200)
                if (emptyList) {
                    binding.emptyPinnedList.visible()
                } else {
                    binding.emptyPinnedList.gone()
                }
            }
        }
    }

    private fun submitListWithLimit() {
        val list = allDataList.safeSlice(viewModel.maxListSize)
        listAdapter.submitList(list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pinned_menu, menu)
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

        if (it[0] != SearchResultCallback.SEARCH_FAILED) {

            if (it[0] == SearchResultCallback.SEARCH_GROUP_RESULT_EXTRA) {
                val bundle = bundleOf("item" to it[1])
                findNavController().navigate(R.id.action_nav_dashboard_to_groupDetailFragment,
                    bundle)
            } else {
                val bundle = bundleOf(Constants.ITEM_ID_KEY to it[1])
                findNavController().navigate(R.id.action_nav_dashboard_to_editFragment, bundle)
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
            val pinnedItem  = allDataList[layoutPosition]

            changePin( pinnedItem.data )

        }


        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (viewHolder != null) {

                val foregroundView: View =   (viewHolder as PinnedItemsListAdapter.ViewHolder).viewForeground

                getDefaultUIUtil().onSelected(foregroundView)
            }
        }

        override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            val foregroundView: View = (viewHolder as PinnedItemsListAdapter.ViewHolder).viewForeground

            getDefaultUIUtil().onDrawOver(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        }


        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            //super.clearView(recyclerView, viewHolder)
            val foregroundView: View = (viewHolder as PinnedItemsListAdapter.ViewHolder).viewForeground
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

            val swipeClearHome: View = (viewHolder as PinnedItemsListAdapter.ViewHolder).swipeArchiveLeft
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


    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.rvDashboardList.layoutManager as LinearLayoutManager
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

