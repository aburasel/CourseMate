package com.amr.coursemate.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.data.model.Dictionary
import com.amr.coursemate.databinding.ItemDictionaryBinding

class DictionaryAdapter(
    private val onLongClick: (Dictionary) -> Unit
) : ListAdapter<Dictionary, DictionaryAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemDictionaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: Dictionary) {
            binding.tvArabic.text = entry.arabic
            binding.tvMeaning.text = entry.bangla
            binding.root.setOnLongClickListener { onLongClick(entry); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemDictionaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Dictionary>() {
            override fun areItemsTheSame(a: Dictionary, b: Dictionary) = a.id == b.id
            override fun areContentsTheSame(a: Dictionary, b: Dictionary) = a == b
        }
    }
}