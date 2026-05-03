package com.amr.coursemate.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amr.coursemate.data.model.TranslationWithClass
import com.amr.coursemate.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(private val repo: AppRepository) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 20
    }

    private val _results = MutableLiveData<List<TranslationWithClass>>(emptyList())
    val results: LiveData<List<TranslationWithClass>> = _results

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasMore = MutableLiveData(false)
    val hasMore: LiveData<Boolean> = _hasMore

    private var currentQuery = ""
    private var offset = 0
    private var isLoadingMore = false
    private var searchJob: Job? = null

    init {
        search("")
    }

    fun search(query: String) {
        searchJob?.cancel()
        currentQuery = query
        offset = 0
        isLoadingMore = false
        searchJob = viewModelScope.launch {
            _isLoading.value = true
            val page = repo.searchTranslations(query, PAGE_SIZE + 1, 0)
            val hasMore = page.size > PAGE_SIZE
            val items = if (hasMore) page.dropLast(1) else page
            _results.value = items
            _hasMore.value = hasMore
            offset = items.size
            _isLoading.value = false
        }
    }

    fun loadMore() {
        if (isLoadingMore || _hasMore.value != true) return
        isLoadingMore = true
        viewModelScope.launch {
            _isLoading.value = true
            val page = repo.searchTranslations(currentQuery, PAGE_SIZE + 1, offset)
            val hasMore = page.size > PAGE_SIZE
            val items = if (hasMore) page.dropLast(1) else page
            _results.value = _results.value.orEmpty() + items
            _hasMore.value = hasMore
            offset += items.size
            isLoadingMore = false
            _isLoading.value = false
        }
    }

    class Factory(private val repo: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = SearchViewModel(repo) as T
    }
}
