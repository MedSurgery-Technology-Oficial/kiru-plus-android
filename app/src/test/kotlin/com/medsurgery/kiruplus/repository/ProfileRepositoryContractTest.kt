package com.medsurgery.kiruplus.repository

import com.medsurgery.kiruplus.domain.profile.Profile
import com.medsurgery.kiruplus.domain.profile.ProfileRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileRepositoryContractTest {

    private val sampleProfile = Profile(
        id = "user-uuid-001",
        fullName = "Dr. Adrián Huerta",
        specialty = "General Surgery",
        role = "attending",
    )

    @Test
    fun `fetchCurrentProfile returns profile on success`() = runTest {
        val repo = FakeProfileRepository(profile = sampleProfile)
        val result = repo.fetchCurrentProfile()
        assertTrue(result.isSuccess)
        assertEquals("Dr. Adrián Huerta", result.getOrThrow()?.fullName)
        assertEquals("attending", result.getOrThrow()?.role)
    }

    @Test
    fun `fetchCurrentProfile returns null when no profile exists`() = runTest {
        val repo = FakeProfileRepository(profile = null)
        val result = repo.fetchCurrentProfile()
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun `fetchCurrentProfile returns failure on network error`() = runTest {
        val repo = FakeProfileRepository(shouldFail = true)
        val result = repo.fetchCurrentProfile()
        assertTrue(result.isFailure)
    }

    @Test
    fun `profile specialty defaults to General Surgery`() {
        val profile = Profile(
            id = "u1",
            fullName = "Dr. Test",
            role = "resident",
        )
        assertEquals("General Surgery", profile.specialty)
    }
}

private class FakeProfileRepository(
    private val profile: Profile? = null,
    private val shouldFail: Boolean = false,
) : ProfileRepository {

    override suspend fun fetchCurrentProfile(): Result<Profile?> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(profile)
}
