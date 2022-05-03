package com.muralex.achiever.presentation.uicomponents.dialog_item_actions

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_1.ui.item_action.ActionStatusPickerListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.databinding.DialogItemActionBinding
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.checkAutoHintDisplay
import com.muralex.achiever.presentation.utils.setPinActionButton
import com.muralex.achiever.presentation.utils.setTodoStatusImage
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ItemActionDialog @Inject constructor(
    @ActivityContext private val context: Context
    ) {

    private var dialog: AlertDialog? = null

    fun openDialog(item: DataItem, displayStatus: String, callBack: (Action, DataItem) -> Unit) {

        val layoutInflater = LayoutInflater.from(context)
        val binding = DialogItemActionBinding.inflate(layoutInflater)

        setStatusList(binding, item, callBack)
        setActionClick(binding, item, callBack)
        setIconsAndText(binding, item)

        checkAutoHintDisplay(item, binding.autoModeHint)
        binding.ivAutoStatusImage.setTodoStatusImage(displayStatus)

        dialog = MaterialAlertDialogBuilder(context)
            //.setTitle("Actions menu")
            .setView(binding.root)
            //.setPositiveButton(R.string.cancel_txt) { _, _ -> cancelFromDialog() }
            .create()

        dialog?.show()
    }

    private fun setIconsAndText(binding: DialogItemActionBinding, item: DataItem) {
        binding.btnPin.setPinActionButton(item)
      //  binding.tvArchiveText.setArchiveText(item)
       // binding.ivDeleteIcon.setRemoveIcon(item)
       // binding.tvDeleteText.setRemoveText(item)
    }

    private fun setStatusList(
        binding: DialogItemActionBinding,
        item: DataItem,
        callBack: (Action, DataItem) -> Unit
    ) {
        binding.rvStatusPickerList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvStatusPickerList.adapter =
            ActionStatusPickerListAdapter(ITEM_STATUS, item.status) { selected: String ->

                val data = item.copy(status = selected)
                if (selected == ITEM_STATUS[5]) data.completedTime = System.currentTimeMillis()

                callBack(Action.EditStatus, data)

                removeChecksFromList(
                    binding.rvStatusPickerList.layoutManager,
                    getIndexByName(ITEM_STATUS.toTypedArray(), selected)
                )

                Handler(Looper.myLooper()!!).postDelayed({
                    cancelFromDialog()
                }, 200)
            }
    }

    private fun setActionClick(
        binding: DialogItemActionBinding,
        item: DataItem,
        callBack: (Action, DataItem) -> Unit
    ) {

        binding.btnPin.setOnClickListener {
            callBack(Action.Pin, item)
            cancelFromDialog()
        }

        binding.btnEdit.setOnClickListener {
            callBack(Action.Edit, item)
            cancelFromDialog()
        }

        binding.btnDelete.setOnClickListener {
            callBack(Action.Delete, item)
            cancelFromDialog()
        }
    }

    private fun getIndexByName(singleItems: Array<String>, currentStatus: String?): Int {
        var checkedItem = singleItems.indexOf(currentStatus)
        if (checkedItem !in singleItems.indices) checkedItem = 0
        return checkedItem
    }

    private fun removeChecksFromList(layoutManager: RecyclerView.LayoutManager?, selected: Int) {
        repeat(ITEM_STATUS.size) {index ->
            val view = layoutManager?.findViewByPosition(index)
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