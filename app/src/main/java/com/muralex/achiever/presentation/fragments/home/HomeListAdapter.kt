package com.muralex.achiever.presentation.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.data.models.datamodels.showProgress
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.databinding.HomeListItemBinding
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.Action

class HomeListAdapter : ListAdapter<GroupData, HomeListAdapter.ViewHolder>(HomeListDiffCallBack()) {

    class ViewHolder(val binding: HomeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val viewForeground = binding.viewForeground
        val swipeArchiveLeft = binding.bgArchiveLeft
        val swipeArchiveRight = binding.bgArchiveRight

        fun bind(groupData: GroupData, onItemClickListener: ((Action, GroupData) -> Unit)?) {
            binding.tvNoteTitle.setTexNoLine(groupData.title)
            binding.ivNoteImage.setImageSource(groupData.group?.image)

            binding.homeCardWrap.setOnClickListener {
                onItemClickListener?.let {
                    it(Action.Click, groupData)
                }
            }

            binding.ivGroupMenu.setOnClickListener {
                onItemClickListener?.let {
                    it(Action.MenuClick, groupData)
                }
            }

            binding.homeCardWrap.setOnLongClickListener {
                onItemClickListener?.let {
                    it(Action.LongClick, groupData)
                }
                true
            }

            binding.progressBar.progress = groupData.progress
            binding.progressBar.setProgressColor(groupData.progress)
            binding.tvGroupProgress.setGroupProgressText(groupData)
            binding.tvNotePr.setGroupCompletedTotalText(groupData)

            groupData.group?.let {
                if (it.showProgress()) {
                    binding.progressBar.visible()
                } else {
                    binding.progressBar.gone()
                    binding.tvGroupProgress.setGroupTotalText(groupData)
                    binding.tvNotePr.setGroupCompletedText(groupData)
                }
            }

            binding.actionTextLeft.setArchiveText(groupData)
            binding.actionTextRight.setArchiveText(groupData)
            binding.actionImageRight.setListSwipeArchiveIcon(groupData)
            binding.actionImageLeft.setListSwipeArchiveIcon(groupData)

            if (groupData.urgentItems > 0) {
                binding.cardUrgentIndicator.visible()
                binding.cardUrgentIndicatorText.text = groupData.urgentItems.toString()
            } else {
                binding.cardUrgentIndicator.gone()
            }

            if (groupData.todayItems > 0) {
                binding.cardTodayIndicator.visible()
                binding.cardTodayIndicatorText.text = groupData.todayItems.toString()
            } else {
                binding.cardTodayIndicator.gone()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HomeListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClickListener)
    }

    private var onItemClickListener: ((Action, GroupData) -> Unit)? = null

    fun setOnItemClickListener(listener: (Action, GroupData) -> Unit) {
        onItemClickListener = listener
    }
}

class HomeListDiffCallBack : DiffUtil.ItemCallback<GroupData>() {
    override fun areItemsTheSame(oldItem: GroupData, newItem: GroupData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupData, newItem: GroupData): Boolean {
        return oldItem == newItem
    }


}