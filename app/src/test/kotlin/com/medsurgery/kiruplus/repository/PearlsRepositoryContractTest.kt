package com.medsurgery.kiruplus.repository

import com.medsurgery.kiruplus.domain.pearls.Pearl
import com.medsurgery.kiruplus.domain.pearls.PearlsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests for PearlsRepository using an in-memory fake.
 *
 * PearlsRepositoryImpl depends on SupabaseClient (extension-function DSL), which
 * cannot be meaningfully unit-tested without a live Supabase instance. These tests
 * instead verify the behavioral contract that any conforming implementation must
 * satisfy. The fake is also reusable as a test double in PearlsViewModel tests.
 */
class PearlsRepositoryContractTest {

    private val sample = listOf(
        Pearl(id = 1, title = "Abdomen abierto", description = "Indicación precisa.", category = "cuidados_criticos"),
        Pearl(id = 2, title = "Hipertensión intraabdominal", description = "Medir presión vesical.", category = "cuidados_criticos"),
    )

    // --- fetchAllPearls ---

    @Test
    fun `fetchAllPearls returns all pearls on success`() = runTest {
        val repo = FakePearlsRepository(pearls = sample)
        val result = repo.fetchAllPearls()
        assertTrue(result.isSuccess)
        assertEquals(sample, result.getOrThrow())
    }

    @Test
    fun `fetchAllPearls returns empty list when no pearls`() = runTest {
        val repo = FakePearlsRepository(pearls = emptyList())
        val result = repo.fetchAllPearls()
        assertTrue(result.isSuccess)
        assertEquals(emptyList<Pearl>(), result.getOrThrow())
    }

    @Test
    fun `fetchAllPearls returns failure on network error`() = runTest {
        val repo = FakePearlsRepository(shouldFail = true)
        val result = repo.fetchAllPearls()
        assertTrue(result.isFailure)
    }

    // --- fetchPearl ---

    @Test
    fun `fetchPearl returns correct pearl by id`() = runTest {
        val repo = FakePearlsRepository(pearls = sample)
        val result = repo.fetchPearl(id = 2)
        assertTrue(result.isSuccess)
        assertEquals("Hipertensión intraabdominal", result.getOrThrow()?.title)
    }

    @Test
    fun `fetchPearl returns null for unknown id`() = runTest {
        val repo = FakePearlsRepository(pearls = sample)
        val result = repo.fetchPearl(id = 999)
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun `fetchPearl returns failure on network error`() = runTest {
        val repo = FakePearlsRepository(shouldFail = true)
        val result = repo.fetchPearl(id = 1)
        assertTrue(result.isFailure)
    }
}

private class FakePearlsRepository(
    private val pearls: List<Pearl> = emptyList(),
    private val shouldFail: Boolean = false,
) : PearlsRepository {

    override suspend fun fetchAllPearls(): Result<List<Pearl>> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(pearls)

    override suspend fun fetchPearl(id: Int): Result<Pearl?> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(pearls.firstOrNull { it.id == id })
}
