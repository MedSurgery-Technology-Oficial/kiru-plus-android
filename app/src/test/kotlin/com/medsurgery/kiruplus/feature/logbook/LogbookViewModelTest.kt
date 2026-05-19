package com.medsurgery.kiruplus.feature.logbook

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.logbook.LogbookRepository
import com.medsurgery.kiruplus.domain.logbook.Procedure
import com.medsurgery.kiruplus.domain.logbook.SurgicalLog
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LogbookViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val procedure = Procedure(
        id = "5283f65d-6178-4835-bd25-86a945af3283",
        nameEs = "Apendicectomía Laparoscópica",
        nameEn = "Laparoscopic Appendectomy",
        category = "Emergency surgery",
    )

    private val log = SurgicalLog(
        id = "log-1",
        userId = "user-1",
        procedureId = procedure.id,
        procedureDate = "2026-05-18",
        complexity = "Low",
        outcome = "Routine",
        notes = "Sin complicaciones.",
    )

    @Test
    fun `init load success populates logs + procedure lookup`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchLogs() } returns Result.success(listOf(log))
        coEvery { repo.fetchProcedures() } returns Result.success(listOf(procedure))

        val viewModel = LogbookViewModel(repo)

        assertEquals(listOf(log), viewModel.state.value.logs)
        assertEquals(procedure, viewModel.state.value.procedureLookup[procedure.id])
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorRes)
    }

    @Test
    fun `init load failure of logs surfaces errorRes (procedures empty)`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchLogs() } returns Result.failure(RuntimeException("boom"))
        coEvery { repo.fetchProcedures() } returns Result.success(emptyList())

        val viewModel = LogbookViewModel(repo)

        assertTrue(viewModel.state.value.logs.isEmpty())
        assertEquals(R.string.logbook_error_load, viewModel.state.value.errorRes)
    }

    @Test
    fun `procedures failure does NOT fail the screen — lookup just stays empty`() = runTest {
        val repo: LogbookRepository = mockk()
        coEvery { repo.fetchLogs() } returns Result.success(listOf(log))
        coEvery { repo.fetchProcedures() } returns Result.failure(RuntimeException("boom"))

        val viewModel = LogbookViewModel(repo)

        assertEquals(listOf(log), viewModel.state.value.logs)
        assertTrue(viewModel.state.value.procedureLookup.isEmpty())
        // No error surfaced — the list still renders with "Unknown procedure" labels
        assertNull(viewModel.state.value.errorRes)
    }
}
