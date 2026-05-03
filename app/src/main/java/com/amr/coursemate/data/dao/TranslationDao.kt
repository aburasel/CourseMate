package com.amr.coursemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amr.coursemate.data.model.Note
import com.amr.coursemate.data.model.Translation
import com.amr.coursemate.data.model.TranslationWithClass

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

    @Query("""
        SELECT t.id, t.classId, t.bangla, t.arabic, c.name AS className
        FROM translations t
        INNER JOIN course_classes c ON t.classId = c.id
        WHERE (:query = '' OR t.bangla LIKE '%' || :query || '%' OR t.arabic LIKE '%' || :query || '%')
        ORDER BY c.name ASC, t.id ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchWithClass(query: String, limit: Int, offset: Int): List<TranslationWithClass>
}