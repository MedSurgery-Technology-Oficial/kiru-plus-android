package com.medsurgery.kiruplus.data.cases

import com.medsurgery.kiruplus.core.assets.AssetReader
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class ClinicalCasesRepositoryImplTest {

    private val validCasesJson = """
        [
          {
            "id": "case_001",
            "title": "Abdomen agudo en adulto mayor",
            "category": "Cirugía de Urgencia",
            "difficulty": "Avanzado",
            "presentation": "Paciente masculino 72 años con dolor abdominal difuso 12 h de evolución",
            "vitalSigns": "PA 90/60, FC 118, Temp 38.9",
            "physicalExam": "Abdomen rígido, signo de Blumberg positivo",
            "labResults": "Leucocitosis 18,000, PCR elevada",
            "imaging": "TAC: líquido libre en cavidad, neumoperitoneo",
            "diagnosis": "Perforación intestinal con peritonitis",
            "differentialDiagnosis": ["Apendicitis perforada", "Diverticulitis complicada"],
            "management": "Laparotomía exploradora de urgencia",
            "teaching": "El neumoperitoneo en Rx de tórax es patognomónico de perforación",
            "tags": ["abdomen agudo", "peritonitis", "urgencia"]
          },
          {
            "id": "case_002",
            "title": "Politrauma por accidente vial",
            "category": "Trauma / ATLS",
            "difficulty": "Intermedio",
            "presentation": "Hombre 28 años traído en ambulancia tras colisión de auto",
            "vitalSigns": "PA 110/70, FC 105, SpO2 94%",
            "physicalExam": "GCS 14, deformidad en extremidad inferior derecha",
            "labResults": "Hb 10.5, lactato 2.3",
            "imaging": "Rx tórax sin neumotórax, fractura fémur derecho",
            "diagnosis": "Fractura de fémur, contusión pulmonar leve",
            "differentialDiagnosis": ["Neumotórax oculto", "Hemoneumotórax"],
            "management": "ABCDE ATLS, fijación de fractura",
            "teaching": "Seguir protocolo ATLS en orden estricto",
            "tags": ["trauma", "atls", "fractura"]
          },
          {
            "id": "case_003",
            "title": "Colecistitis aguda litiásica",
            "category": "Hepatobiliar",
            "difficulty": "Básico",
            "presentation": "Mujer 45 años con dolor en hipocondrio derecho posprandial",
            "vitalSigns": "PA 120/80, FC 88, Temp 38.1",
            "physicalExam": "Murphy positivo, sin defensa abdominal",
            "labResults": "Leucocitos 12,000, bilirrubina normal",
            "imaging": "Eco: litiasis vesicular, pared engrosada",
            "diagnosis": "Colecistitis aguda litiásica",
            "differentialDiagnosis": ["Coledocolitiasis", "Pancreatitis"],
            "management": "Colecistectomía laparoscópica electiva vs urgente",
            "teaching": "Murphy positivo tiene alta especificidad para colecistitis",
            "tags": ["colecistitis", "hepatobiliar", "litiasis"]
          }
        ]
    """.trimIndent()

    private fun makeRepo(casesJson: String? = validCasesJson): ClinicalCasesRepositoryImpl {
        val assetReader = mockk<AssetReader>()
        every { assetReader.open("clinical_cases.json") } returns
            casesJson?.let { ByteArrayInputStream(it.toByteArray()) }
        return ClinicalCasesRepositoryImpl(assetReader)
    }

    // --- getCases ---

    @Test
    fun `getCases returns all 3 cases from sample JSON`() = runTest {
        val repo = makeRepo()
        val result = repo.getCases()
        assertEquals(3, result.size)
    }

    @Test
    fun `getCases maps case fields correctly`() = runTest {
        val repo = makeRepo()
        val case = repo.getCases().first()
        assertEquals("case_001", case.id)
        assertEquals("Abdomen agudo en adulto mayor", case.title)
        assertEquals("Cirugía de Urgencia", case.category)
        assertEquals("Avanzado", case.difficulty)
        assertEquals(2, case.differentialDiagnosis.size)
        assertEquals(3, case.tags.size)
    }

    @Test
    fun `getCases returns empty list when asset missing`() = runTest {
        val repo = makeRepo(casesJson = null)
        val result = repo.getCases()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCases returns empty list on malformed JSON`() = runTest {
        val repo = makeRepo(casesJson = "not json at all")
        val result = repo.getCases()
        assertTrue(result.isEmpty())
    }

    // --- getCaseById ---

    @Test
    fun `getCaseById returns correct case for valid id`() = runTest {
        val repo = makeRepo()
        val case = repo.getCaseById("case_002")
        assertEquals("Politrauma por accidente vial", case?.title)
        assertEquals("Trauma / ATLS", case?.category)
    }

    @Test
    fun `getCaseById returns null for unknown id`() = runTest {
        val repo = makeRepo()
        val case = repo.getCaseById("case_999")
        assertNull(case)
    }

    // --- getCasesByCategory ---

    @Test
    fun `getCasesByCategory returns only cases matching that category`() = runTest {
        val repo = makeRepo()
        val result = repo.getCasesByCategory("Hepatobiliar")
        assertEquals(1, result.size)
        assertEquals("case_003", result[0].id)
    }

    @Test
    fun `getCasesByCategory returns empty list for unknown category`() = runTest {
        val repo = makeRepo()
        val result = repo.getCasesByCategory("Categoría Inexistente")
        assertTrue(result.isEmpty())
    }

    // --- searchCases ---

    @Test
    fun `searchCases returns all for blank query`() = runTest {
        val repo = makeRepo()
        val cases = repo.getCases()
        val result = repo.searchCases("", cases)
        assertEquals(cases, result)
    }

    @Test
    fun `searchCases returns all for whitespace-only query`() = runTest {
        val repo = makeRepo()
        val cases = repo.getCases()
        val result = repo.searchCases("  ", cases)
        assertEquals(cases, result)
    }

    @Test
    fun `searchCases matches title case-insensitive`() = runTest {
        val repo = makeRepo()
        val cases = repo.getCases()
        val result = repo.searchCases("politrauma", cases)
        assertEquals(1, result.size)
        assertEquals("case_002", result[0].id)
    }

    @Test
    fun `searchCases matches diagnosis`() = runTest {
        val repo = makeRepo()
        val cases = repo.getCases()
        val result = repo.searchCases("peritonitis", cases)
        assertEquals(1, result.size)
        assertEquals("case_001", result[0].id)
    }

    @Test
    fun `searchCases matches tag`() = runTest {
        val repo = makeRepo()
        val cases = repo.getCases()
        val result = repo.searchCases("litiasis", cases)
        assertEquals(1, result.size)
        assertEquals("case_003", result[0].id)
    }

    @Test
    fun `searchCases returns empty when no match`() = runTest {
        val repo = makeRepo()
        val cases = repo.getCases()
        val result = repo.searchCases("xyzzzzzz", cases)
        assertTrue(result.isEmpty())
    }

    // --- Caching ---

    @Test
    fun `getCases caches result — asset opened only once`() = runTest {
        val assetReader = mockk<AssetReader>()
        var openCount = 0
        every { assetReader.open("clinical_cases.json") } answers {
            openCount++
            ByteArrayInputStream(validCasesJson.toByteArray())
        }
        val repo = ClinicalCasesRepositoryImpl(assetReader)

        repo.getCases()
        repo.getCases()
        repo.getCaseById("case_001")

        assertEquals("Asset file should only be opened once due to cache", 1, openCount)
    }
}
