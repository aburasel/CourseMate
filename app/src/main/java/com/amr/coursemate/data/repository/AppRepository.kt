package com.amr.coursemate.data.repository

import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.CourseClass
import com.amr.coursemate.data.model.Translation

class AppRepository(private val db: AppDatabase) {

    val allClasses = db.courseClassDao().getAllClasses()

    fun getClassById(id: Long) = db.courseClassDao().getClassById(id)

    fun getTranslationsForClass(classId: Long) = db.translationDao().getTranslationsForClass(classId)

    suspend fun addClass(name: String) = db.courseClassDao().insert(CourseClass(name = name))

    suspend fun deleteClass(courseClass: CourseClass) = db.courseClassDao().delete(courseClass)

    suspend fun updateNotes(classId: Long, notes: String) = db.courseClassDao().updateNotes(classId, notes)

    suspend fun updateHomework(classId: Long, homework: String) = db.courseClassDao().updateHomework(classId, homework)

    suspend fun addTranslation(classId: Long, bangla: String, arabic: String) =
        db.translationDao().insert(Translation(classId = classId, bangla = bangla, arabic = arabic))

    suspend fun deleteTranslation(translation: Translation) = db.translationDao().delete(translation)
}