package com.muralex.achiever.presentation.fragments.group_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.data.models.datamodels.showProgress
import com.muralex.achiever.data.models.usemodels.GroupData
import com.muralex.achiever.data.models.usemodels.ItemInGroup
import com.muralex.achiever.databinding.ListItemGroupDataitemBinding
import com.muralex.achiever.databinding.ListItemGroupDetailBinding
import com.muralex.achiever.databinding.ListItemGroupEmptyListBinding
import com.muralex.achiever.presentation.utils.*
import com.muralex.achiever.presentation.utils.Constants.Action
import timber.log.Timber

class ItemsListAdapter : ListAdapter<GroupDetailItem, RecyclerView.ViewHolder>(HomeListDiffCallBack()) {

    class ViewHolder (private val binding: ListItemGroupDataitemBinding) : RecyclerView.ViewHolder (binding.root) {

        fun bind(dataItem: ItemInGroup, onItemClickListener: ((Action, ItemInGroup) -> Unit)?) {
            binding.tvNoteTitle.setTexNoLine(dataItem.title)

            binding.tvNoteDesc.apply {
                defineDescText(dataItem)
            }

            binding.boxScheduled.displayIf(dataItem.schedule.isScheduled)

            if (dataItem.schedule.isScheduled) {
                binding.ivTaskRepeat.displayIf(dataItem.schedule.isRepeated)
                binding.tvTaskCompleted.displayIf(dataItem.schedule.displayCompletedTime)
                binding.tvNextDate.displayIf(dataItem.schedule.displayNextDate)
                binding.tvDueDate.displayIf(dataItem.schedule.displayDueDate)
                binding.dateSpace.displayIf(dataItem.schedule.displayDateSpace)
                binding.tvTaskCompleted.setCompletedText(dataItem)
                binding.tvDueDate.setDueDateText(dataItem)
                binding.tvNextDate.setNextDateText(dataItem)
            }

            binding.homeCardWrap.setOnClickListener {
                onItemClickListener?.let {  it (Action.Click, dataItem) }
            }

            binding.ivItemImage.setOnClickListener {
                onItemClickListener?.let { it (Action.TodoStatus, dataItem) }
            }

            binding.ivItemImage.setTodoStatusImage(dataItem.displayStatus)
            binding.ivPinned.setPinnedIcon(dataItem.data)

            binding.homeCardWrap.setOnLongClickListener {
                onItemClickListener?.let {
                    it (Action.LongClick, dataItem)
                }
                true
            }
        }

        companion object {
            fun from (parent: ViewGroup) : ViewHolder {
                val layoutInflater =LayoutInflater.from(parent.context)
                val binding = ListItemGroupDataitemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class GroupDetailViewHolder (val binding: ListItemGroupDetailBinding) : RecyclerView.ViewHolder (binding.root) {

        fun bind(groupData: GroupData, onGroupClickListener: ((Action, GroupData) -> Unit)?) {
            binding.tvDetailTitle.setTexNoLine(groupData.title)

            binding.ivDetailImage.setGroupImageSource(groupData.group?.image)

            if (groupData.text.isNullOrEmpty()  ) {
                binding.tvGroupDesc.gone()
            } else {
                binding.tvGroupDesc.setTexNoLine(groupData.text)
            }

            binding.groupCardWrap.setOnClickListener {
                onGroupClickListener?.let { it (Action.OpenGroup, groupData) }
            }

            binding.tvGroupTotalTask.setGroupTotalProgressText( groupData )

            groupData.group?.let {
                if (it.showProgress()) {
                    binding.progressBar.visible()
                } else {
                    binding.progressBar.gone()
                }
            }

            binding.progressBar.progress = groupData.progress
            binding.progressBar.setProgressColor(groupData.progress)

        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemGroupDetailBinding.inflate(layoutInflater, parent, false)
                return GroupDetailViewHolder(binding)
            }
        }
    }


    class GroupEmptyListViewHolder (val binding: ListItemGroupEmptyListBinding) : RecyclerView.ViewHolder (binding.root) {

        fun bind(onGroupClickListener: ((Action, GroupData) -> Unit)?) {

        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemGroupEmptyListBinding.inflate(layoutInflater, parent, false)
                return GroupEmptyListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_GROUP -> GroupDetailViewHolder.from(parent)
            ITEM_VIEW_TYPE_EMPTY ->  GroupEmptyListViewHolder.from(parent)
            else -> ViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as GroupDetailItem.TaskInfo
                holder.bind(item.taskInfo, onItemClickListener )
            }

            is GroupDetailViewHolder -> {
                val item = getItem(position) as GroupDetailItem.GroupDetailInfo
                holder.bind(item.groupData, onGroupClickListener )
            }

            is GroupEmptyListViewHolder -> {
                holder.bind(onGroupClickListener )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GroupDetailItem.GroupDetailInfo -> ITEM_VIEW_TYPE_GROUP
            is GroupDetailItem.TaskInfo -> ITEM_VIEW_TYPE_TASK
            is GroupDetailItem.GroupDetailEmptyList -> ITEM_VIEW_TYPE_EMPTY
        }
    }

    private var onItemClickListener : ( (Action, ItemInGroup) -> Unit )?= null
    private var onGroupClickListener : ( (Action, GroupData) -> Unit )?= null

    fun setOnItemClickListener (listener:  (Action, ItemInGroup) -> Unit ) {
        onItemClickListener = listener
    }

    fun setOnItemGroupListener (listener:  (Action, GroupData) -> Unit ) {
        onGroupClickListener = listener
    }

    companion object {
        const val ITEM_VIEW_TYPE_TASK = 1
        const val ITEM_VIEW_TYPE_GROUP = 2
        const val ITEM_VIEW_TYPE_EMPTY = 3
    }
}



class HomeListDiffCallBack : DiffUtil.ItemCallback<GroupDetailItem>() {

    override fun areItemsTheSame(oldItem: GroupDetailItem, newItem: GroupDetailItem): Boolean {
       return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupDetailItem, newItem: GroupDetailItem): Boolean {
        return oldItem == newItem
    }
}

sealed class GroupDetailItem {

    data class GroupDetailInfo (val groupData: GroupData): GroupDetailItem() {
        override val id: String = groupData.id.toString()
    }

    data class TaskInfo (val taskInfo: ItemInGroup ): GroupDetailItem() {
        override val id = taskInfo.id
    }

    data class GroupDetailEmptyList (val empty: Boolean): GroupDetailItem() {
        override val id = "empty_group_list"
    }

    abstract val id: String
}