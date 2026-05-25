package com.medsurgery.kiruplus.data.library.dto

import com.medsurgery.kiruplus.domain.library.LibraryModule
import com.medsurgery.kiruplus.domain.library.StudyPoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyModulesFileDto(
    val modules: List<StudyModuleDto>,
)

@Serializable
data class StudyModuleDto(
    val id: String,
    val title: String,
    @SerialName("icon") val iconName: String = "",
    val content: List<StudyPointDto> = emptyList(),
) {
    fun toDomain() = LibraryModule(
        id = id,
        title = title,
        points = content.map { it.toDomain() },
    )
}

@Serializable
data class StudyPointDto(
    val id: String,
    val title: String,
    val details: String = "",
) {
    fun toDomain() = StudyPoint(id = id, title = title, details = details)
}
