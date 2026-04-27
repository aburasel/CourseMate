package com.amr.coursemate.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = CourseClass::class,
        parentColumns = ["id"],
        childColumns = ["classId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("classId")]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
