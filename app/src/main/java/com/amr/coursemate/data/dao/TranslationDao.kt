package com.amr.coursemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amr.coursemate.data.model.Translation

@Dao
interface TranslationDao {

    @Query("SELECT * FROM translations WHERE classId = :classId ORDER BY id ASC")
    fun getTranslationsForClass(classId: Long): LiveData<List<Translation>>

    @Insert
    suspend fun insert(translation: Translation): Long

    @Delete
    suspend fun delete(translation: Translation)
}