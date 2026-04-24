package com.amr.coursemate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.data.repository.AppRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val allClasses = repository.allClasses

    fun addClass(name: String, description: String = "") = viewModelScope.launch {
        repository.addClass(name, description)
    }

    fun deleteClass(courseClass: CourseClass) = viewModelScope.launch {
        repository.deleteClass(courseClass)
    }

    fun updateClassNameAndDescription(classId: Long, name: String, description: String) = viewModelScope.launch {
        repository.updateNameAndDescription(classId, name, description)
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(repository) as T
    }
}