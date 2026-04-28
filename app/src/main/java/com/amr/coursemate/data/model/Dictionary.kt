package com.amr.coursemate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary")
data class Dictionary(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bangla: String,
    val arabic: String = ""
)