package com.muralex.achiever.presentation.activities.search_images

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class SearchImageResultCallback : ActivityResultContract<String, ArrayList<String> >() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, SearchImageActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ArrayList<String> {
        return when {
            resultCode != Activity.RESULT_OK -> arrayListOf(SEARCH_IMAGE_FAILED, "")
            else -> intent?.getStringArrayListExtra(SEARCH_IMAGE_EXTRA_DATA)!!
        }
    }

    companion object{
        const val SEARCH_IMAGE_FAILED = "none"
        const val SEARCH_IMAGE_EXTRA_DATA = "data"
    }


}