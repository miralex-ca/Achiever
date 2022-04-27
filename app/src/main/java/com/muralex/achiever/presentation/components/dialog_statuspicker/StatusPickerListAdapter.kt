package com.muralex.achiever.presentation.components.dialog_statuspicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.databinding.DialogStatusListItemBinding


class StatusPickerListAdapter(

    private val statusList: List<String>,
    private var selected: String,
    private val onItemClicker: (String) -> Unit

) : RecyclerView.Adapter<StatusPickerListAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: DialogStatusListItemBinding): RecyclerView.ViewHolder (binding.root) {

        fun bind(status: String, selected: Boolean, onItemClicker: (String) -> Unit) {
            binding.status = status
            binding.executePendingBindings()

            if (selected)  binding.selectedStatus.visibility = View.VISIBLE
            else binding.selectedStatus.visibility = View.GONE

            binding.wrap.setOnClickListener {
                onItemClicker(status)
                binding.selectedStatus.visibility = View.VISIBLE
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DialogStatusListItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val status = statusList[position]
        holder.bind(status, selected == status) { updateData(status) }
    }

    fun updateData(status: String) {

        selected = status
        onItemClicker(status)

    }

    override fun getItemCount(): Int {
       return statusList.size
    }




}