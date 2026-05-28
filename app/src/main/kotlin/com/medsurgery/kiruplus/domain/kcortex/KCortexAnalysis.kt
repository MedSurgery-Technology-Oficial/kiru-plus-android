package com.medsurgery.kiruplus.domain.kcortex

/**
 * Domain model for a completed K-CORTEX clinical analysis.
 *
 * Maps from the backend response of POST /v1/kcortex/analyze.
 * Matches iOS `KCortexClinicalResponse` → `ClinicalAnalysisResult`.
 */
data class KCortexAnalysis(
    val id: String,
    val analysisType: KCortexAnalysisType,
    val status: String,                     // "success" | "partial" | "rejected"
    val quality: String,                    // "Analizable" | "Parcialmente analizable" | "No analizable"
    val modality: String,
    val findings: String,
    val preliminaryInterpretation: String,
    val limitations: String,
    val missingData: String,
    val recommendations: String,
    val redFlags: List<String>,
    val clinicalData: List<ClinicalDataRow>,
    val clinicalDataExtractedCount: Int,
    val adminDataIgnoredCount: Int,
    val rejectionReason: String?,
    val rawInput: String,
) {
    val isUsable: Boolean get() = status == "success" || status == "partial"

    val hasCriticalFindings: Boolean
        get() = clinicalData.any { it.isCritical } || redFlags.isNotEmpty()
}

data class ClinicalDataRow(
    val analyte: String,
    val value: String,
    val unit: String,
    val referenceRange: String,
    val interpretation: String,     // "Normal", "Alto", "Bajo", "Crítico"
    val confidence: String,         // "Alta", "Media", "Baja", "No confiable"
) {
    val isCritical: Boolean
        get() = interpretation.uppercase().let { it.contains("CRÍT") || it.contains("CRIT") }

    val isAbnormal: Boolean
        get() = interpretation.uppercase().let { it == "ALTO" || it == "BAJO" || isCritical }
}
