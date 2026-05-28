package com.medsurgery.kiruplus.domain.cases

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClinicalCaseDomainTest {

    // ── ClinicalCase.categories ───────────────────────────────────────────────

    @Test
    fun `ClinicalCase categories list contains exactly 5 categories`() {
        assertEquals(5, ClinicalCase.categories.size)
    }

    @Test
    fun `ClinicalCase categories contains Cirugia General`() {
        assertTrue(ClinicalCase.categories.contains("Cirugía General"))
    }

    @Test
    fun `ClinicalCase categories contains Trauma ATLS`() {
        assertTrue(ClinicalCase.categories.contains("Trauma / ATLS"))
    }

    @Test
    fun `ClinicalCase categories contains Cirugia de Urgencia`() {
        assertTrue(ClinicalCase.categories.contains("Cirugía de Urgencia"))
    }

    @Test
    fun `ClinicalCase categories contains Hepatobiliar`() {
        assertTrue(ClinicalCase.categories.contains("Hepatobiliar"))
    }

    @Test
    fun `ClinicalCase categories contains Colorrectal`() {
        assertTrue(ClinicalCase.categories.contains("Colorrectal"))
    }

    @Test
    fun `all ClinicalCase categories are non-blank`() {
        ClinicalCase.categories.forEach { category ->
            assertTrue("Category must not be blank", category.isNotBlank())
        }
    }

    // ── ClinicalCase.difficulties ─────────────────────────────────────────────

    @Test
    fun `ClinicalCase difficulties list contains exactly 3 levels`() {
        assertEquals(3, ClinicalCase.difficulties.size)
    }

    @Test
    fun `ClinicalCase difficulties contains Basico`() {
        assertTrue(ClinicalCase.difficulties.contains("Básico"))
    }

    @Test
    fun `ClinicalCase difficulties contains Intermedio`() {
        assertTrue(ClinicalCase.difficulties.contains("Intermedio"))
    }

    @Test
    fun `ClinicalCase difficulties contains Avanzado`() {
        assertTrue(ClinicalCase.difficulties.contains("Avanzado"))
    }

    // ── ClinicalCase data class ───────────────────────────────────────────────

    private fun makeCase(
        id: String = "c1",
        title: String = "Abdomen agudo",
        category: String = "Cirugía General",
        difficulty: String = "Intermedio",
    ) = ClinicalCase(
        id = id,
        title = title,
        category = category,
        difficulty = difficulty,
        presentation = "presentation",
        vitalSigns = "vitals",
        physicalExam = "exam",
        labResults = "labs",
        imaging = "imaging",
        diagnosis = "diagnosis",
        differentialDiagnosis = listOf("Dx1", "Dx2"),
        management = "management",
        teaching = "teaching",
        tags = listOf("tag1"),
    )

    @Test
    fun `ClinicalCase data class equality works`() {
        assertEquals(makeCase(), makeCase())
    }

    @Test
    fun `ClinicalCase differentialDiagnosis preserves all entries`() {
        val case = makeCase()
        assertEquals(2, case.differentialDiagnosis.size)
        assertEquals("Dx1", case.differentialDiagnosis[0])
    }

    @Test
    fun `ClinicalCase tags are preserved`() {
        val case = makeCase()
        assertEquals(listOf("tag1"), case.tags)
    }
}
