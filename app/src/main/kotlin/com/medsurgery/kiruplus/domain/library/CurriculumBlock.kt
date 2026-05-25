package com.medsurgery.kiruplus.domain.library

data class CurriculumBlock(
    val id: String,
    val blockNumber: Int,
    val title: String,
    val units: List<CurriculumUnit>,
) {
    val chapterCount: Int get() = units.sumOf { it.chapters.size }
}

data class CurriculumUnit(
    val id: String,
    val unitNumber: Int,
    val title: String,
    val chapters: List<CurriculumChapter>,
)

data class CurriculumChapter(
    val id: String,
    val chapterNumber: Int,
    val title: String,
    val isAvailable: Boolean,
)
