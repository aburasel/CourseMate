package com.amr.coursemate.ui.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.repository.AppRepository
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: AppRepository, private val classId: Long) : ViewModel() {

    val notes = repository.getNotesForClass(classId)

    fun addNote(content: String) = viewModelScope.launch {
        repository.addNote(classId, content)
    }

    fun updateNote(note: Note,content: String) = viewModelScope.launch {
        repository.updateNote(note.copy(content = content))
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    class Factory(private val repository: AppRepository, private val classId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NotesViewModel(repository, classId) as T
    }
}
