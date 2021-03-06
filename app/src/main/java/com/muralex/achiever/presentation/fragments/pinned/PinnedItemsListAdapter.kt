package com.muralex.achiever.presentation.fragments.pinned

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.databinding.ListitemPinneditemsBinding
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.presentation.utils.setPinnedGroupImageSource
import com.muralex.achiever.presentation.utils.setPinnedIcon
import com.muralex.achiever.presentation.utils.setTodoStatusImage

class PinnedItemsListAdapter : ListAdapter<PinnedItem, PinnedItemsListAdapter.ViewHolder>(PinnedListDiffCallBack()) {

    class ViewHolder (val binding: ListitemPinneditemsBinding) : RecyclerView.ViewHolder (binding.root)  {
        val viewForeground = binding.viewForeground
        val swipeArchiveLeft = binding.bgArchiveLeft
        val swipeArchiveRight = binding.bgArchiveRight

        fun bind(dataItem: PinnedItem, onItemClickListener: ((Action, PinnedItem) -> Unit)?) {
            binding.tvNoteTitle.text = dataItem.title
            binding.tvGroupName.text = dataItem.groupName
            binding.ivGroupImage.setPinnedGroupImageSource(dataItem.groupImage)
            binding.ivItemImage.setTodoStatusImage(dataItem.displayStatus)
            binding.ivPinned.setPinnedIcon(dataItem.data)
            setClickEvents(onItemClickListener, dataItem)
        }

        private fun setClickEvents(
            onItemClickListener: ((Action, PinnedItem) -> Unit)?,
            dataItem: PinnedItem,
        ) {
            onItemClickListener?.let {clickListener ->
                binding.apply {
                    itemCardWrap.setOnClickListener {
                        clickListener(Action.Click, dataItem)
                    }
                    itemCardWrap.setOnLongClickListener {
                        clickListener(Action.LongClick, dataItem)
                        true
                    }
                    llSection.setOnClickListener {
                        clickListener(Action.OpenGroup, dataItem)
                    }
                }
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val layoutInflater =LayoutInflater.from(parent.context)
                val binding = ListitemPinneditemsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClickListener )
    }

    private var onItemClickListener : ( (Action, PinnedItem) -> Unit )?= null

    fun setOnItemClickListener (listener:  (Action, PinnedItem) -> Unit ) {
        onItemClickListener = listener
    }
}

class PinnedListDiffCallBack : DiffUtil.ItemCallback<PinnedItem>() {
    override fun areItemsTheSame(oldItem: PinnedItem, newItem: PinnedItem): Boolean {
       return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PinnedItem, newItem: PinnedItem): Boolean {
        return oldItem == newItem
    }
}