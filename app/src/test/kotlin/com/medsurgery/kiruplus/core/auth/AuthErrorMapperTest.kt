package com.medsurgery.kiruplus.core.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AuthErrorMapperTest {

    @Test
    fun `maps Invalid login credentials (case-insensitive)`() {
        val mapped = RuntimeException("Invalid Login Credentials").toAuthError()
        assertEquals(AuthError.InvalidCredentials, mapped)
    }

    @Test
    fun `maps invalid_credentials snake case`() {
        val mapped = RuntimeException("invalid_credentials").toAuthError()
        assertEquals(AuthError.InvalidCredentials, mapped)
    }

    @Test
    fun `maps user_already_exists to EmailAlreadyInUse`() {
        val mapped = RuntimeException("user_already_exists").toAuthError()
        assertEquals(AuthError.EmailAlreadyInUse, mapped)
    }

    @Test
    fun `maps Email not confirmed`() {
        val mapped = RuntimeException("Email not confirmed").toAuthError()
        assertEquals(AuthError.EmailNotConfirmed, mapped)
    }

    @Test
    fun `maps over_request_rate_limit to RateLimited`() {
        val mapped = RuntimeException("over_request_rate_limit").toAuthError()
        assertEquals(AuthError.RateLimited, mapped)
    }

    @Test
    fun `maps Password should be (weak_password) to WeakPassword`() {
        val mapped = RuntimeException("Password should be at least 8…").toAuthError()
        assertEquals(AuthError.WeakPassword, mapped)
    }

    @Test
    fun `maps email_address_invalid to InvalidEmail`() {
        val mapped = RuntimeException("email_address_invalid").toAuthError()
        assertEquals(AuthError.InvalidEmail, mapped)
    }

    @Test
    fun `maps Unable to resolve host to Network`() {
        val mapped = RuntimeException("Unable to resolve host: api.example.com").toAuthError()
        assertEquals(AuthError.Network, mapped)
    }

    @Test
    fun `maps unknown message to Unknown`() {
        val original = RuntimeException("Something obscure")
        val mapped = original.toAuthError()
        assertTrue(mapped is AuthError.Unknown)
        assertEquals(original, (mapped as AuthError.Unknown).original)
    }

    @Test
    fun `passes AuthError instances through unchanged`() {
        assertEquals(AuthError.RateLimited, AuthError.RateLimited.toAuthError())
    }

    @Test
    fun `treats IOException with empty message as Unknown (not Network) when not a network IOException`() {
        // toAuthError sólo dispara Network si message contiene un keyword conocido
        // o si la excepción es HttpRequestException de Ktor.
        val mapped = IOException("").toAuthError()
        assertTrue(mapped is AuthError.Unknown)
    }
}
