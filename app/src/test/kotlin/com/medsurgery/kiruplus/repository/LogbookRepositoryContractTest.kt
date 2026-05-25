package com.medsurgery.kiruplus.repository

import com.medsurgery.kiruplus.domain.logbook.LogbookRepository
import com.medsurgery.kiruplus.domain.logbook.NewLogInput
import com.medsurgery.kiruplus.domain.logbook.Procedure
import com.medsurgery.kiruplus.domain.logbook.SurgicalLog
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogbookRepositoryContractTest {

    private val sampleLog = SurgicalLog(
        id = "log-001",
        userId = "user-abc",
        procedureId = "proc-laparotomy",
        procedureDate = "2026-05-20",
        complexity = "High",
        outcome = "Successful",
        notes = "No complications",
    )

    private val sampleProcedure = Procedure(
        id = "proc-laparotomy",
        nameEs = "Laparotomía exploradora",
        nameEn = "Exploratory laparotomy",
        category = "Emergency surgery",
    )

    private val newLog = NewLogInput(
        procedureId = "proc-laparotomy",
        procedureDate = LocalDate(2026, 5, 20),
        complexity = "High",
        outcome = "Successful",
        notes = null,
    )

    // --- fetchLogs ---

    @Test
    fun `fetchLogs returns surgical logs on success`() = runTest {
        val repo = FakeLogbookRepository(logs = listOf(sampleLog))
        val result = repo.fetchLogs()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("log-001", result.getOrThrow()[0].id)
    }

    @Test
    fun `fetchLogs returns empty list when no logs`() = runTest {
        val repo = FakeLogbookRepository()
        val result = repo.fetchLogs()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `fetchLogs returns failure on error`() = runTest {
        val repo = FakeLogbookRepository(shouldFail = true)
        val result = repo.fetchLogs()
        assertTrue(result.isFailure)
    }

    // --- fetchProcedures ---

    @Test
    fun `fetchProcedures returns catalog on success`() = runTest {
        val repo = FakeLogbookRepository(procedures = listOf(sampleProcedure))
        val result = repo.fetchProcedures()
        assertTrue(result.isSuccess)
        assertEquals("Laparotomía exploradora", result.getOrThrow()[0].nameEs)
    }

    @Test
    fun `fetchProcedures returns failure on error`() = runTest {
        val repo = FakeLogbookRepository(shouldFail = true)
        val result = repo.fetchProcedures()
        assertTrue(result.isFailure)
    }

    // --- createLog ---

    @Test
    fun `createLog returns success and persists entry`() = runTest {
        val repo = FakeLogbookRepository()
        val createResult = repo.createLog(newLog)
        assertTrue(createResult.isSuccess)
        assertEquals(1, repo.fetchLogs().getOrThrow().size)
        assertEquals("proc-laparotomy", repo.fetchLogs().getOrThrow()[0].procedureId)
    }

    @Test
    fun `createLog returns failure on error`() = runTest {
        val repo = FakeLogbookRepository(shouldFail = true)
        val result = repo.createLog(newLog)
        assertTrue(result.isFailure)
    }

    // --- deleteLog ---

    @Test
    fun `deleteLog removes the entry`() = runTest {
        val repo = FakeLogbookRepository(logs = mutableListOf(sampleLog))
        assertTrue(repo.deleteLog("log-001").isSuccess)
        assertTrue(repo.fetchLogs().getOrThrow().isEmpty())
    }

    @Test
    fun `deleteLog on unknown id succeeds without crashing`() = runTest {
        val repo = FakeLogbookRepository(logs = mutableListOf(sampleLog))
        assertTrue(repo.deleteLog("does-not-exist").isSuccess)
        assertEquals(1, repo.fetchLogs().getOrThrow().size)
    }

    @Test
    fun `deleteLog returns failure on error`() = runTest {
        val repo = FakeLogbookRepository(shouldFail = true)
        assertTrue(repo.deleteLog("log-001").isFailure)
    }
}

private class FakeLogbookRepository(
    logs: List<SurgicalLog> = emptyList(),
    private val procedures: List<Procedure> = emptyList(),
    private val shouldFail: Boolean = false,
) : LogbookRepository {

    private val store: MutableList<SurgicalLog> = logs.toMutableList()
    private var nextId = 1000

    override suspend fun fetchLogs(): Result<List<SurgicalLog>> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(store.toList())

    override suspend fun fetchProcedures(): Result<List<Procedure>> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(procedures)

    override suspend fun createLog(input: NewLogInput): Result<Unit> {
        if (shouldFail) return Result.failure(RuntimeException("network error"))
        store.add(
            SurgicalLog(
                id = "fake-${nextId++}",
                userId = "fake-user",
                procedureId = input.procedureId,
                procedureDate = input.procedureDate.toString(),
                complexity = input.complexity,
                outcome = input.outcome,
                notes = input.notes,
            ),
        )
        return Result.success(Unit)
    }

    override suspend fun deleteLog(id: String): Result<Unit> {
        if (shouldFail) return Result.failure(RuntimeException("network error"))
        store.removeAll { it.id == id }
        return Result.success(Unit)
    }
}
