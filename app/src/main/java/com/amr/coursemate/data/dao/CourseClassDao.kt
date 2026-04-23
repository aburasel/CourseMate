package com.amr.coursemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amr.coursemate.data.model.CourseClass

@Dao
interface CourseClassDao {

    @Query("SELECT * FROM course_classes ORDER BY id ASC")
    fun getAllClasses(): LiveData<List<CourseClass>>

    @Query("SELECT * FROM course_classes WHERE id = :id")
    fun getClassById(id: Long): LiveData<CourseClass?>

    @Insert
    suspend fun insert(courseClass: CourseClass): Long

    @Delete
    suspend fun delete(courseClass: CourseClass)

    @Query("UPDATE course_classes SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("UPDATE course_classes SET homework = :homework WHERE id = :id")
    suspend fun updateHomework(id: Long, homework: String)
}