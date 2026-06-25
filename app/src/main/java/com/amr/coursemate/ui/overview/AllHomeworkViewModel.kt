package com.amr.coursemate.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amr.coursemate.data.repository.AppRepository

class AllHomeworkViewModel(repository: AppRepository) : ViewModel() {

    val homeworks = repository.getClassesWithHomework()

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AllHomeworkViewModel(repository) as T
    }
}
