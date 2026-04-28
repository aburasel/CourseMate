package com.amr.coursemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amr.coursemate.data.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE classId = :classId ORDER BY createdAt DESC")
    fun getNotesForClass(classId: Long): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY id ASC")
    suspend fun getAllList(): List<Note>

    @Query("SELECT * FROM notes WHERE classId = :classId AND content = :content LIMIT 1")
    suspend fun findByContent(classId: Long, content: String): Note?

    @Insert
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
