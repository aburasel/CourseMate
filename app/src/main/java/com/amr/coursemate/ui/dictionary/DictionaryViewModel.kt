package com.amr.coursemate.ui.dictionary

import androidx.lifecycle.*
import com.amr.coursemate.data.model.Dictionary
import com.amr.coursemate.data.repository.AppRepository
import kotlinx.coroutines.launch

class DictionaryViewModel(private val repo: AppRepository) : ViewModel() {

    private val searchQuery = MutableLiveData("")

    val entries: LiveData<List<Dictionary>> = searchQuery.switchMap { q ->
        if (q.isBlank()) repo.getAllDictionary() else repo.searchDictionary(q)
    }

    fun setQuery(q: String) { searchQuery.value = q }

    fun addWord(arabic: String, meaning: String) = viewModelScope.launch {
        repo.addDictionaryEntry(arabic, meaning)
    }

    fun addBatch(entries: List<Dictionary>) = viewModelScope.launch {
        repo.addDictionaryEntries(entries)
    }

    fun deleteEntry(entry: Dictionary) = viewModelScope.launch {
        repo.deleteDictionaryEntry(entry)
    }

    class Factory(private val repo: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DictionaryViewModel(repo) as T
    }
}