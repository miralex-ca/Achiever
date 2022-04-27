package com.example.app_1.ui.item_action

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muralex.achiever.databinding.DialogItemActionStatusListItemBinding


class ActionStatusPickerListAdapter(

    private val statusList: List<String>,
    private var selected: String,
    private val onItemClicker: (String) -> Unit

) : RecyclerView.Adapter<ActionStatusPickerListAdapter.ViewHolder>() {

    class ViewHolder private constructor(val binding: DialogItemActionStatusListItemBinding): RecyclerView.ViewHolder (binding.root) {

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
                val binding = DialogItemActionStatusListItemBinding.inflate(layoutInflater)
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

    private fun updateData(status: String) {
        selected = status
        onItemClicker(status)

    }

    override fun getItemCount(): Int {
       return statusList.size
    }




}