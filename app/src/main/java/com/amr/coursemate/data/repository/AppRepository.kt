package com.amr.coursemate.data.repository

import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.BackupData
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.data.model.Dictionary
import com.amr.coursemate.data.model.ImportResult
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.model.Translation

class AppRepository(private val db: AppDatabase) {

    val allClasses = db.courseClassDao().getAllClasses()

    fun getClassById(id: Long) = db.courseClassDao().getClassById(id)

    fun getTranslationsForClass(classId: Long) = db.translationDao().getTranslationsForClass(classId)

    suspend fun addClass(name: String, description: String = "") = db.courseClassDao().insert(CourseClass(name = name, description = description))

    suspend fun deleteClass(courseClass: CourseClass) = db.courseClassDao().delete(courseClass)

    suspend fun updateNameAndDescription(classId: Long, name: String, description: String) = 
        db.courseClassDao().updateNameAndDescription(classId, name, description)

    suspend fun updateDescription(classId: Long, description: String) = 
        db.courseClassDao().updateDescription(classId, description)

    suspend fun updateNotes(classId: Long, notes: String) = db.courseClassDao().updateNotes(classId, notes)

    suspend fun updateHomework(classId: Long, homework: String) = db.courseClassDao().updateHomework(classId, homework)

    suspend fun addTranslation(classId: Long, bangla: String, arabic: String) =
        db.translationDao().insert(Translation(classId = classId, bangla = bangla, arabic = arabic))

    suspend fun deleteTranslation(translation: Translation) = db.translationDao().delete(translation)

    fun getNotesForClass(classId: Long) = db.noteDao().getNotesForClass(classId)

    suspend fun addNote(classId: Long, content: String) =
        db.noteDao().insert(Note(classId = classId, content = content))

    suspend fun addNoteForTranslation(classId: Long, translationId: Long, content: String): Long =
        db.noteDao().insert(Note(classId = classId, translationId = translationId, content = content))

    suspend fun updateNote(note: Note) = db.noteDao().update(note)

    suspend fun deleteNote(note: Note) = db.noteDao().delete(note)

    fun getAllDictionary() = db.dictionaryDao().getAll()

    fun searchDictionary(q: String) = db.dictionaryDao().search(q)

    suspend fun addDictionaryEntry(arabic: String, meaning: String) =
        db.dictionaryDao().insert(Dictionary(arabic = arabic, bangla = meaning))

    suspend fun addDictionaryEntries(entries: List<Dictionary>) =
        db.dictionaryDao().insertAll(entries)

    suspend fun deleteDictionaryEntry(entry: Dictionary) = db.dictionaryDao().delete(entry)

    suspend fun exportData() = BackupData(
        classes = db.courseClassDao().getAllList(),
        translations = db.translationDao().getAllList(),
        notes = db.noteDao().getAllList(),
        dictionary = db.dictionaryDao().getAllList()
    )

    suspend fun importData(data: BackupData): ImportResult {
        var classesAdded = 0
        var translationsAdded = 0
        var notesAdded = 0
        var dictionaryAdded = 0

        // Pass 1: classes — deduplicate by name, build id remap
        val classIdMap = mutableMapOf<Long, Long>()
        for (cls in data.classes) {
            val existing = db.courseClassDao().findByName(cls.name)
            if (existing != null) {
                classIdMap[cls.id] = existing.id
            } else {
                classIdMap[cls.id] = db.courseClassDao().insert(cls.copy(id = 0))
                classesAdded++
            }
        }

        // Pass 2: translations — deduplicate by (classId, bangla, arabic), build id remap
        val translationIdMap = mutableMapOf<Long, Long>()
        for (tr in data.translations) {
            val localClassId = classIdMap[tr.classId] ?: continue
            val existing = db.translationDao().findByContent(localClassId, tr.bangla, tr.arabic)
            if (existing != null) {
                translationIdMap[tr.id] = existing.id
            } else {
                translationIdMap[tr.id] = db.translationDao().insert(tr.copy(id = 0, classId = localClassId))
                translationsAdded++
            }
        }

        // Pass 3: notes — deduplicate by (classId, content), remap translationId
        for (note in data.notes) {
            val localClassId = classIdMap[note.classId] ?: continue
            val existing = db.noteDao().findByContent(localClassId, note.content)
            if (existing == null) {
                val localTranslationId = note.translationId?.let { translationIdMap[it] }
                db.noteDao().insert(note.copy(id = 0, classId = localClassId, translationId = localTranslationId))
                notesAdded++
            }
        }

        // Pass 4: dictionary — deduplicate by (arabic, bangla)
        for (word in data.dictionary) {
            val existing = db.dictionaryDao().findByArabicAndBangla(word.arabic, word.bangla)
            if (existing == null) {
                db.dictionaryDao().insert(word.copy(id = 0))
                dictionaryAdded++
            }
        }

        return ImportResult(classesAdded, translationsAdded, notesAdded, dictionaryAdded)
    }
}