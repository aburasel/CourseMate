package com.amr.coursemate.ui.viewer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.ActivityClassViewerBinding
import com.amr.coursemate.ui.home.MainViewModel

class ClassViewerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLASS_POSITION = "class_position"
    }

    private lateinit var binding: ActivityClassViewerBinding
    private lateinit var pagerAdapter: ClassPagerAdapter

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(AppRepository(AppDatabase.getInstance(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val initialPosition = intent.getIntExtra(EXTRA_CLASS_POSITION, 0)
        pagerAdapter = ClassPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        var initialScrollDone = false
        viewModel.allClasses.observe(this) { classes ->
            pagerAdapter.submitList(classes)
            if (!initialScrollDone && classes.isNotEmpty()) {
                binding.viewPager.setCurrentItem(initialPosition.coerceAtMost(classes.lastIndex), false)
                initialScrollDone = true
            }
            updateTitle(binding.viewPager.currentItem)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateTitle(position)
        })
    }

    private fun updateTitle(position: Int) {
        val name = viewModel.allClasses.value?.getOrNull(position)?.name ?: "CourseMate"
        supportActionBar?.title = name
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}