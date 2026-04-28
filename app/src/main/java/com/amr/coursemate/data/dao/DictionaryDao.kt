package com.amr.coursemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amr.coursemate.data.model.Dictionary

@Dao
interface DictionaryDao {

    @Query("SELECT * FROM dictionary ORDER BY arabic ASC")
    fun getAll(): LiveData<List<Dictionary>>

    @Query("SELECT * FROM dictionary ORDER BY id ASC")
    suspend fun getAllList(): List<Dictionary>

    @Query("SELECT * FROM dictionary WHERE arabic LIKE '%' || :q || '%' OR bangla LIKE '%' || :q || '%' ORDER BY arabic ASC")
    fun search(q: String): LiveData<List<Dictionary>>

    @Query("SELECT * FROM dictionary WHERE arabic = :arabic AND bangla = :bangla LIMIT 1")
    suspend fun findByArabicAndBangla(arabic: String, bangla: String): Dictionary?

    @Insert
    suspend fun insert(entry: Dictionary): Long

    @Insert
    suspend fun insertAll(entries: List<Dictionary>)

    @Delete
    suspend fun delete(entry: Dictionary)
}