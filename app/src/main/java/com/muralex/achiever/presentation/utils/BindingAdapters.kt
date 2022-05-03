package com.muralex.achiever.presentation.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.muralex.achiever.R
import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.showProgress
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_DUE_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_NO_DATE
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEATED_AFTER
import com.muralex.achiever.presentation.utils.Constants.COMPLETION_TYPE_REPEAT_PERIOD
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_COLORS
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS_ICONS
import timber.log.Timber
import java.text.DateFormat


@BindingAdapter("setImageSource")
fun ImageView.setImageSource(imageUrl: String?) {
    imageUrl?.let {
        val requestOptions = RequestOptions().transform(CenterCrop(), CircleCrop())

            Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .error(R.drawable.image_off)
                .into(this)

    }
}

@BindingAdapter("setGroupImageSource")
fun ImageView.setGroupImageSource(imageUrl: String?) {
    imageUrl?.let {
        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(20))
        Glide.with(context)
            .load(imageUrl)
            .apply(requestOptions)
            .error(R.drawable.image_off)
            .into(this)
    }
}

@BindingAdapter("setGroupEditImageSource")
fun ImageView.setGroupEditImageSource(imageUrl: String?) {
    imageUrl?.let {
        val requestOptions = RequestOptions().transform(CenterCrop())
        Glide.with(context)
            .load(imageUrl)
            .apply(requestOptions)
            .error(R.drawable.image_off)
            .into(this)
    }
}

@BindingAdapter("setPinnedGroupImageSource")
fun ImageView.setPinnedGroupImageSource(imageUrl: String?) {
    imageUrl?.let {

        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(15))

        Glide.with(context)
            .load(imageUrl)
            .apply(requestOptions)
            .error(R.drawable.image_off)
            .into(this)

    }
}

@BindingAdapter("setSearchImageSource")
fun ImageView.setSearchImageSource(imageUrl: String?) {
    imageUrl?.let {

        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(15))
        Glide.with(context)
            .load(imageUrl)
            .apply(requestOptions)
            .error(R.drawable.image_off)
            .into(this)
    }
}


@BindingAdapter("setTabButtonActive")
fun MaterialButton.setTabButtonActive(active: Boolean) {

    if (active) {
        background.setTint( ContextCompat.getColor(context, R.color.active_tab_btn) )
        alpha = 1.0f
    } else {
        background.setTint( ContextCompat.getColor(context, R.color.inactive_tab_btn) )
        alpha = .8f
    }
}

@BindingAdapter("setTotalProgressText")
fun TextView.setGroupTotalProgressText(groupData: GroupData) {


    text = context.getString(R.string.group_total_progress,
        groupData.progress.toString(),
        groupData.completedItems.toString(),
        groupData.activeItems.toString()
        )

    groupData.group?.let {
        if ( !it.showProgress()) {
            text = context.getString(R.string.group_total_no_progress,
                groupData.totalItemsCount.toString(),
                groupData.completedItems.toString()
            )
        }
    }
}

@BindingAdapter("setTexNoLine")
fun TextView.setTexNoLine(textToCheck: String?) {
     textToCheck?.let {
         text = removeLineBreaks(it)
     }
}

fun removeLineBreaks(text: String) = text.replace("\n"," ")

@BindingAdapter("setPinActionButton")
fun MaterialButton.setPinActionButton(item: DataItem?) {
    item?.let {
        var image = R.drawable.ic_action_pin
        var btnTxt  = "Pin"
        if (it.pinned == 1 )   {
            image = R.drawable.ic_action_unpin
            btnTxt  = "Unpin"
        }
        setIconResource(image)
        text = btnTxt
    }
}


@BindingAdapter("setImageStatusResource")
fun ImageView.setImageStatusResource(status: String?) {

    status?.let {
        val index = ITEM_STATUS.indexOf(it)
        val icon: Int
        if (index == 0) {
            icon = R.drawable.ic_status_circle
            setImageResource(icon)
        }
    }
}

@BindingAdapter("setImageStatusColor")
fun ImageView.setImageStatusColor(status: String?) {
    status?.let {
        var index = ITEM_STATUS.indexOf(it)
        if (index !in ITEM_STATUS.indices) index = 0
        val color: Int = ITEM_STATUS_COLORS[index]
        setColorFilter(ContextCompat.getColor(context, color))
    }
}

@BindingAdapter("setStatusCheckTint")
fun ImageView.setStatusCheckTint(status: String?) {
    status?.let {
        if (status == "yellow" || status == "gray") {
            setColorFilter(ContextCompat.getColor(context, R.color.status_dark_check))
        }
    }
}

