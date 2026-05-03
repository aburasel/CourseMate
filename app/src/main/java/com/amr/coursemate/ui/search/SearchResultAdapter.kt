package com.amr.coursemate.ui.search

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.data.model.TranslationWithClass
import com.amr.coursemate.databinding.ItemLoadingFooterBinding
import com.amr.coursemate.databinding.ItemSearchResultBinding

class SearchResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<TranslationWithClass>()
    private var showFooter = false

    fun submitList(newItems: List<TranslationWithClass>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size
            override fun areItemsTheSame(old: Int, new: Int) = items[old].id == newItems[new].id
            override fun areContentsTheSame(old: Int, new: Int) = items[old] == newItems[new]
        })
        items.clear()
        items.addAll(newItems)
        diff.dispatchUpdatesTo(this)
    }

    fun setFooterVisible(visible: Boolean) {
        if (showFooter == visible) return
        showFooter = visible
        if (visible) notifyItemInserted(items.size) else notifyItemRemoved(items.size)
    }

    override fun getItemCount() = items.size + if (showFooter) 1 else 0

    override fun getItemViewType(position: Int) = if (position < items.size) TYPE_ITEM else TYPE_FOOTER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_ITEM)
            ItemViewHolder(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else
            FooterViewHolder(ItemLoadingFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) holder.bind(items[position])
    }

    inner class ItemViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TranslationWithClass) {
            binding.tvClassName.text = item.className
            binding.tvBangla.text = item.bangla
            binding.tvArabic.text = item.arabic
            binding.root.setOnClickListener {
                val text = "${item.bangla}\n${item.arabic}"
                val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("translation", text))
                Toast.makeText(it.context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class FooterViewHolder(binding: ItemLoadingFooterBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1
    }
}
