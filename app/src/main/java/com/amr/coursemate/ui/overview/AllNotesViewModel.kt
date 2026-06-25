package com.amr.coursemate.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amr.coursemate.data.repository.AppRepository

class AllNotesViewModel(repository: AppRepository) : ViewModel() {

    val notes = repository.getAllNotesWithClass()

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AllNotesViewModel(repository) as T
    }
}
