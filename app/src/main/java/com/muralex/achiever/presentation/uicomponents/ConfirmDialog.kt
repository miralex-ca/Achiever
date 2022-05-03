package com.muralex.achiever.presentation.uicomponents

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class ConfirmDialog @Inject constructor(
    @ActivityContext private val context: Context,
) {
    fun open(title: String, confirmation: String, callBack: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(confirmation)
            .setNeutralButton(context.getString(R.string.cancel_txt)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(context.getString(R.string.confirm_txt)) { dialog, _ ->
                dialog.dismiss()
                callBack()
            }
            .show()
    }

}
