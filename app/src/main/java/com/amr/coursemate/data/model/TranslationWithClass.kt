package com.amr.coursemate.data.model

data class TranslationWithClass(
    val id: Long,
    val classId: Long,
    val bangla: String,
    val arabic: String,
    val className: String
)
