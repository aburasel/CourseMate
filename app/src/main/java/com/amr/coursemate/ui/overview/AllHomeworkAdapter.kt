package com.amr.coursemate.ui.overview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.databinding.ItemHomeworkBinding

/** One homework per class (class title + homework text). Tapping opens that class's homework. */
class AllHomeworkAdapter(
    private val onClick: (CourseClass) -> Unit
) : ListAdapter<CourseClass, AllHomeworkAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CourseClass>() {
            override fun areItemsTheSame(old: CourseClass, new: CourseClass) = old.id == new.id
            override fun areContentsTheSame(old: CourseClass, new: CourseClass) = old == new
        }
    }

    inner class ViewHolder(private val binding: ItemHomeworkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(courseClass: CourseClass) {
            binding.tvClassName.text = courseClass.name
            binding.tvHomeworkContent.text = courseClass.homework
            binding.root.setOnClickListener { onClick(courseClass) }
            binding.btnCopyHomework.setOnClickListener {
                val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("homework", courseClass.homework))
                Toast.makeText(it.context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemHomeworkBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}
