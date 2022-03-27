package com.muralex.achiever.presentation.activities.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class SearchResultCallback : ActivityResultContract<String, ArrayList<String> >() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, SearchActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<String> {
        return when {
            resultCode != Activity.RESULT_OK -> arrayListOf(SEARCH_FAILED, "")
            else -> intent?.getStringArrayListExtra(SEARCH_EXTRA_DATA)!!
        }
    }

    companion object{
        const val SEARCH_FAILED = "none"
        const val SEARCH_EXTRA_DATA = "data"
        const val SEARCH_GROUP_RESULT_EXTRA = "group"
        const val SEARCH_ITEM_RESULT_EXTRA = "item"
    }


}