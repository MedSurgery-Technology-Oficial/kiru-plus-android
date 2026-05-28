package com.medsurgery.kiruplus.data.drugs

import com.medsurgery.kiruplus.core.assets.AssetReader
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class DrugsRepositoryImplTest {

    private val validDrugsJson = """
        [
          {
            "id": "drug_001",
            "nameEs": "Morfina",
            "genericName": "Morphine sulfate",
            "category": "Analgésicos / Opioides",
            "indication": "Dolor agudo severo postoperatorio",
            "mechanism": "Agonista receptor mu opioide",
            "dose": "0.1 mg/kg IV",
            "route": "IV / SC",
            "contraindication": "Depresión respiratoria severa",
            "adverseEffects": "Náusea, constipación, sedación",
            "notes": "Tener naloxona disponible",
            "tags": ["opioide", "dolor", "postoperatorio"]
          },
          {
            "id": "drug_002",
            "nameEs": "Cefalexina",
            "genericName": "Cephalexin",
            "category": "Antibióticos Quirúrgicos",
            "indication": "Profilaxis quirúrgica piel/tejidos blandos",
            "mechanism": "Inhibición síntesis pared bacteriana",
            "dose": "1g IV 30 min antes de incisión",
            "route": "IV",
            "contraindication": "Alergia a cefalosporinas",
            "adverseEffects": "Rash, diarrea",
            "notes": "Primera generación cefalosporina",
            "tags": ["antibiotico", "profilaxis"]
          },
          {
            "id": "drug_003",
            "nameEs": "Propofol",
            "genericName": "Propofol",
            "category": "Anestésicos / Sedación",
            "indication": "Inducción y mantenimiento anestesia",
            "mechanism": "Potenciación GABA-A",
            "dose": "1.5-2.5 mg/kg IV",
            "route": "IV",
            "contraindication": "Alergia al huevo o soya",
            "adverseEffects": "Hipotensión, dolor de inyección",
            "notes": "PRIS síndrome en infusiones prolongadas",
            "tags": ["anestesia", "sedacion", "induccion"]
          }
        ]
    """.trimIndent()

    private fun makeRepo(drugsJson: String? = validDrugsJson): DrugsRepositoryImpl {
        val assetReader = mockk<AssetReader>()
        every { assetReader.open("drugs.json") } returns
            drugsJson?.let { ByteArrayInputStream(it.toByteArray()) }
        return DrugsRepositoryImpl(assetReader)
    }

    // --- getDrugs ---

    @Test
    fun `getDrugs returns non-empty list on valid JSON`() = runTest {
        val repo = makeRepo()
        val result = repo.getDrugs()
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `getDrugs returns all 3 drugs from sample JSON`() = runTest {
        val repo = makeRepo()
        val result = repo.getDrugs()
        assertEquals(3, result.size)
    }

    @Test
    fun `getDrugs maps drug fields correctly`() = runTest {
        val repo = makeRepo()
        val drug = repo.getDrugs().first()
        assertEquals("drug_001", drug.id)
        assertEquals("Morfina", drug.nameEs)
        assertEquals("Morphine sulfate", drug.genericName)
        assertEquals("Analgésicos / Opioides", drug.category)
        assertEquals(3, drug.tags.size)
    }

    @Test
    fun `getDrugs returns empty list when asset missing`() = runTest {
        val repo = makeRepo(drugsJson = null)
        val result = repo.getDrugs()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getDrugs returns empty list on malformed JSON`() = runTest {
        val repo = makeRepo(drugsJson = "{ not valid json }")
        val result = repo.getDrugs()
        assertTrue(result.isEmpty())
    }

    // --- getDrugById ---

    @Test
    fun `getDrugById returns correct drug for valid id`() = runTest {
        val repo = makeRepo()
        val drug = repo.getDrugById("drug_002")
        assertEquals("Cefalexina", drug?.nameEs)
        assertEquals("Antibióticos Quirúrgicos", drug?.category)
    }

    @Test
    fun `getDrugById returns null for unknown id`() = runTest {
        val repo = makeRepo()
        val drug = repo.getDrugById("drug_999")
        assertNull(drug)
    }

    // --- getDrugsByCategory ---

    @Test
    fun `getDrugsByCategory returns only drugs matching that category`() = runTest {
        val repo = makeRepo()
        val result = repo.getDrugsByCategory("Analgésicos / Opioides")
        assertEquals(1, result.size)
        assertEquals("drug_001", result[0].id)
    }

    @Test
    fun `getDrugsByCategory returns empty list for non-existent category`() = runTest {
        val repo = makeRepo()
        val result = repo.getDrugsByCategory("Categoría Inexistente")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getDrugsByCategory returns multiple drugs when category matches several`() = runTest {
        // All 3 drugs have different categories, so any valid category returns exactly 1
        val repo = makeRepo()
        val result = repo.getDrugsByCategory("Anestésicos / Sedación")
        assertEquals(1, result.size)
        assertEquals("Propofol", result[0].nameEs)
    }

    // --- searchDrugs ---

    @Test
    fun `searchDrugs returns all when query is blank`() = runTest {
        val repo = makeRepo()
        val drugs = repo.getDrugs()
        val result = repo.searchDrugs("", drugs)
        assertEquals(drugs, result)
    }

    @Test
    fun `searchDrugs returns all when query is only whitespace`() = runTest {
        val repo = makeRepo()
        val drugs = repo.getDrugs()
        val result = repo.searchDrugs("   ", drugs)
        assertEquals(drugs, result)
    }

    @Test
    fun `searchDrugs filters by nameEs case-insensitive`() = runTest {
        val repo = makeRepo()
        val drugs = repo.getDrugs()
        val result = repo.searchDrugs("morfina", drugs)
        assertEquals(1, result.size)
        assertEquals("drug_001", result[0].id)
    }

    @Test
    fun `searchDrugs filters by nameEs uppercase`() = runTest {
        val repo = makeRepo()
        val drugs = repo.getDrugs()
        val result = repo.searchDrugs("PROPOFOL", drugs)
        assertEquals(1, result.size)
        assertEquals("drug_003", result[0].id)
    }

    @Test
    fun `searchDrugs filters by tag`() = runTest {
        val repo = makeRepo()
        val drugs = repo.getDrugs()
        val result = repo.searchDrugs("profilaxis", drugs)
        assertEquals(1, result.size)
        assertEquals("drug_002", result[0].id)
    }

    @Test
    fun `searchDrugs returns empty list when no match`() = runTest {
        val repo = makeRepo()
        val drugs = repo.getDrugs()
        val result = repo.searchDrugs("zzznomatch", drugs)
        assertTrue(result.isEmpty())
    }

    // --- Caching ---

    @Test
    fun `getDrugs caches result — asset opened only once`() = runTest {
        val assetReader = mockk<AssetReader>()
        var openCount = 0
        every { assetReader.open("drugs.json") } answers {
            openCount++
            ByteArrayInputStream(validDrugsJson.toByteArray())
        }
        val repo = DrugsRepositoryImpl(assetReader)

        repo.getDrugs()
        repo.getDrugs()
        repo.getDrugById("drug_001")

        assertEquals("Asset file should only be opened once due to cache", 1, openCount)
    }
}
