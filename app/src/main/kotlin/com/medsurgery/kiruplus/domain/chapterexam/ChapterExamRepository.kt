package com.medsurgery.kiruplus.domain.chapterexam

import kotlinx.coroutines.flow.Flow

interface ChapterExamRepository {
    fun getChapterExam(examId: String): Flow<Result<ChapterExam?>>
    fun getAvailableExamIds(): Flow<Result<Set<String>>>
}
