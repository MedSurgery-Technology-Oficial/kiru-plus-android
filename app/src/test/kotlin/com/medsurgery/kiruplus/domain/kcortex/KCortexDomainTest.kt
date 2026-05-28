package com.medsurgery.kiruplus.domain.kcortex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KCortexDomainTest {

    // ── KCortexAnalysisType enum ──────────────────────────────────────────────

    @Test
    fun `KCortexAnalysisType has exactly 5 values`() {
        assertEquals(5, KCortexAnalysisType.entries.size)
    }

    @Test
    fun `KCortexAnalysisType LABORATORIOS has correct backendKey`() {
        assertEquals("laboratorios", KCortexAnalysisType.LABORATORIOS.backendKey)
    }

    @Test
    fun `KCortexAnalysisType GASOMETRIA has correct backendKey`() {
        assertEquals("gasometria", KCortexAnalysisType.GASOMETRIA.backendKey)
    }

    @Test
    fun `KCortexAnalysisType ECG has correct backendKey`() {
        assertEquals("ecg", KCortexAnalysisType.ECG.backendKey)
    }

    @Test
    fun `KCortexAnalysisType IMAGEN_MEDICA has correct backendKey`() {
        assertEquals("imagen_medica", KCortexAnalysisType.IMAGEN_MEDICA.backendKey)
    }

    @Test
    fun `KCortexAnalysisType TEXTO_CLINICO has correct backendKey`() {
        assertEquals("texto_clinico", KCortexAnalysisType.TEXTO_CLINICO.backendKey)
    }

    @Test
    fun `all KCortexAnalysisType displayNames are non-empty`() {
        KCortexAnalysisType.entries.forEach { type ->
            assertTrue("displayName must not be blank for $type", type.displayName.isNotBlank())
        }
    }

    @Test
    fun `KCortexAnalysisType displayNames are human-readable strings`() {
        assertEquals("Laboratorios", KCortexAnalysisType.LABORATORIOS.displayName)
        assertEquals("Gasometría", KCortexAnalysisType.GASOMETRIA.displayName)
        assertEquals("ECG", KCortexAnalysisType.ECG.displayName)
        assertEquals("Imagen Médica", KCortexAnalysisType.IMAGEN_MEDICA.displayName)
        assertEquals("Texto Clínico", KCortexAnalysisType.TEXTO_CLINICO.displayName)
    }

    // ── KCortexAnalysis domain model ─────────────────────────────────────────

    private fun makeAnalysis(
        status: String = "success",
        clinicalData: List<ClinicalDataRow> = emptyList(),
        redFlags: List<String> = emptyList(),
    ) = KCortexAnalysis(
        id = "test-id",
        analysisType = KCortexAnalysisType.LABORATORIOS,
        status = status,
        quality = "Analizable",
        modality = "laboratorios",
        findings = "findings",
        preliminaryInterpretation = "interpretation",
        limitations = "none",
        missingData = "",
        recommendations = "consult",
        redFlags = redFlags,
        clinicalData = clinicalData,
        clinicalDataExtractedCount = clinicalData.size,
        adminDataIgnoredCount = 0,
        rejectionReason = null,
        rawInput = "raw input",
    )

    @Test
    fun `isUsable is true when status is success`() {
        assertTrue(makeAnalysis(status = "success").isUsable)
    }

    @Test
    fun `isUsable is true when status is partial`() {
        assertTrue(makeAnalysis(status = "partial").isUsable)
    }

    @Test
    fun `isUsable is false when status is rejected`() {
        assertFalse(makeAnalysis(status = "rejected").isUsable)
    }

    @Test
    fun `hasCriticalFindings is false when no critical data and no red flags`() {
        val analysis = makeAnalysis(
            clinicalData = listOf(
                ClinicalDataRow("Hb", "12", "g/dL", "12-16", "Normal", "Alta"),
            ),
            redFlags = emptyList(),
        )
        assertFalse(analysis.hasCriticalFindings)
    }

    @Test
    fun `hasCriticalFindings is true when clinicalData contains critical row`() {
        val criticalRow = ClinicalDataRow("K+", "6.8", "mEq/L", "3.5-5.0", "Crítico", "Alta")
        val analysis = makeAnalysis(clinicalData = listOf(criticalRow), redFlags = emptyList())
        assertTrue(analysis.hasCriticalFindings)
    }

    @Test
    fun `hasCriticalFindings is true when redFlags is non-empty`() {
        val analysis = makeAnalysis(
            clinicalData = emptyList(),
            redFlags = listOf("Acidosis metabólica severa"),
        )
        assertTrue(analysis.hasCriticalFindings)
    }

    // ── ClinicalDataRow ───────────────────────────────────────────────────────

    @Test
    fun `ClinicalDataRow isCritical true for interpretation containing Crítico`() {
        val row = ClinicalDataRow("Na+", "165", "mEq/L", "135-145", "Crítico", "Alta")
        assertTrue(row.isCritical)
    }

    @Test
    fun `ClinicalDataRow isCritical true for interpretation containing CRIT uppercase`() {
        val row = ClinicalDataRow("Glucosa", "600", "mg/dL", "70-100", "CRITICO", "Alta")
        assertTrue(row.isCritical)
    }

    @Test
    fun `ClinicalDataRow isCritical false for Normal interpretation`() {
        val row = ClinicalDataRow("Hb", "14", "g/dL", "12-16", "Normal", "Alta")
        assertFalse(row.isCritical)
    }

    @Test
    fun `ClinicalDataRow isAbnormal true for Alto interpretation`() {
        val row = ClinicalDataRow("PCR", "120", "mg/L", "0-5", "Alto", "Alta")
        assertTrue(row.isAbnormal)
    }

    @Test
    fun `ClinicalDataRow isAbnormal true for Bajo interpretation`() {
        val row = ClinicalDataRow("Plaquetas", "50000", "/uL", "150000-400000", "Bajo", "Alta")
        assertTrue(row.isAbnormal)
    }

    @Test
    fun `ClinicalDataRow isAbnormal false for Normal interpretation`() {
        val row = ClinicalDataRow("Hb", "13", "g/dL", "12-16", "Normal", "Alta")
        assertFalse(row.isAbnormal)
    }

    // ── KCortexRequest ────────────────────────────────────────────────────────

    @Test
    fun `KCortexRequest stores analysisType and clinicalInput`() {
        val request = KCortexRequest(
            analysisType = KCortexAnalysisType.GASOMETRIA,
            clinicalInput = "pH 7.28 PaCO2 52 HCO3 18",
        )
        assertEquals(KCortexAnalysisType.GASOMETRIA, request.analysisType)
        assertEquals("pH 7.28 PaCO2 52 HCO3 18", request.clinicalInput)
    }
}
