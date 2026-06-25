package com.amr.coursemate.ui.overview

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.R
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.NoteWithClass
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityAllNotesBinding
import com.amr.coursemate.ui.viewer.NotesActivity

class AllNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllNotesBinding
    private lateinit var adapter: AllNotesAdapter

    private var allNotes: List<NoteWithClass> = emptyList()
    private var query: String = ""

    private val viewModel: AllNotesViewModel by viewModels {
        AllNotesViewModel.Factory(AppRepository(AppDatabase.getInstance(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Notes"

        adapter = AllNotesAdapter(
            onNoteClick = { note -> startActivity(NotesActivity.newIntent(this, note.classId)) }
        )

        binding.recyclerNotes.adapter = adapter
        binding.recyclerNotes.layoutManager = LinearLayoutManager(this)

        viewModel.notes.observe(this) { notes ->
            allNotes = notes
            render()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_only, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = "Search notes…"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?) = false
            override fun onQueryTextChange(text: String?): Boolean {
                query = text.orEmpty()
                render()
                return true
            }
        })
        return true
    }

    /** Filters by note content or class name, then regroups under class headers. */
    private fun render() {
        val q = query.trim()
        val filtered = if (q.isEmpty()) allNotes else allNotes.filter {
            it.content.contains(q, ignoreCase = true) || it.className.contains(q, ignoreCase = true)
        }
        val items = mutableListOf<NoteListItem>()
        var lastClass: String? = null
        for (note in filtered) {
            if (note.className != lastClass) {
                items.add(NoteListItem.Header(note.className))
                lastClass = note.className
            }
            items.add(NoteListItem.Item(note))
        }
        adapter.submitList(items)
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
