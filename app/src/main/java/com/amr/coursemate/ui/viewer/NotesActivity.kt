package com.amr.coursemate.ui.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amr.coursemate.R
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityTextViewerBinding
import androidx.core.view.isVisible

class NotesActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CLASS_ID = "class_id"

        fun newIntent(context: Context, classId: Long) = Intent(context, NotesActivity::class.java).apply {
            putExtra(EXTRA_CLASS_ID, classId)
        }
    }

    private lateinit var binding: ActivityTextViewerBinding
    private val classId by lazy { intent.getLongExtra(EXTRA_CLASS_ID, 0L) }

    private val viewModel: ClassPageViewModel by viewModels {
        ClassPageViewModel.ClassPageViewModelFactory(AppRepository(AppDatabase.getInstance(this)), classId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notes"

        viewModel.courseClass.observe(this) { courseClass ->
            binding.tvContent.text = courseClass?.notes
            if (binding.tilEdit.isVisible) {
                binding.etText.setText(courseClass?.notes)
            }
        }

        binding.fabEdit.setOnClickListener { toggleEditMode(true) }
        binding.fabSave.setOnClickListener { saveAndExitEditMode() }
    }

    private fun toggleEditMode(editMode: Boolean) {
        if (editMode) {
            binding.tvContent.visibility = android.view.View.GONE
            binding.tilEdit.visibility = android.view.View.VISIBLE
            binding.etText.setText(binding.tvContent.text)
            binding.fabEdit.hide()
            binding.fabSave.show()
        } else {
            binding.tvContent.visibility = android.view.View.VISIBLE
            binding.tilEdit.visibility = android.view.View.GONE
            binding.fabEdit.show()
            binding.fabSave.hide()
        }
    }

    private fun saveAndExitEditMode() {
        val text = binding.etText.text?.toString().orEmpty()
        viewModel.saveNotes(text)
        toggleEditMode(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}