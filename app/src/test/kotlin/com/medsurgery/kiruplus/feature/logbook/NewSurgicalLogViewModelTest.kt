package com.medsurgery.kiruplus.feature.logbook

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.logbook.LogbookRepository
import com.medsurgery.kiruplus.domain.logbook.NewLogInput
import com.medsurgery.kiruplus.domain.logbook.Procedure
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NewSurgicalLogViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val procedure = Procedure(
        id = "proc-1",
        nameEs = "Apendicectomía",
        nameEn = "Appendectomy",
        category = "Emergency surgery",
    )

    @Test
    fun `init loads procedures`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))

        val vm = NewSurgicalLogViewModel(repo)

        assertEquals(listOf(procedure), vm.state.value.procedures)
        assertFalse(vm.state.value.isLoadingProcedures)
        assertNull(vm.state.value.errorRes)
    }

    @Test
    fun `init procedures failure surfaces error`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.failure(RuntimeException("network"))

        val vm = NewSurgicalLogViewModel(repo)

        assertTrue(vm.state.value.procedures.isEmpty())
        assertEquals(R.string.logbook_error_procedures, vm.state.value.errorRes)
    }

    @Test
    fun `selectProcedure updates state and clears error`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))

        val vm = NewSurgicalLogViewModel(repo)
        vm.selectProcedure(procedure)

        assertEquals(procedure, vm.state.value.selectedProcedure)
        assertNull(vm.state.value.errorRes)
    }

    @Test
    fun `submit without procedure sets error and does not call createLog`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))

        val vm = NewSurgicalLogViewModel(repo)
        vm.submit()

        assertEquals(R.string.logbook_error_pick_procedure, vm.state.value.errorRes)
        coVerify(exactly = 0) { repo.createLog(any()) }
    }

    @Test
    fun `submit success sets saved = true`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))
        coEvery { repo.createLog(any()) } returns Result.success(Unit)

        val vm = NewSurgicalLogViewModel(repo)
        vm.selectProcedure(procedure)
        vm.setNotes("Sin complicaciones")
        vm.submit()

        assertTrue(vm.state.value.saved)
        assertFalse(vm.state.value.isSubmitting)
        assertNull(vm.state.value.errorRes)
    }

    @Test
    fun `submit failure surfaces error and does not set saved`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))
        coEvery { repo.createLog(any()) } returns Result.failure(RuntimeException("network"))

        val vm = NewSurgicalLogViewModel(repo)
        vm.selectProcedure(procedure)
        vm.submit()

        assertFalse(vm.state.value.saved)
        assertEquals(R.string.logbook_error_save, vm.state.value.errorRes)
    }

    @Test
    fun `submit sends correct NewLogInput`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))
        coEvery { repo.createLog(any()) } returns Result.success(Unit)

        val vm = NewSurgicalLogViewModel(repo)
        vm.selectProcedure(procedure)
        val date = LocalDate(2026, 5, 19)
        vm.setProcedureDate(date)
        vm.setComplexity("High")
        vm.setOutcome("Routine")
        vm.setNotes("Sin complicaciones")
        vm.submit()

        coVerify {
            repo.createLog(
                NewLogInput(
                    procedureId = procedure.id,
                    procedureDate = date,
                    complexity = "High",
                    outcome = "Routine",
                    notes = "Sin complicaciones",
                ),
            )
        }
    }

    @Test
    fun `double submit is ignored`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))
        coEvery { repo.createLog(any()) } returns Result.success(Unit)

        val vm = NewSurgicalLogViewModel(repo)
        vm.selectProcedure(procedure)
        vm.submit()
        vm.submit() // second call ignored because saved == true

        coVerify(exactly = 1) { repo.createLog(any()) }
    }
}
