package com.medsurgery.kiruplus.domain.drugs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrugDomainTest {

    // ── Drug.categories ───────────────────────────────────────────────────────

    @Test
    fun `Drug categories list contains exactly 7 categories`() {
        assertEquals(7, Drug.categories.size)
    }

    @Test
    fun `Drug categories contains Analgesicos Opioides`() {
        assertTrue(Drug.categories.contains("Analgésicos / Opioides"))
    }

    @Test
    fun `Drug categories contains Antibioticos Quirurgicos`() {
        assertTrue(Drug.categories.contains("Antibióticos Quirúrgicos"))
    }

    @Test
    fun `Drug categories contains Anestesicos Sedacion`() {
        assertTrue(Drug.categories.contains("Anestésicos / Sedación"))
    }

    @Test
    fun `Drug categories contains Vasopresores UCI`() {
        assertTrue(Drug.categories.contains("Vasopresores / UCI"))
    }

    @Test
    fun `Drug categories contains Gastrointestinal`() {
        assertTrue(Drug.categories.contains("Gastrointestinal"))
    }

    @Test
    fun `Drug categories contains Anticoagulacion`() {
        assertTrue(Drug.categories.contains("Anticoagulación"))
    }

    @Test
    fun `Drug categories contains Fluidos Electrolitos`() {
        assertTrue(Drug.categories.contains("Fluidos / Electrolitos"))
    }

    @Test
    fun `all Drug categories are non-blank strings`() {
        Drug.categories.forEach { category ->
            assertTrue("Category must not be blank", category.isNotBlank())
        }
    }

    // ── Drug data class ───────────────────────────────────────────────────────

    private fun makeDrug(
        id: String = "d1",
        nameEs: String = "Morfina",
        category: String = "Analgésicos / Opioides",
        tags: List<String> = listOf("opioide"),
    ) = Drug(
        id = id,
        nameEs = nameEs,
        genericName = "Morphine",
        category = category,
        indication = "Dolor severo",
        mechanism = "Agonista mu",
        dose = "0.1 mg/kg",
        route = "IV",
        contraindication = "Depresión respiratoria",
        adverseEffects = "Náusea",
        notes = "",
        tags = tags,
    )

    @Test
    fun `Drug data class equality works correctly`() {
        val d1 = makeDrug()
        val d2 = makeDrug()
        assertEquals(d1, d2)
    }

    @Test
    fun `Drug data class with different id are not equal`() {
        val d1 = makeDrug(id = "d1")
        val d2 = makeDrug(id = "d2")
        assertFalse(d1 == d2)
    }

    @Test
    fun `Drug tags can be empty`() {
        val drug = makeDrug(tags = emptyList())
        assertTrue(drug.tags.isEmpty())
    }

    @Test
    fun `Drug tags preserve order`() {
        val tags = listOf("opioide", "dolor", "postoperatorio")
        val drug = makeDrug(tags = tags)
        assertEquals(tags, drug.tags)
    }
}
