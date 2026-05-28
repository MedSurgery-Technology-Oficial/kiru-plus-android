package com.medsurgery.kiruplus.feature.drugs

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.domain.drugs.Drug
import com.medsurgery.kiruplus.domain.drugs.DrugsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrugsViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private fun makeDrug(
        id: String,
        nameEs: String,
        category: String,
        tags: List<String> = emptyList(),
    ) = Drug(
        id = id,
        nameEs = nameEs,
        genericName = "generic_$id",
        category = category,
        indication = "indication",
        mechanism = "mechanism",
        dose = "dose",
        route = "route",
        contraindication = "none",
        adverseEffects = "none",
        notes = "",
        tags = tags,
    )

    private val sampleDrugs = listOf(
        makeDrug("d1", "Morfina", "Analgésicos / Opioides", listOf("opioide", "dolor")),
        makeDrug("d2", "Cefalexina", "Antibióticos Quirúrgicos", listOf("antibiotico")),
        makeDrug("d3", "Propofol", "Anestésicos / Sedación", listOf("anestesia")),
    )

    private fun makeRepo(
        drugs: List<Drug> = sampleDrugs,
        searchResult: (String, List<Drug>) -> List<Drug> = { query, list ->
            if (query.isBlank()) list
            else list.filter { it.nameEs.lowercase().contains(query.trim().lowercase()) }
        },
    ): DrugsRepository {
        val repo = mockk<DrugsRepository>()
        coEvery { repo.getDrugs() } returns drugs
        coEvery { repo.getDrugById(any()) } answers {
            drugs.firstOrNull { it.id == firstArg() }
        }
        coEvery { repo.getDrugsByCategory(any()) } answers {
            drugs.filter { it.category == firstArg<String>() }
        }
        every { repo.searchDrugs(any(), any()) } answers {
            searchResult(firstArg(), secondArg())
        }
        return repo
    }

    @Test
    fun `initial state has loading true before data arrives`() {
        // The state starts with isLoading = true in the data class default
        assertEquals(true, DrugsUiState().isLoading)
    }

    @Test
    fun `initial state has empty search and no category filter`() = runTest {
        val vm = DrugsViewModel(makeRepo())
        assertEquals("", vm.state.value.searchQuery)
        assertNull(vm.state.value.selectedCategory)
    }

    @Test
    fun `after init load — drugs and filteredDrugs are populated`() = runTest {
        val vm = DrugsViewModel(makeRepo())
        assertFalse(vm.state.value.isLoading)
        assertEquals(3, vm.state.value.drugs.size)
        assertEquals(3, vm.state.value.filteredDrugs.size)
    }

    @Test
    fun `onSearch updates searchQuery`() = runTest {
        val vm = DrugsViewModel(makeRepo())
        vm.onSearch("morfina")
        assertEquals("morfina", vm.state.value.searchQuery)
    }

    @Test
    fun `onSearch filters filteredDrugs by name`() = runTest {
        val vm = DrugsViewModel(makeRepo())
        vm.onSearch("propofol")
        assertEquals(1, vm.state.value.filteredDrugs.size)
        assertEquals("d3", vm.state.value.filteredDrugs[0].id)
    }

    @Test
    fun `onSearch with empty string returns all drugs`() = runTest {
        val vm = DrugsViewModel(makeRepo())
        vm.onSearch("morfina")
        vm.onSearch("")
        assertEquals(3, vm.state.value.filteredDrugs.size)
    }

    @Test
    fun `onCategoryFilter filters by category`() = runTest {
        val repo = makeRepo(
            searchResult = { query, list ->
                if (query.isBlank()) list
                else list.filter { it.nameEs.lowercase().contains(query.lowercase()) }
            }
        )
        val vm = DrugsViewModel(repo)
        vm.onCategoryFilter("Antibióticos Quirúrgicos")
        assertEquals("Antibióticos Quirúrgicos", vm.state.value.selectedCategory)
        assertEquals(1, vm.state.value.filteredDrugs.size)
        assertEquals("d2", vm.state.value.filteredDrugs[0].id)
    }

    @Test
    fun `onCategoryFilter with null clears filter`() = runTest {
        val vm = DrugsViewModel(makeRepo())
        vm.onCategoryFilter("Analgésicos / Opioides")
        vm.onCategoryFilter(null)
        assertNull(vm.state.value.selectedCategory)
        assertEquals(3, vm.state.value.filteredDrugs.size)
    }

    @Test
    fun `onSearch with active category applies both filters`() = runTest {
        // category="Analgésicos / Opioides" → only d1 ("Morfina"); search "morfina" → still d1
        val vm = DrugsViewModel(makeRepo())
        vm.onCategoryFilter("Analgésicos / Opioides")
        vm.onSearch("morfina")
        assertEquals(1, vm.state.value.filteredDrugs.size)
        assertEquals("d1", vm.state.value.filteredDrugs[0].id)
    }

    @Test
    fun `onSearch with active category excluding all returns empty`() = runTest {
        // category="Analgésicos / Opioides" → only d1; search "propofol" → no match
        val vm = DrugsViewModel(makeRepo())
        vm.onCategoryFilter("Analgésicos / Opioides")
        vm.onSearch("propofol")
        assertTrue(vm.state.value.filteredDrugs.isEmpty())
    }

    @Test
    fun `load() reloads drugs from repository`() = runTest {
        val repo = makeRepo()
        val vm = DrugsViewModel(repo)
        assertEquals(3, vm.state.value.drugs.size)
        vm.load()
        assertEquals(3, vm.state.value.drugs.size)
    }
}
