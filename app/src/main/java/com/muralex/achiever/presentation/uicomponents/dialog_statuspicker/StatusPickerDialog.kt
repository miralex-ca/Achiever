package com.muralex.achiever.presentation.uicomponents.dialog_statuspicker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.databinding.DialogStatusPickerBinding
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS

open class StatusPickerDialog(private val context: Context) {

    private var dialog: AlertDialog? = null


    fun openDialog(singleItems: Array<String>, displayAuto: Boolean, currentStatus: String?, callBack: (String) -> Unit) {

        getIndexByName(singleItems, currentStatus)
        val layoutInflater = LayoutInflater.from(context)
        val binding = DialogStatusPickerBinding.inflate(layoutInflater)

        binding.rvStatusPickerList.layoutManager = GridLayoutManager(context, 3)

        binding.rvStatusPickerList.adapter = StatusPickerListAdapter(ITEM_STATUS, currentStatus!!) { selected: String ->

            callBack(selected)
            removeChecksFromList(
                binding.rvStatusPickerList.layoutManager as GridLayoutManager,
                getIndexByName(singleItems, selected)
            )

            Handler(Looper.myLooper()!!).postDelayed({
                cancelFromDialog()
            }, 200)
        }

        dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Select Status")
            .setView(binding.root)
            .setPositiveButton("Cancel") { _, _ -> cancelFromDialog() }
            .create()

        dialog?.show()

    }

    private fun getIndexByName(singleItems: Array<String>, currentStatus: String?): Int {
        var checkedItem = singleItems.indexOf(currentStatus)
        if (checkedItem !in singleItems.indices) checkedItem = 0
        return checkedItem
    }

    private fun removeChecksFromList(layoutManager: GridLayoutManager, selected: Int) {

        repeat(ITEM_STATUS.size) {index ->
            val view = layoutManager.findViewByPosition(index)
            view?.let{
                val selection = view.findViewById<View>(R.id.selectedStatus)
                selection?.let {
                    selection.visibility = View.GONE
                    if (index == selected) selection.visibility = View.VISIBLE
                }
            }
        }
    }




    private fun cancelFromDialog() {
        dialog?.dismiss()
    }


}