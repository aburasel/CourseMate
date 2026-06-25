package com.amr.coursemate.data.model

/** A note joined with the name of the class it belongs to (query result, not an entity). */
data class NoteWithClass(
    val id: Long,
    val classId: Long,
    val content: String,
    val createdAt: Long,
    val className: String
)
