package com.medsurgery.kiruplus.data.library.dto

import com.medsurgery.kiruplus.domain.library.CurriculumBlock
import com.medsurgery.kiruplus.domain.library.CurriculumChapter
import com.medsurgery.kiruplus.domain.library.CurriculumUnit
import kotlinx.serialization.Serializable

@Serializable
data class CurriculumIndexFileDto(
    val schemaVersion: Int = 1,
    val blocks: List<CurriculumBlockDto> = emptyList(),
)

@Serializable
data class CurriculumBlockDto(
    val id: String,
    val blockNumber: Int,
    val title: String,
    val units: List<CurriculumUnitDto> = emptyList(),
) {
    fun toDomain() = CurriculumBlock(
        id = id,
        blockNumber = blockNumber,
        title = title,
        units = units.map { it.toDomain() },
    )
}

@Serializable
data class CurriculumUnitDto(
    val id: String,
    val unitNumber: Int,
    val title: String,
    val chapters: List<CurriculumChapterDto> = emptyList(),
) {
    fun toDomain() = CurriculumUnit(
        id = id,
        unitNumber = unitNumber,
        title = title,
        chapters = chapters.map { it.toDomain() },
    )
}

@Serializable
data class CurriculumChapterDto(
    val id: String,
    val chapterNumber: Int,
    val title: String,
    val status: String = "available",
) {
    fun toDomain() = CurriculumChapter(
        id = id,
        chapterNumber = chapterNumber,
        title = title,
        isAvailable = status == "available",
    )
}
