package com.amr.coursemate.ui.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityNotesBinding
import com.amr.coursemate.databinding.DialogAddNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotesActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CLASS_ID = "class_id"
        private const val EXTRA_SCROLL_TO_NOTE_ID = "scroll_to_note_id"

        fun newIntent(context: Context, classId: Long, scrollToNoteId: Long? = null) =
            Intent(context, NotesActivity::class.java).apply {
                putExtra(EXTRA_CLASS_ID, classId)
                scrollToNoteId?.let { putExtra(EXTRA_SCROLL_TO_NOTE_ID, it) }
            }
    }

    private lateinit var binding: ActivityNotesBinding
    private val classId by lazy { intent.getLongExtra(EXTRA_CLASS_ID, 0L) }
    private var scrollToNoteId: Long? = null

    private val viewModel: NotesViewModel by viewModels {
        NotesViewModel.Factory(AppRepository(AppDatabase.getInstance(this)), classId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollToNoteId = intent.getLongExtra(EXTRA_SCROLL_TO_NOTE_ID, -1L).takeIf { it != -1L }
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notes"

        val adapter = NoteAdapter(
            onClick = { note -> showNoteDialog(note) },
            onLongClick = { note -> confirmDelete(note) }
        )

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerNotes.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }

        viewModel.notes.observe(this) { notes ->
            adapter.submitList(notes) {
                val targetId = scrollToNoteId ?: return@submitList
                val idx = notes.indexOfFirst { it.id == targetId }
                if (idx >= 0) {
                    layoutManager.scrollToPositionWithOffset(idx, 0)
                    scrollToNoteId = null
                }
            }
            binding.tvEmpty.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddNote.setOnClickListener { showNoteDialog(null) }
    }

    private fun showNoteDialog(existing: Note?) {
        val dialogBinding = DialogAddNoteBinding.inflate(layoutInflater)
        existing?.let { dialogBinding.etNoteContent.setText(it.content) }
        MaterialAlertDialogBuilder(this)
            .setTitle(if (existing == null) "New Note" else "Edit Note")
            .setView(dialogBinding.root)
            .setPositiveButton(if (existing == null) "Add" else "Save") { _, _ ->
                val content = dialogBinding.etNoteContent.text?.toString()?.trim().orEmpty()
                if (content.isNotEmpty()) {
                    if (existing == null) viewModel.addNote(content)
                    else viewModel.updateNote(existing, content)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(note: Note) {
        val label = if (note.content.isNotEmpty())
            "\"${note.content.take(10)}\".."
        else "This note"
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete note?")
            .setMessage("$label will be deleted.")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteNote(note) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}