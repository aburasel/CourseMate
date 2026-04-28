package com.amr.coursemate.ui.viewer

import androidx.lifecycle.*
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.model.Translation
import com.amr.coursemate.data.repository.AppRepository
import kotlinx.coroutines.launch

class ClassPageViewModel(private val repository: AppRepository, private val classId: Long) : ViewModel() {

    val translations = repository.getTranslationsForClass(classId)
    val courseClass = repository.getClassById(classId)
    private val notes = repository.getNotesForClass(classId)

    val translationsWithNotes: LiveData<List<TranslationWithNote>> =
        MediatorLiveData<List<TranslationWithNote>>().apply {
            var latestTranslations: List<Translation> = emptyList()
            var latestNotes: List<Note> = emptyList()

            fun combine() {
                val noteMap = latestNotes
                    .filter { it.translationId != null }
                    .associateBy { it.translationId!! }
                value = latestTranslations.map { t -> TranslationWithNote(t, noteMap[t.id]) }
            }

            addSource(translations) { latestTranslations = it ?: emptyList(); combine() }
            addSource(notes) { latestNotes = it ?: emptyList(); combine() }
        }

    val newlyCreatedNoteId = MutableLiveData<Long?>(null)

    fun addTranslation(bangla: String, arabic: String) = viewModelScope.launch {
        repository.addTranslation(classId, bangla, arabic)
    }

    fun deleteTranslation(translation: Translation) = viewModelScope.launch {
        repository.deleteTranslation(translation)
    }

    fun addNoteForTranslation(translationId: Long, content: String) = viewModelScope.launch {
        val noteId = repository.addNoteForTranslation(classId, translationId, content)
        newlyCreatedNoteId.postValue(noteId)
    }

    fun clearNewNoteId() { newlyCreatedNoteId.value = null }

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
