package com.amr.coursemate.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.databinding.ItemClassBinding

class ClassAdapter(
    private val onClick: (CourseClass, Int) -> Unit,
    private val onLongClick: (CourseClass) -> Unit
) : ListAdapter<CourseClass, ClassAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CourseClass>() {
            override fun areItemsTheSame(old: CourseClass, new: CourseClass) = old.id == new.id
            override fun areContentsTheSame(old: CourseClass, new: CourseClass) = old == new
        }
    }

    inner class ViewHolder(private val binding: ItemClassBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CourseClass, position: Int) {
            binding.tvClassName.text = item.name
            binding.root.setOnClickListener { onClick(item, position) }
            binding.root.setOnLongClickListener { onLongClick(item); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemClassBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}