package com.muralex.achiever.presentation.activities.search_images

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.SearchItem
import com.muralex.achiever.databinding.ActivitySearchImageBinding
import com.muralex.achiever.presentation.activities.search_images.SearchImageViewModel.Companion.PAGE_SIZE
import com.muralex.achiever.presentation.fragments.group_edit.GroupEditViewModel
import com.muralex.achiever.presentation.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchImageBinding

    private lateinit var listAdapter: ImageRecyclerAdapter

    private val viewModel: SearchImageViewModel by viewModels()

    private var isScrolling = false
    private var isLoading = false

    var job: Job? = null
    var progressJob: Job? = null

    private lateinit var allDataList: List<SearchItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        binding.searchView.onActionViewExpanded()
        binding.searchView.requestFocus()


       // viewModel = ViewModelProvider(this)[SearchImageViewModel::class.java]

        setSearchListener()

        subscribeToObservers()

        initSearchList()


    }

    private fun initSearchList() {
        listAdapter = ImageRecyclerAdapter()
        binding.rvSearchList.adapter = listAdapter
        binding.rvSearchList.layoutManager = GridLayoutManager(this,3)

        listAdapter.setOnItemClickListener {
            selectSearchItem( it )
        }
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
        binding.initListHint.displayIf(query.trim().isEmpty())

        binding.emptyListHint.displayIf(query.trim().isNotEmpty())

        if (query.trim().isNotEmpty()) binding.searchEmptyList.gone()

        job?.cancel()
        job = lifecycleScope.launch {

            delay(600)

            binding.searchQueryHint.text = query
            if (query.isNotEmpty()) {
                viewModel.searchForImage(query)


            } else {
                progressJob?.cancel()
                progressJob =  lifecycleScope.launch {

                    delay(200)
                    binding.searchProgress.hide()
                }
            }
        }
    }


    private fun subscribeToObservers() {

        viewModel.imageList.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    progressJob?.cancel()
                    progressJob =  lifecycleScope.launch {
                        delay(1400)
                        binding.searchProgress.hide()
                    }
                    val urls = it.data?.hits?.map { imageResult -> imageResult.previewURL }
                    listAdapter.images = urls ?: listOf()
                    binding.searchEmptyList.displayIf( urls.isNullOrEmpty())
                    binding.checkNetwork.gone()

                }

                Status.ERROR -> {

                    Toast.makeText(this, it.message ?: "Connection Error", Toast.LENGTH_LONG)
                        .show()

                    binding.checkNetwork.visible()

                    progressJob?.cancel()
                    progressJob =  lifecycleScope.launch {
                        delay(200)
                        binding.searchProgress.hide()
                    }
                }

                Status.LOADING -> {
                    binding.checkNetwork.gone()
                    binding.searchProgress.show()
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


    private fun selectSearchItem(selectedImage: String) {

        val selectedData = arrayListOf(selectedImage)

        val returnedValue = Intent().apply {
            putStringArrayListExtra(SearchImageResultCallback.SEARCH_IMAGE_EXTRA_DATA, selectedData)
        }

        setResult(Activity.RESULT_OK, returnedValue)
        finish()
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

      //  listAdapter.submitList(list)
    }


}