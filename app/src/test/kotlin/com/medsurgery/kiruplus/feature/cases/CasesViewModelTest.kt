package com.medsurgery.kiruplus.feature.cases

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.domain.cases.ClinicalCase
import com.medsurgery.kiruplus.domain.cases.ClinicalCasesRepository
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

class CasesViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private fun makeCase(
        id: String,
        title: String,
        category: String,
        difficulty: String,
        diagnosis: String = "diagnosis_$id",
        tags: List<String> = emptyList(),
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
        diagnosis = diagnosis,
        differentialDiagnosis = emptyList(),
        management = "management",
        teaching = "teaching",
        tags = tags,
    )

    private val sampleCases = listOf(
        makeCase("c1", "Abdomen agudo", "Cirugía de Urgencia", "Avanzado", tags = listOf("urgencia")),
        makeCase("c2", "Politrauma ATLS", "Trauma / ATLS", "Intermedio", tags = listOf("trauma")),
        makeCase("c3", "Colecistitis aguda", "Hepatobiliar", "Básico", diagnosis = "Colecistitis aguda litiásica"),
    )

    private fun makeRepo(
        cases: List<ClinicalCase> = sampleCases,
        searchResult: (String, List<ClinicalCase>) -> List<ClinicalCase> = { query, list ->
            if (query.isBlank()) list
            else list.filter { it.title.lowercase().contains(query.trim().lowercase()) }
        },
    ): ClinicalCasesRepository {
        val repo = mockk<ClinicalCasesRepository>()
        coEvery { repo.getCases() } returns cases
        coEvery { repo.getCaseById(any()) } answers {
            cases.firstOrNull { it.id == firstArg() }
        }
        coEvery { repo.getCasesByCategory(any()) } answers {
            cases.filter { it.category == firstArg<String>() }
        }
        every { repo.searchCases(any(), any()) } answers {
            searchResult(firstArg(), secondArg())
        }
        return repo
    }

    @Test
    fun `initial state has loading true before data arrives`() {
        assertEquals(true, CasesUiState().isLoading)
    }

    @Test
    fun `initial state has empty search and no filters`() = runTest {
        val vm = CasesViewModel(makeRepo())
        assertEquals("", vm.state.value.searchQuery)
        assertNull(vm.state.value.selectedCategory)
        assertNull(vm.state.value.selectedDifficulty)
    }

    @Test
    fun `after init load — cases and filteredCases are populated`() = runTest {
        val vm = CasesViewModel(makeRepo())
        assertFalse(vm.state.value.isLoading)
        assertEquals(3, vm.state.value.cases.size)
        assertEquals(3, vm.state.value.filteredCases.size)
    }

    @Test
    fun `onSearch updates searchQuery`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onSearch("abdomen")
        assertEquals("abdomen", vm.state.value.searchQuery)
    }

    @Test
    fun `onSearch filters filteredCases by title`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onSearch("politrauma")
        assertEquals(1, vm.state.value.filteredCases.size)
        assertEquals("c2", vm.state.value.filteredCases[0].id)
    }

    @Test
    fun `onSearch with empty string returns all cases`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onSearch("abdomen")
        vm.onSearch("")
        assertEquals(3, vm.state.value.filteredCases.size)
    }

    @Test
    fun `onCategoryFilter filters by category`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onCategoryFilter("Hepatobiliar")
        assertEquals("Hepatobiliar", vm.state.value.selectedCategory)
        assertEquals(1, vm.state.value.filteredCases.size)
        assertEquals("c3", vm.state.value.filteredCases[0].id)
    }

    @Test
    fun `onCategoryFilter with null clears filter`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onCategoryFilter("Trauma / ATLS")
        vm.onCategoryFilter(null)
        assertNull(vm.state.value.selectedCategory)
        assertEquals(3, vm.state.value.filteredCases.size)
    }

    @Test
    fun `onDifficultyFilter filters by difficulty`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onDifficultyFilter("Básico")
        assertEquals("Básico", vm.state.value.selectedDifficulty)
        assertEquals(1, vm.state.value.filteredCases.size)
        assertEquals("c3", vm.state.value.filteredCases[0].id)
    }

    @Test
    fun `onDifficultyFilter with null clears difficulty filter`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onDifficultyFilter("Avanzado")
        vm.onDifficultyFilter(null)
        assertNull(vm.state.value.selectedDifficulty)
        assertEquals(3, vm.state.value.filteredCases.size)
    }

    @Test
    fun `combined category and difficulty filters work together`() = runTest {
        val allCases = listOf(
            makeCase("c1", "Abdomen agudo", "Cirugía de Urgencia", "Avanzado"),
            makeCase("c2", "Politrauma", "Trauma / ATLS", "Intermedio"),
            makeCase("c4", "Hernia avanzada", "Cirugía General", "Avanzado"),
        )
        val vm = CasesViewModel(makeRepo(cases = allCases))
        vm.onCategoryFilter("Cirugía General")
        vm.onDifficultyFilter("Avanzado")
        // After both filters: only c4 matches Cirugía General + Avanzado
        assertEquals(1, vm.state.value.filteredCases.size)
        assertEquals("c4", vm.state.value.filteredCases[0].id)
    }

    @Test
    fun `combined search and category both active filters correctly`() = runTest {
        val vm = CasesViewModel(makeRepo())
        vm.onCategoryFilter("Cirugía de Urgencia")
        vm.onSearch("abdomen")
        // "Cirugía de Urgencia" → only c1; search "abdomen" matches c1 title
        assertEquals(1, vm.state.value.filteredCases.size)
        assertEquals("c1", vm.state.value.filteredCases[0].id)
    }

    @Test
    fun `load() reloads cases from repository`() = runTest {
        val repo = makeRepo()
        val vm = CasesViewModel(repo)
        assertEquals(3, vm.state.value.cases.size)
        vm.load()
        assertEquals(3, vm.state.value.cases.size)
    }
}
