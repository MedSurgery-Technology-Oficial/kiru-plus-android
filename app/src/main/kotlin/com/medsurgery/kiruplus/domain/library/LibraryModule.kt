package com.medsurgery.kiruplus.domain.library

data class LibraryModule(
    val id: String,
    val title: String,
    val points: List<StudyPoint>,
) {
    val pointCount: Int get() = points.size
}

data class StudyPoint(
    val id: String,
    val title: String,
    val details: String,
)
