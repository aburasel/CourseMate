package com.amr.coursemate.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amr.coursemate.data.model.Translation
import com.amr.coursemate.data.repository.AppRepository
import kotlinx.coroutines.launch

class ClassPageViewModel(private val repository: AppRepository, private val classId: Long) : ViewModel() {

    val translations = repository.getTranslationsForClass(classId)
    val courseClass = repository.getClassById(classId)

    fun addTranslation(bangla: String, arabic: String) = viewModelScope.launch {
        repository.addTranslation(classId, bangla, arabic)
    }

    fun deleteTranslation(translation: Translation) = viewModelScope.launch {
        repository.deleteTranslation(translation)
    }

    fun saveNotes(notes: String) = viewModelScope.launch {
        repository.updateNotes(classId, notes)
    }

    fun saveHomework(homework: String) = viewModelScope.launch {
        repository.updateHomework(classId, homework)
    }

    fun saveDescription(description: String) = viewModelScope.launch {
        repository.updateDescription(classId, description)
    }

    class ClassPageViewModelFactory(private val repository: AppRepository, private val classId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ClassPageViewModel(repository, classId) as T
    }
}