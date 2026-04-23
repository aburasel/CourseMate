package com.amr.coursemate.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "translations",
    foreignKeys = [ForeignKey(
        entity = CourseClass::class,
        parentColumns = ["id"],
        childColumns = ["classId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("classId")]
)
data class Translation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val bangla: String,
    val arabic: String
)