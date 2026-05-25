package com.medsurgery.kiruplus.data.chapterexam.dto

import com.medsurgery.kiruplus.domain.chapterexam.ChapterExam
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExamQuestion
import kotlinx.serialization.Serializable

@Serializable
data class ChapterExamDto(
    val id: String,
    val chapterNumber: Int,
    val title: String,
    val questions: List<ChapterExamQuestionDto>,
) {
    fun toDomain() = ChapterExam(
        id = id,
        chapterNumber = chapterNumber,
        title = title,
        questions = questions.map { it.toDomain() },
    )
}

@Serializable
data class ChapterExamQuestionDto(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val rationale: String,
) {
    fun toDomain() = ChapterExamQuestion(
        id = id,
        prompt = prompt,
        options = options,
        correctIndex = correctIndex,
        rationale = rationale,
    )
}
