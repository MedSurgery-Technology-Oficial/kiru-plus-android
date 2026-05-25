package com.medsurgery.kiruplus.domain.chapterexam

data class ChapterExam(
    val id: String,
    val chapterNumber: Int,
    val title: String,
    val questions: List<ChapterExamQuestion>,
) {
    val questionCount: Int get() = questions.size
}

data class ChapterExamQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val rationale: String,
) {
    val correctAnswer: String get() = options.getOrElse(correctIndex) { "" }
}

/** Derives the ChapterExams.json examId from a curriculum chapter's position. */
fun examIdForChapter(blockNumber: Int, unitNumber: Int, chapterNumber: Int): String {
    val globalIndex = (blockNumber - 1) * 25 + (unitNumber - 1) * 5 + chapterNumber
    return "Chapter-" + globalIndex.toString().padStart(3, '0')
}
