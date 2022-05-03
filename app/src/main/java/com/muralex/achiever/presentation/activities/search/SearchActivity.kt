package com.muralex.achiever.presentation.activities.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.muralex.achiever.R
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.usemodels.SearchItem
import com.muralex.achiever.databinding.ActivitySearchBinding
import com.muralex.achiever.presentation.activities.search.SearchResultCallback.Companion.SEARCH_EXTRA_DATA
import com.muralex.achiever.presentation.activities.search.SearchResultCallback.Companion.SEARCH_GROUP_RESULT_EXTRA
import com.muralex.achiever.presentation.activities.search.SearchResultCallback.Companion.SEARCH_ITEM_RESULT_EXTRA
import com.muralex.achiever.presentation.activities.search.SearchViewModel.Companion.PAGE_SIZE
import com.muralex.achiever.presentation.uicomponents.ConfirmDialog
import com.muralex.achiever.presentation.uicomponents.dialog_item_actions.ItemActionDialog
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.displayIf
import com.muralex.achiever.presentation.utils.safeSlice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var listAdapter: SearchListAdapter
    @Inject
    lateinit var itemActionDialog: ItemActionDialog
    @Inject
    lateinit var confirmDialog: ConfirmDialog

    private lateinit var binding: ActivitySearchBinding
    private lateinit var allDataList: List<SearchItem>
    private var job: Job? = null
    private var progressJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        binding.searchView.onActionViewExpanded()
        binding.searchView.requestFocus()
        setSearchListener()
        initSearchList()

    }

    private fun initSearchList() {
        listAdapter = SearchListAdapter {action,  item: SearchItem ->
             if (action == Action.LongClick) openActionsDialog(item)
             else selectSearchItem(action, item)
        }
        binding.rvSearchList.adapter = listAdapter

    }

    private fun setSearchListener() {

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    handleSearch(it)
                }
                return false
            }
        })
    }

    private fun handleSearch(query: String) {

        binding.searchProgress.show()
        var delayLength: Long = 1600
        if (query.isEmpty()) delayLength = 300

        job?.cancel()
        job = lifecycleScope.launch {
            delay(300)

            viewModel.searchByQuery(query).observe(this@SearchActivity) {

                it?.let {
                    allDataList = it.asReversed()
                    submitListWithLimit()
                    progressJob?.cancel()
                    progressJob =  lifecycleScope.launch {
                        delay(delayLength)
                        binding.searchProgress.hide()
                    }
                }

            }

            viewModel.displayEmptyList.observe(this@SearchActivity) { emptyList ->
                lifecycleScope.launch {
                    binding.searchQueryHint.text = query
                    delay(200)
                    binding.searchEmptyList.displayIf(emptyList || query.trim().isEmpty() )
                    binding.initListHint.displayIf(query.trim().isEmpty())
                    binding.emptyListHint.displayIf(query.trim().isNotEmpty())

                }
            }
        }
    }




    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.fade_exit_out)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun openActionsDialog(item: SearchItem) {
        closeKeyboard()
        item.data?.let {


            itemActionDialog.openDialog(it, item.displayStatus) { action: Action, returnedItem: DataItem ->
                item.data = returnedItem
                callBackFromActionsDialog(action, item)
            }
        }
    }

    private fun callBackFromActionsDialog(action: Action, item: SearchItem) {


        if (action == Action.EditStatus) changeItemStatus(item.data)
        if (action == Action.Edit) selectSearchItem(action, item)
        if (action == Action.Pin) changePin(item.data)
        if (action == Action.Delete) {
            Handler(Looper.getMainLooper()).postDelayed({
                confirmItemDeletion(item.data)
            }, 100)
        }
    }

    private fun changePin(item: DataItem?) {
        item?.let { viewModel.changePin(it) }
    }

    private fun changeItemStatus(item: DataItem?) {
        item?.let { viewModel.changeItemStatus(it) }
    }

    private fun confirmItemDeletion(dataItem: DataItem?) {
        lifecycleScope.launch {
            delay(150)
            confirmDialog.open(getString(R.string.confirm_deletion), getString(R.string.confirm_delete_msg)) {
                deleteItem(dataItem)
            }
        }
    }

    private fun deleteItem(dataItem: DataItem?) {
        lifecycleScope.launch {
            delay(200)
            dataItem?.let { viewModel.deleteItem(it) }
        }
    }

    private fun selectSearchItem(action: Action, item: SearchItem) {

        if (item.id == "last_item") {
            val layoutManager = binding.rvSearchList.layoutManager as LinearLayoutManager
            val sizeOfCurrentList = layoutManager.itemCount
            viewModel.addListItems(sizeOfCurrentList)
            submitListWithLimit()

        } else {

            var open = arrayListOf(SEARCH_ITEM_RESULT_EXTRA, item.id)

            if (action == Action.OpenGroup) {
                item.groupData?.let {
                    open = arrayListOf(SEARCH_GROUP_RESULT_EXTRA, it.id)
                }
            }

            val returnedValue = Intent().apply {
                putStringArrayListExtra(SEARCH_EXTRA_DATA, open)
            }

            closeKeyboard()

            Handler(Looper.getMainLooper()).postDelayed({
                setResult(Activity.RESULT_OK, returnedValue)
                finish()
            }, 100)
        }
    }



    private fun submitListWithLimit() {
        val list = allDataList.safeSlice(viewModel.maxListSize)

        if (allDataList.size > list.size)  {

            val remainingItemsCount = allDataList.size - list.size
            val moreSize = if (remainingItemsCount > PAGE_SIZE ) PAGE_SIZE else remainingItemsCount

            val item = SearchItem( "Load $moreSize more from $remainingItemsCount" ).apply {
                id = "last_item"
            }
            list.add(item)
        }

        listAdapter.submitList(list)
    }

    private fun closeKeyboard() {
        binding.root.findFocus()?.clearFocus()
        val view: View? = findViewById(android.R.id.content)
        view?.let {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }


}