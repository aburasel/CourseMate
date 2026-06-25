package com.amr.coursemate.ui.overview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.data.model.NoteWithClass
import com.amr.coursemate.databinding.ItemClassHeaderBinding
import com.amr.coursemate.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** A flat row in the all-notes list: either a class header or a note. */
sealed class NoteListItem {
    data class Header(val className: String) : NoteListItem()
    data class Item(val note: NoteWithClass) : NoteListItem()
}

/** All notes across classes, grouped under class-name headers. Tapping a note opens its class. */
class AllNotesAdapter(
    private val onNoteClick: (NoteWithClass) -> Unit
) : ListAdapter<NoteListItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTE = 1
        private val DATE_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        private val DIFF = object : DiffUtil.ItemCallback<NoteListItem>() {
            override fun areItemsTheSame(old: NoteListItem, new: NoteListItem) = when {
                old is NoteListItem.Header && new is NoteListItem.Header -> old.className == new.className
                old is NoteListItem.Item && new is NoteListItem.Item -> old.note.id == new.note.id
                else -> false
            }

            override fun areContentsTheSame(old: NoteListItem, new: NoteListItem) = old == new
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is NoteListItem.Header -> TYPE_HEADER
        is NoteListItem.Item -> TYPE_NOTE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(ItemClassHeaderBinding.inflate(inflater, parent, false))
        } else {
            NoteViewHolder(ItemNoteBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is NoteListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is NoteListItem.Item -> (holder as NoteViewHolder).bind(item.note)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemClassHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: NoteListItem.Header) {
            binding.tvClassHeader.text = header.className
        }
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteWithClass) {
            binding.tvNoteContent.text = note.content
            binding.tvNoteContent.visibility = if (note.content.isNotEmpty()) View.VISIBLE else View.GONE
            binding.tvNoteDate.text = DATE_FORMAT.format(Date(note.createdAt))
            binding.root.setOnClickListener { onNoteClick(note) }
            binding.btnCopyNote.setOnClickListener {
                val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("note", note.content))
                Toast.makeText(it.context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
