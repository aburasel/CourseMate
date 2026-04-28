package com.amr.coursemate.ui.dictionary

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.R
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.Dictionary
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityDictionaryBinding
import com.amr.coursemate.databinding.DialogAddWordBinding
import com.amr.coursemate.databinding.DialogBatchAddBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDictionaryBinding

    private val viewModel: DictionaryViewModel by viewModels {
        DictionaryViewModel.Factory(AppRepository(AppDatabase.getInstance(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = DictionaryAdapter { entry ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete word?")
                .setMessage("\"${entry.bangla}\" will be removed from the dictionary.")
                .setPositiveButton("Delete") { _, _ -> viewModel.deleteEntry(entry) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.recyclerDictionary.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@DictionaryActivity)
        }

        viewModel.entries.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dictionary, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search words…"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText.orEmpty())
                return true
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem) = true
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                viewModel.setQuery("")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish(); true
        }

        R.id.action_add_word -> {
            showAddWordDialog(); true
        }

        R.id.action_batch_add -> {
            showBatchAddDialog(); true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun showAddWordDialog() {
        val dialogBinding = DialogAddWordBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setTitle("Add Word")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val arabic = dialogBinding.etArabic.text?.toString()?.trim().orEmpty()
                val meaning = dialogBinding.etMeaning.text?.toString()?.trim().orEmpty()
                if (arabic.isNotEmpty() && meaning.isNotEmpty()) viewModel.addWord(arabic, meaning)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBatchAddDialog() {
        val dialogBinding = DialogBatchAddBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setTitle("Batch Add Words")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val lines = dialogBinding.etBatchInput.text?.toString().orEmpty()
                    .lines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                val entries = mutableListOf<Dictionary>()
                var i = 0
                while (i + 1 < lines.size) {
                    entries += Dictionary(arabic = lines[i], bangla = lines[i + 1])
                    i += 2
                }
                if (entries.isNotEmpty()) viewModel.addBatch(entries)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}