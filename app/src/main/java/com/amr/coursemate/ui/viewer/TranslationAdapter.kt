package com.amr.coursemate.ui.viewer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.data.model.Translation
import com.amr.coursemate.databinding.ItemTranslationBinding

class TranslationAdapter(
    private val onLongClick: (Translation) -> Unit,
    private val onEditClick: (Translation) -> Unit
) : ListAdapter<Translation, TranslationAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemTranslationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Translation) {
            binding.tvBangla.text = item.bangla
            binding.tvArabic.text = item.arabic
            binding.root.setOnLongClickListener { onLongClick(item); true }
            binding.root.setOnClickListener { onEditClick(item) }
            binding.btnCopy.setOnClickListener {
                val text = "${item.bangla}\n${item.arabic}"
                val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("translation", text))
                Toast.makeText(it.context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemTranslationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Translation>() {
            override fun areItemsTheSame(old: Translation, new: Translation) =
                old.id == new.id

            override fun areContentsTheSame(old: Translation, new: Translation) =
                old == new
        }
    }
}