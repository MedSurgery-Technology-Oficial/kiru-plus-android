package com.medsurgery.kiruplus.data.cases

import com.medsurgery.kiruplus.domain.cases.ClinicalCase
import kotlinx.serialization.Serializable

@Serializable
data class ClinicalCaseDto(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val presentation: String,
    val vitalSigns: String,
    val physicalExam: String,
    val labResults: String,
    val imaging: String,
    val diagnosis: String,
    val differentialDiagnosis: List<String> = emptyList(),
    val management: String,
    val teaching: String,
    val tags: List<String> = emptyList(),
) {
    fun toDomain() = ClinicalCase(
        id = id,
        title = title,
        category = category,
        difficulty = difficulty,
        presentation = presentation,
        vitalSigns = vitalSigns,
        physicalExam = physicalExam,
        labResults = labResults,
        imaging = imaging,
        diagnosis = diagnosis,
        differentialDiagnosis = differentialDiagnosis,
        management = management,
        teaching = teaching,
        tags = tags,
    )
}
