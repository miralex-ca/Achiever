package com.muralex.achiever.presentation.uicomponents

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.databinding.DialogStatusModeBinding
import com.muralex.achiever.presentation.utils.gone

class StatusModeDialog (
    private val context: Context
) {

    private var dialog: AlertDialog? = null
    private var onDismissAction : ( () -> Unit )? = null

    fun setOnDismissAction (listener:  () -> Unit ) {
        onDismissAction = listener
    }

    fun open(title: String, type: Int, selected: Int, callBack: (Int) -> Unit) {
        val layoutInflater =  LayoutInflater.from(context)
        val binding = DialogStatusModeBinding.inflate(layoutInflater)
        val radioGroup = binding.radioWrap


        setRadiosText(radioGroup, type)
        setRadioOnClick(radioGroup)
        selectOptionByIndex(radioGroup, selected)


        dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(binding.root)
            .setNegativeButton(context.getString(R.string.cancel_txt)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(context.getString(R.string.confirm_txt)) { dialog, _ ->
               val selectedOption =  checkSelectedRadio(radioGroup)
                callBack(selectedOption)
            }
            .show()

        dialog?.setOnDismissListener {
            onDismissAction?.invoke()
        }
    }

    private fun setRadioOnClick(radioGroup: LinearLayout) {
        for (index in 0 until radioGroup.childCount) {
            val option = radioGroup.getChildAt(index)
            option.setOnClickListener {
                unselectRadios(radioGroup)
                it.findViewById<RadioButton>(R.id.radio).isChecked = true
            }
        }
    }

    private fun selectOptionByIndex(radioGroup: LinearLayout, selected: Int) {
        unselectRadios(radioGroup)
        radioGroup.getChildAt(selected).findViewById<RadioButton>(R.id.radio).isChecked = true
    }

    private fun setRadiosText(radioGroup: LinearLayout, type: Int) {

        var titles = context.resources.getStringArray(R.array.status_mode_titles)
        var descriptions = context.resources.getStringArray(R.array.status_mode_desc)

        if (type == REPEAT_AFTER_OPTIONS || type == REPEAT_OPTIONS) {
            titles = context.resources.getStringArray(R.array.repeat_status_mode_titles)
            descriptions = context.resources.getStringArray(R.array.repeat_status_mode_desc)
        }

        for (index in 0 until radioGroup.childCount) {

            val option = radioGroup.getChildAt(index)
            val title = option.findViewById<TextView>(R.id.status_title)
            val desc = option.findViewById<TextView>(R.id.status_desc)

            title.text = titles[index]
            desc.text = descriptions[index]

            if (type == DUEDATE_OPTIONS && index > 1) option.gone()
        }
    }

    private fun unselectRadios(radioGroup: LinearLayout) {
        for (index in 0 until radioGroup.childCount) {
            val option = radioGroup.getChildAt(index)
            val radio = option.findViewById<RadioButton>(R.id.radio)
            radio.isChecked = false
        }
    }

    private fun checkSelectedRadio(radioGroup: LinearLayout) : Int {
        var checked = -1
        for (index in 0 until radioGroup.childCount) {
            val option = radioGroup.getChildAt(index)
            val radio = option.findViewById<RadioButton>(R.id.radio)
            if (radio.isChecked) checked = index
        }
        return checked
    }

    companion object {
        const val DUEDATE_OPTIONS = 1
        const val REPEAT_OPTIONS = 2
        const val REPEAT_AFTER_OPTIONS = 3
    }

}
