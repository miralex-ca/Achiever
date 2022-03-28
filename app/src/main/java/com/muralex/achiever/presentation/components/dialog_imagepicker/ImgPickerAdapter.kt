package com.example.tasker.presentation.components.imagepicker

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.muralex.achiever.databinding.DialogImagePickerItemListBinding
import com.muralex.achiever.presentation.utils.Constants.IMAGES_FOLDER_ICONS


open class ImgPickerAdapter(
    private val pics: List<String>,
    private val selected: Int,
    private val onItemClicker: (String) -> Unit
): RecyclerView.Adapter<ImgPickerAdapter.ViewHolder>() {

    private var clickOn = true

    class ViewHolder (view: DialogImagePickerItemListBinding) : RecyclerView.ViewHolder(view.root) {
        val icon = view.icon
        private val wrap = view.imgWrap

        fun bind(pic: String, checkOnClick: (String) -> Unit) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(8))

            Glide.with(icon.context).load(IMAGES_FOLDER_ICONS + pic)
                .apply(requestOptions)
                .into(icon)

            wrap.setOnClickListener {
                checkOnClick(pic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DialogImagePickerItemListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pic = pics[position]
        holder.bind(pic) { selectedPic: String -> checkOnClick(selectedPic) }
    }

    override fun getItemCount(): Int {
        return pics.size
    }

    private fun checkOnClick(picture: String) {
        if (clickOn) {
            clickOn = false
            Handler(Looper.myLooper()!!).postDelayed({
                clickOn = true
                onItemClicker(IMAGES_FOLDER_ICONS+picture)
            }, 200)
        }
    }


}