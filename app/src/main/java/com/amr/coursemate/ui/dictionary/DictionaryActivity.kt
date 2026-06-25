package com.amr.coursemate.ui.dictionary

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.R
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.Dictionary
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityDictionaryBinding
import com.amr.coursemate.databinding.DialogAddWordBinding
import com.amr.coursemate.databinding.DialogBatchAddBinding
import com.amr.coursemate.ocr.OcrHelper
import com.amr.coursemate.ui.adjustForKeyboard
import com.amr.coursemate.ui.parseScriptPairs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class DictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDictionaryBinding

    private val viewModel: DictionaryViewModel by viewModels {
        DictionaryViewModel.Factory(AppRepository(AppDatabase.getInstance(this)))
    }

    /** EditText that the next OCR result should be written into. */
    private var ocrTarget: EditText? = null

    /** Uri the camera writes the captured photo into. */
    private var captureUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) captureUri?.let { runOcr(it) }
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
            .adjustForKeyboard()
    }

    private fun showBatchAddDialog() {
        val dialogBinding = DialogBatchAddBinding.inflate(layoutInflater)
        dialogBinding.btnScanImage.setOnClickListener {
            launchImageScan(dialogBinding.etBatchInput)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle("Batch Add Words")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val entries = parseScriptPairs(dialogBinding.etBatchInput.text?.toString().orEmpty())
                    .filter { (bangla, arabic) -> bangla.isNotEmpty() && arabic.isNotEmpty() }
                    .map { (bangla, arabic) -> Dictionary(arabic = arabic, bangla = bangla) }
                if (entries.isNotEmpty()) viewModel.addBatch(entries)
            }
            .setNegativeButton("Cancel", null)
            .show()
            .adjustForKeyboard()
    }

    private fun launchImageScan(target: EditText) {
        if (!OcrHelper.hasLanguageData(this)) {
            Toast.makeText(this, "OCR language data not installed", Toast.LENGTH_LONG).show()
            return
        }
        ocrTarget = target
        captureUri = OcrHelper.createImageCaptureUri(this)
        takePicture.launch(captureUri!!)
    }

    private fun runOcr(uri: Uri) {
        val target = ocrTarget ?: return
        Toast.makeText(this, "Reading text…", Toast.LENGTH_SHORT).show()
        val appContext = applicationContext
        lifecycleScope.launch {
            val text = OcrHelper.recognize(appContext, uri)
            if (text.isBlank()) {
                Toast.makeText(this@DictionaryActivity, "No text detected in image", Toast.LENGTH_SHORT).show()
            } else {
                target.append(if (target.text.isNullOrBlank()) text else "\n$text")
            }
        }
    }
}