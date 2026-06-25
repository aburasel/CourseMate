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
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityAllHomeworkBinding
import com.amr.coursemate.ui.viewer.HomeworkActivity

class AllHomeworkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllHomeworkBinding
    private lateinit var adapter: AllHomeworkAdapter

    private var allHomework: List<CourseClass> = emptyList()
    private var query: String = ""

    private val viewModel: AllHomeworkViewModel by viewModels {
        AllHomeworkViewModel.Factory(AppRepository(AppDatabase.getInstance(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllHomeworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Homework"

        adapter = AllHomeworkAdapter(
            onClick = { courseClass -> startActivity(HomeworkActivity.newIntent(this, courseClass.id)) }
        )

        binding.recyclerHomework.adapter = adapter
        binding.recyclerHomework.layoutManager = LinearLayoutManager(this)

        viewModel.homeworks.observe(this) { classes ->
            allHomework = classes
            render()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_only, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = "Search homework…"
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

    /** Filters by class name or homework content. */
    private fun render() {
        val q = query.trim()
        val filtered = if (q.isEmpty()) allHomework else allHomework.filter {
            it.name.contains(q, ignoreCase = true) || it.homework.contains(q, ignoreCase = true)
        }
        adapter.submitList(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
