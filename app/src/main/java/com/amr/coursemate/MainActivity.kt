package com.amr.coursemate

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityMainBinding
import com.amr.coursemate.databinding.DialogAddClassBinding
import com.amr.coursemate.ui.dictionary.DictionaryActivity
import com.amr.coursemate.ui.home.ClassAdapter
import com.amr.coursemate.ui.settings.SettingsActivity
import com.amr.coursemate.ui.home.MainViewModel
import com.amr.coursemate.ui.about.AboutActivity
import com.amr.coursemate.ui.adjustForKeyboard
import com.amr.coursemate.ui.viewer.ClassViewerActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(AppRepository(AppDatabase.getInstance(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val adapter = ClassAdapter(
            onClick = { _, position ->
                startActivity(
                    Intent(this, ClassViewerActivity::class.java)
                        .putExtra(ClassViewerActivity.EXTRA_CLASS_POSITION, position)
                )
            },
            onLongClick = { courseClass ->
                showEditClassDialog(courseClass)
            }
        )

        binding.recyclerClasses.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        viewModel.allClasses.observe(this) { adapter.submitList(it) }

        binding.fabAddClass.setOnClickListener { showAddClassDialog() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_dictionary -> {
            startActivity(Intent(this, DictionaryActivity::class.java))
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        R.id.action_about -> {
            startActivity(Intent(this, AboutActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showAddClassDialog() {
        val dialogBinding = DialogAddClassBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setTitle("New Class")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etClassName.text?.toString()?.trim().orEmpty()
                val description = dialogBinding.etDescription.text?.toString()?.trim().orEmpty()
                if (name.isNotEmpty()) viewModel.addClass(name, description)
            }
            .setNegativeButton("Cancel", null)
            .show()
            .adjustForKeyboard()
    }

    private fun showEditClassDialog(courseClass: CourseClass) {
        val dialogBinding = DialogAddClassBinding.inflate(layoutInflater)
        dialogBinding.etClassName.setText(courseClass.name)
        dialogBinding.etDescription.setText(courseClass.description)
        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Class")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = dialogBinding.etClassName.text?.toString()?.trim().orEmpty()
                val description = dialogBinding.etDescription.text?.toString()?.trim().orEmpty()
                if (name.isNotEmpty()) viewModel.updateClassNameAndDescription(courseClass.id, name, description)
            }
            .setNeutralButton("Delete") { _, _ ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete class?")
                    .setMessage("\"${courseClass.name}\" and all its translations will be deleted.")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteClass(courseClass) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
            .adjustForKeyboard()
    }

}