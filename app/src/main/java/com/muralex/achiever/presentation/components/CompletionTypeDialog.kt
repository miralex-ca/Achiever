package com.muralex.achiever.presentation.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.databinding.DialogCompletiontTypeBinding

class CompletionTypeDialog (
    private val context: Context
) {

    private var dialog: AlertDialog? = null
    private var onDismissAction : ( () -> Unit )? = null

    fun setOnDismissAction (listener:  () -> Unit ) {
        onDismissAction = listener
    }

    fun open(title: String, selected: Int, callBack: (Int) -> Unit) {
        val layoutInflater =  LayoutInflater.from(context)
        val binding = DialogCompletiontTypeBinding.inflate(layoutInflater)
        val radioGroup = binding.radioWrap

        setRadiosText(radioGroup)
        setRadioOnClick(radioGroup, callBack)
        selectOptionByIndex(radioGroup, selected)

        dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(binding.root)
            .show()

        dialog?.setOnDismissListener {
            onDismissAction?.invoke()
        }
    }

    private fun setRadioOnClick(radioGroup: LinearLayout, callBack: (Int) -> Unit) {
        for (index in 0 until radioGroup.childCount) {
            val option = radioGroup.getChildAt(index)
            option.setOnClickListener {
                unselectRadios(radioGroup)
                it.findViewById<RadioButton>(R.id.radio).isChecked = true

                val selectedOption =  checkSelectedRadio(radioGroup)
                callBack(selectedOption)

                Handler(Looper.getMainLooper()).postDelayed({
                    dialog?.dismiss()
                }, 200)

            }
        }
    }

    private fun selectOptionByIndex(radioGroup: LinearLayout, selected: Int) {
        unselectRadios(radioGroup)
        radioGroup.getChildAt(selected).findViewById<RadioButton>(R.id.radio).isChecked = true
    }

    private fun setRadiosText(radioGroup: LinearLayout) {

        val titles = context.resources.getStringArray(R.array.set_completion_type_values)
        val descriptions = context.resources.getStringArray(R.array.set_completion_type_desc)

        for (index in 0 until radioGroup.childCount) {

            val option = radioGroup.getChildAt(index)
            val title = option.findViewById<TextView>(R.id.status_title)
            val desc = option.findViewById<TextView>(R.id.status_desc)

            title.text = titles[index]
            desc.text = descriptions[index]

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


}