@BindingAdapter("setStatusText")
fun TextView.setStatusText(status: String?) {
    status?.let {
        text = when (status) {
            ITEM_STATUS[0] -> context.getString(R.string.status_white_title)
            ITEM_STATUS[1] -> context.getString(R.string.status_red_title)
            ITEM_STATUS[2] -> context.getString(R.string.status_gray_title)
            ITEM_STATUS[3] -> context.getString(R.string.status_yellow_title)
            ITEM_STATUS[4] -> context.getString(R.string.status_blue_title)
            ITEM_STATUS[5] -> context.getString(R.string.status_green_title)
            else -> context.getString(R.string.current_status)
        }
    }
}

@BindingAdapter("setItemStatusIcon")
fun TextInputLayout.setItemStatusIcon(status: String?) {
    status?.let {

        var index = ITEM_STATUS.indexOf(status)
        if (index !in ITEM_STATUS.indices) index = 0

        var icon: Int = R.drawable.ic_status_round
        if (index == 0) icon = R.drawable.ic_status_circle

        endIconDrawable = ResourcesCompat.getDrawable(this.context.resources, icon, null)

        setEndIconTintList(
            ResourcesCompat.getColorStateList(
                this.context.resources,
                ITEM_STATUS_ICONS[index],
                null
            )
        )
    }
}

@BindingAdapter("setTodoStatusImage")
fun ImageView.setTodoStatusImage(item: String?) {
    item?.let {
        val image = when (it) {
            ITEM_STATUS[5] -> R.drawable.ic_todo_green
            ITEM_STATUS[4] -> R.drawable.ic_todo_blue
            ITEM_STATUS[3] -> R.drawable.ic_todo_yellow
            ITEM_STATUS[2] -> R.drawable.ic_todo_gray
            ITEM_STATUS[1] -> R.drawable.ic_todo_red
            else -> {
                R.drawable.ic_todo_circle
            }
        }
        setImageResource(image)
    }
}


@BindingAdapter("setDateButtonText")
fun TextView.setDateButtonText(date: String?) {
    date?.let {
        var dateString : String
        if ( date.isNullOrEmpty() || date == "0") {
            dateString = ""
        } else {

            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
            // val dateFormat = SimpleDateFormat("yyyy.MM.dd, HH:mm:ss z")

            try {
                dateString = dateFormat.format( date.toLong() )
            } catch (e: Exception) {
                Timber.e("$date can't be converted")
                dateString = ""
            }
        }
        text = dateString
    }
}

@BindingAdapter("setDueDateVisibility")
fun View.setDueDateVisibility(completionType: String?) {
    if (completionType == COMPLETION_TYPE_DUE_DATE) {
        visible()
    } else {
        gone()
    }
}


@BindingAdapter("setCompleteVisibility")
fun View.setCompleteVisibility(completionType: String?) {
    if (completionType == COMPLETION_TYPE_NO_DATE) {
        gone()
    } else {
        visible()
    }
}

@BindingAdapter("setRepeatVisibility")
fun View.setRepeatVisibility(completionType: String?) {
    if (completionType == COMPLETION_TYPE_REPEATED || completionType == COMPLETION_TYPE_REPEAT_PERIOD
        || completionType == COMPLETION_TYPE_REPEATED_AFTER) {
        visible()
    } else {
        gone()
    }
}

@BindingAdapter("setStatusModeVisibility")
fun View.setStatusModeVisibility(completionType: String?) {
    if (completionType == COMPLETION_TYPE_NO_DATE) {
        gone()
    } else {
        visible()
    }
}

@BindingAdapter("setPinnedIcon")
fun ImageView.setPinnedIcon(item: DataItem?) {
    item?.let {
        if (it.pinned == 1) visible()
        else invisible()
    }
}

@BindingAdapter("setProgressColor")
fun LinearProgressIndicator.setProgressColor(progress: Int) {

    var color = ContextCompat.getColor(context, R.color.status_gray)

    when {
        progress > 96 -> color = ContextCompat.getColor(context, R.color.status_green)
        progress > 74 -> color = ContextCompat.getColor(context, R.color.status_cyan)
        progress > 40 -> color = ContextCompat.getColor(context, R.color.status_blue)
        progress > 15 -> color = ContextCompat.getColor(context, R.color.status_yellow)
        progress > 2 -> color = ContextCompat.getColor(context, R.color.status_orange)
    }

    setIndicatorColor(color)
    trackColor = ColorUtils.setAlphaComponent(color, 70)

}




