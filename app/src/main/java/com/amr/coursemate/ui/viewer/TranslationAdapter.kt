package com.amr.coursemate.ui.viewer

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.R
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.model.Translation
import com.amr.coursemate.databinding.ItemTranslationBinding
import com.google.android.material.color.MaterialColors

class TranslationAdapter(
    private val onLongClick: (Translation) -> Unit,
    private val onNoteClick: (Translation, Note?) -> Unit
) : ListAdapter<TranslationWithNote, TranslationAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemTranslationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TranslationWithNote) {
            binding.tvBangla.text = item.translation.bangla
            binding.tvArabic.text = item.translation.arabic
            binding.root.setOnLongClickListener { onLongClick(item.translation); true }

            if (item.note != null) {
                binding.btnNote.setImageResource(R.drawable.ic_note)
                binding.btnNote.imageTintList = ColorStateList.valueOf(
                    MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)
                )
            } else {
                binding.btnNote.setImageResource(R.drawable.ic_note_add)
                binding.btnNote.imageTintList = ColorStateList.valueOf(
                    MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOutline)
                )
            }

            binding.btnNote.setOnClickListener { onNoteClick(item.translation, item.note) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTranslationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TranslationWithNote>() {
            override fun areItemsTheSame(old: TranslationWithNote, new: TranslationWithNote) =
                old.translation.id == new.translation.id
            override fun areContentsTheSame(old: TranslationWithNote, new: TranslationWithNote) =
                old == new
        }
    }
}