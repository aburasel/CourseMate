package com.amr.coursemate.ui.viewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amr.coursemate.data.model.CourseClass

class ClassPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private var classes: List<CourseClass> = emptyList()

    fun submitList(newList: List<CourseClass>) {
        classes = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = classes.size

    override fun createFragment(position: Int): Fragment =
        ClassPageFragment.newInstance(classes[position].id)

    // Stable IDs so ViewPager2 reuses fragments correctly across list changes
    override fun getItemId(position: Int) = classes[position].id
    override fun containsItem(itemId: Long) = classes.any { it.id == itemId }
}