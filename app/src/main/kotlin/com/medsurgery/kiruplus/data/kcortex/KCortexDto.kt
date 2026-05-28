package com.medsurgery.kiruplus.data.kcortex

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for the POST /v1/kcortex/analyze response.
 * Mirrors iOS KCortexClinicalResponse (snake_case fields from backend).
 */
@Serializable
data class KCortexClinicalResponseDto(
    val status: String,
    val quality: String,
    val modality: String,
    @SerialName("clinical_data") val clinicalData: List<KCortexClinicalDataDto> = emptyList(),
    val findings: String = "",
    @SerialName("preliminary_interpretation") val preliminaryInterpretation: String = "",
    val limitations: String = "",
    @SerialName("missing_data") val missingData: String = "",
    val recommendations: String = "",
    @SerialName("red_flags") val redFlags: List<String> = emptyList(),
    @SerialName("admin_data_ignored_count") val adminDataIgnoredCount: Int = 0,
    @SerialName("clinical_data_extracted_count") val clinicalDataExtractedCount: Int = 0,
    @SerialName("rejection_reason") val rejectionReason: String? = null,
)

@Serializable
data class KCortexClinicalDataDto(
    val analyte: String,
    val value: String,
    val unit: String? = null,
    @SerialName("reference_range") val referenceRange: String? = null,
    val interpretation: String = "",
    val confidence: String = "Media",
)

/**
 * Fallback DTO used when the backend returns only a plain "analysis_text" string
 * (older K-CORTEX endpoint /v1/kcortex/analyze in text-only mode).
 */
@Serializable
data class KCortexTextResponseDto(
    val status: String,
    val analysis: String? = null,
    val message: String? = null,
)
