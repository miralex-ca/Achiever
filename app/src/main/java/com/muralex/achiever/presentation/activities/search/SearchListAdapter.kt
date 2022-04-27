package com.muralex.achiever.presentation.activities.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.muralex.achiever.R
import com.muralex.achiever.data.models.usemodels.SearchItem
import com.muralex.achiever.databinding.ListItemSearchBinding
import com.muralex.achiever.databinding.SearchItemMoreBinding
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.setSearchImageSource
import com.muralex.achiever.presentation.utils.setTexNoLine
import com.muralex.achiever.presentation.utils.setTodoStatusImage


class SearchListAdapter (
    private val callBack:(Action, SearchItem) -> Unit
)  : ListAdapter<SearchItem, RecyclerView.ViewHolder>(SearchListDiffCallback()) {

    class ViewHolder (val binding: ListItemSearchBinding) : RecyclerView.ViewHolder (binding.root) {

        fun bind(item: SearchItem, callBack: (Action, SearchItem) -> Unit) {
            binding.tvTitle.setTexNoLine(item.title)
            binding.ivImage.setSearchImageSource(item.image)

            binding.wrapSearchItem.setOnClickListener {
                callBack(Action.Click, item)
            }

//            binding.wrapSearchItem.setOnLongClickListener{
//                callBack(Action.LongClick, item)
//                true
//            }

            binding.tvSection.setTexNoLine(item.groupData?.title)

            binding.llSection.setOnClickListener {
                callBack(Action.OpenGroup, item)
            }

            binding.ivStatus.setTodoStatusImage(item.displayStatus)
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSearchBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class LastViewHolder (val binding: SearchItemMoreBinding) : RecyclerView.ViewHolder (binding.root) {

        fun bind(item: SearchItem, callBack: (Action, SearchItem) -> Unit) {
            binding.openMoreTxt.text = item.title
            binding.openMore.setOnClickListener {
                callBack(Action.Click, item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchItemMoreBinding.inflate(layoutInflater, parent, false)
                return LastViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var type = 1
        if (getItem(position).id == "last_item")  type  = 2
        return type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            2 ->   LastViewHolder.from(parent)
            else -> ViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ViewHolder -> holder.bind(item, callBack)
            is LastViewHolder -> holder.bind(item, callBack)

        }
    }
}

class SearchListDiffCallback : DiffUtil.ItemCallback<SearchItem>() {
    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem == newItem
    }
}