package com.muralex.achiever.presentation.fragments.archive

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.muralex.achiever.R
import com.muralex.achiever.databinding.FragmentArchiveBinding
import com.muralex.achiever.presentation.activities.search.SearchResultCallback
import com.muralex.achiever.presentation.utils.dataBindings

class ArchiveFragment : Fragment(R.layout.fragment_archive) {

    private val binding by dataBindings(FragmentArchiveBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
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
//        if (it[0] != SearchResultCallback.SEARCH_FAILED) {
//
//        }
    }

}

