package com.amr.coursemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.model.Translation

@Dao
interface TranslationDao {

    @Query("SELECT * FROM translations WHERE classId = :classId ORDER BY id ASC")
    fun getTranslationsForClass(classId: Long): LiveData<List<Translation>>

    @Query("SELECT * FROM translations ORDER BY id ASC")
    suspend fun getAllList(): List<Translation>

    @Query("SELECT * FROM translations WHERE classId = :classId AND bangla = :bangla AND arabic = :arabic LIMIT 1")
    suspend fun findByContent(classId: Long, bangla: String, arabic: String): Translation?

    @Insert
    suspend fun insert(translation: Translation): Long

    @Query("UPDATE translations SET bangla = :bangla AND arabic = :arabic WHERE id = :id")
    suspend fun updateTranslation(id: Long, bangla: String, arabic:String)

    @Update
    suspend fun update(translation: Translation)

    @Delete
    suspend fun delete(translation: Translation)
}