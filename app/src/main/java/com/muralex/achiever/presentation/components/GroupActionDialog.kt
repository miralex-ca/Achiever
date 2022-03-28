package com.muralex.achiever.presentation.components

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.databinding.DialogGroupActionBinding
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.gone
import com.muralex.achiever.presentation.utils.visible
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class GroupActionDialog @Inject constructor(
    @ActivityContext private val context: Context,
) {

    private var dialog: AlertDialog? = null

    fun openDialog(
        item: GroupData,
        callBack: (Action, GroupData) -> Unit,
    ) {
        openDialog(true, item, callBack)
    }

    fun openDialog(
        top: Boolean = true,
        item: GroupData,
        callBack: (Action, GroupData) -> Unit,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val binding = DialogGroupActionBinding.inflate(layoutInflater)

        binding.let {
            if (!top) noMoveToTop(it)
            setActionClick(it, item, callBack)
            setIconsAndText(it, item)
        }

        dialog = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .create()
        dialog!!.show()
    }

    private fun setIconsAndText(binding: DialogGroupActionBinding, item: GroupData) {
        binding.ivArchiveIcon.setArchiveIcon(item)
        binding.tvArchiveText.setArchiveText(item)
    }

    private fun noMoveToTop(binding: DialogGroupActionBinding) {

        binding.llActionMoveTop.gone()
        binding.topSpace2.visible()
    }

    private fun setActionClick(
        binding: DialogGroupActionBinding,
        item: GroupData,
        callBack: (Action, GroupData) -> Unit,
    ) {

        binding.llActionEdit.setOnClickListener {
            callBack(Action.Edit, item)
            cancelFromDialog()
        }

        binding.llActionView.setOnClickListener {
            callBack(Action.OpenGroup, item)
            cancelFromDialog()
        }

        binding.llActionArchive.setOnClickListener {

            item.group?.let {
                if (it.archived > 0) callBack(Action.Unarchive, item)
                else callBack(Action.Archive, item)
            }
            cancelFromDialog()
        }

        binding.llActionMoveTop.setOnClickListener {
            callBack(Action.MoveToTop, item)
            cancelFromDialog()
        }
    }

    private fun cancelFromDialog() {
        dialog?.dismiss()
    }


    private fun ImageView.setArchiveIcon(item: GroupData?) {
        item?.let {
            var icon = R.drawable.ic_action_archive
            item.group?.let {
                if (it.archived  > 0) {
                    icon = R.drawable.ic_action_unarchive
                }
            }
            setImageResource(icon)
        }
    }

    private fun TextView.setArchiveText(item: GroupData?) {
        item?.let {
            text = context.getString(R.string.archive_text)
            item.group?.let {
                if (it.archived  > 0) {
                    text = context.getString(R.string.unarchive_text)
                }
            }
        }
    }


}


