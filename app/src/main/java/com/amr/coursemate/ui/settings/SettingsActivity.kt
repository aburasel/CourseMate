package com.amr.coursemate.ui.settings

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amr.coursemate.data.BackupManager
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var repository: AppRepository

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@registerForActivityResult
        setLoading(true)
        lifecycleScope.launch {
            try {
                val data = withContext(Dispatchers.IO) { repository.exportData() }
                val json = withContext(Dispatchers.Default) { BackupManager.toJson(data) }
                withContext(Dispatchers.IO) {
                    contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
                        ?: throw Exception("Could not open output stream")
                }
                setLoading(false)
                Snackbar.make(binding.root, "Export successful", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                setLoading(false)
                showError("Export failed: ${e.message}")
            }
        }
    }

    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@registerForActivityResult
        setLoading(true)
        lifecycleScope.launch {
            try {
                val json = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                        ?: throw Exception("Could not read file")
                }
                val data = withContext(Dispatchers.Default) { BackupManager.fromJson(json) }
                val result = withContext(Dispatchers.IO) { repository.importData(data) }
                setLoading(false)
                MaterialAlertDialogBuilder(this@SettingsActivity)
                    .setTitle("Import Complete")
                    .setMessage(
                        "Added:\n" +
                        "  • ${result.classesAdded} class(es)\n" +
                        "  • ${result.translationsAdded} translation(s)\n" +
                        "  • ${result.notesAdded} note(s)\n" +
                        "  • ${result.dictionaryAdded} dictionary word(s)"
                    )
                    .setPositiveButton("OK", null)
                    .show()
            } catch (e: Exception) {
                setLoading(false)
                showError("Import failed: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = AppRepository(AppDatabase.getInstance(this))

        binding.btnExport.setOnClickListener {
            val filename = "coursemate_${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}.json"
            exportLauncher.launch(filename)
        }

        binding.btnImport.setOnClickListener {
            importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnExport.isEnabled = !loading
        binding.btnImport.isEnabled = !loading
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}