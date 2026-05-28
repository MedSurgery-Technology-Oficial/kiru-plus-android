package com.medsurgery.kiruplus.domain.kcortex

/**
 * Input for a K-CORTEX clinical analysis request.
 *
 * Mirrors the iOS `KCortexAnalysisType` visible cases:
 * laboratorios, gasometria, ecg, imagen_medica.
 * Android v1 uses text-only input (no on-device image capture / OCR);
 * the modality is sent as the backend key to POST /v1/kcortex/analyze.
 */
data class KCortexRequest(
    val analysisType: KCortexAnalysisType,
    val clinicalInput: String,          // free-text entered by the user
)

enum class KCortexAnalysisType(val backendKey: String, val displayName: String) {
    LABORATORIOS("laboratorios", "Laboratorios"),
    GASOMETRIA("gasometria", "Gasometría"),
    ECG("ecg", "ECG"),
    IMAGEN_MEDICA("imagen_medica", "Imagen Médica"),
    TEXTO_CLINICO("texto_clinico", "Texto Clínico"),
}
