package com.amr.coursemate.ui.viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.amr.coursemate.R
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityHomeworkBinding

class HomeworkActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CLASS_ID = "class_id"

        fun newIntent(context: Context, classId: Long) =
            Intent(context, HomeworkActivity::class.java).apply {
                putExtra(EXTRA_CLASS_ID, classId)
            }
    }

    private lateinit var binding: ActivityHomeworkBinding
    private val classId by lazy { intent.getLongExtra(EXTRA_CLASS_ID, 0L) }
    private var menuEdit: MenuItem? = null
    private var menuSave: MenuItem? = null

    private val viewModel: ClassPageViewModel by viewModels {
        ClassPageViewModel.ClassPageViewModelFactory(
            AppRepository(AppDatabase.getInstance(this)),
            classId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityHomeworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, insets ->
            v.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.nestedScrollView) { v, insets ->
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.updatePadding(bottom = maxOf(navBottom, imeBottom))
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Homework"

        viewModel.courseClass.observe(this) { courseClass ->
            showReadMode(courseClass?.homework.orEmpty())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_homework, menu)
        menuEdit = menu.findItem(R.id.action_edit)
        menuSave = menu.findItem(R.id.action_save)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> { enterEditMode(); true }
            R.id.action_save -> { saveAndExitEditMode(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showReadMode(homework: String) {
        val hasContent = homework.isNotEmpty()
        binding.tvContent.text = homework
        binding.tvContent.visibility = if (hasContent) View.VISIBLE else View.GONE
        binding.tvEmpty.visibility = if (hasContent) View.GONE else View.VISIBLE
        binding.tilEdit.visibility = View.GONE
        menuEdit?.isVisible = true
        menuSave?.isVisible = false
    }

    private fun enterEditMode() {
        val currentText = viewModel.courseClass.value?.homework.orEmpty()
        binding.etText.setText(currentText)
        binding.tvContent.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.tilEdit.visibility = View.VISIBLE
        menuEdit?.isVisible = false
        menuSave?.isVisible = true
        binding.etText.requestFocus()
    }

    private fun saveAndExitEditMode() {
        val text = binding.etText.text?.toString().orEmpty()
        viewModel.saveHomework(text)
        showReadMode(text)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
