package com.muralex.achiever.presentation.utils

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.muralex.achiever.R


@BindingAdapter("setImageSource")
fun ImageView.setImageSource(imageUrl: String?) {
    imageUrl?.let {
        //val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(15))
        val requestOptions = RequestOptions().transform(CenterCrop(), CircleCrop())

            Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(this)

    }
}

@BindingAdapter("setGroupImageSource")
fun ImageView.setGroupImageSource(imageUrl: String?) {
    imageUrl?.let {
        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(15))
        Glide.with(context)
            .load(imageUrl)
            .apply(requestOptions)
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

