package com.muralex.achiever.presentation.activities.search_images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.muralex.achiever.R


class ImageRecyclerAdapter : RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder>() {

    private var onItemClickListener : ((String) -> Unit)? = null
    class ImageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var images : List<String>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_row,parent,false)
        return ImageViewHolder(view)
    }


    fun setOnItemClickListener(listener : (String) -> Unit) {
        onItemClickListener = listener
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageView = holder.itemView.findViewById<ImageView>(R.id.singleArtImageView)
        val url = images[position]

        holder.itemView.apply {

            val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(16))

            Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(imageView)

            setOnClickListener {
                onItemClickListener?.let {
                    it(url)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

}