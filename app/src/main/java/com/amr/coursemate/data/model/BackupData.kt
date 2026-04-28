package com.amr.coursemate.data.model

data class BackupData(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val classes: List<CourseClass> = emptyList(),
    val translations: List<Translation> = emptyList(),
    val notes: List<Note> = emptyList(),
    val dictionary: List<Dictionary> = emptyList()
)

data class ImportResult(
    val classesAdded: Int,
    val translationsAdded: Int,
    val notesAdded: Int,
    val dictionaryAdded: Int
)