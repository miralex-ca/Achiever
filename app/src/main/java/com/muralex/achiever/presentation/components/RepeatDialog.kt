package com.muralex.achiever.presentation.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muralex.achiever.R
import com.muralex.achiever.databinding.DialogSetRepeatBinding
import com.muralex.achiever.presentation.utils.Constants.REPEAT_PERIODS
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class RepeatDialog @Inject constructor (
    @ActivityContext private val context: Context
) {

    private var dialog: AlertDialog? = null

    private var onDismissAction : ( () -> Unit )? = null

    fun setOnDismissAction (listener:  () -> Unit ) {
        onDismissAction = listener
    }

    fun open(repeat: String?, callBack: (String) -> Unit) {

        val layoutInflater =  LayoutInflater.from(context)
        val binding = DialogSetRepeatBinding.inflate(layoutInflater)


        var repeatData = listOf("1", "day")

        repeat?.let {
              repeatData = it.split("_")
        }

        dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Frequency")
            .setView(binding.root)
            .setNeutralButton(context.getString(R.string.cancel_txt)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(context.getString(R.string.confirm_txt), null)
            .show()


        val numberPicker = binding.numberPicker
        numberPicker.minValue = 1
        numberPicker.maxValue = 60
        numberPicker.wrapSelectorWheel = true
        numberPicker.value = repeatData[0].toInt()

        val periodPicker = binding.periodPicker
        val values = arrayOf("day(s)", "week(s)", "month(s)", "year(s)")

        val savedPeriod = repeatData[1]
        var savedPeriodIndex = 0
        if (REPEAT_PERIODS.contains(savedPeriod)) savedPeriodIndex = REPEAT_PERIODS.indexOf(savedPeriod)

        periodPicker.minValue = 0
        periodPicker.maxValue = values.size - 1
        periodPicker.displayedValues = values
        periodPicker.wrapSelectorWheel = true
        periodPicker.value = savedPeriodIndex

        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {

            val count = numberPicker.value
            val period = REPEAT_PERIODS[periodPicker.value]

            callBack("${count}_$period")

            Handler(Looper.myLooper()!!).postDelayed({
                dialog?.dismiss()
            }, 200)
        }


        dialog?.setOnDismissListener {
            onDismissAction?.invoke()
            closeKeyboard()
        }
    }



    private fun showKeyboard() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }



}
