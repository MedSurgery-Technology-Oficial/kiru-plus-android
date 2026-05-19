package com.medsurgery.kiruplus.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidatorTest {

    @Test
    fun `accepts standard email`() {
        assertTrue(EmailValidator.isValid("user@example.com"))
    }

    @Test
    fun `accepts subdomain email`() {
        assertTrue(EmailValidator.isValid("dr.huerta@medsurgery.academy"))
    }

    @Test
    fun `accepts plus alias`() {
        assertTrue(EmailValidator.isValid("user+work@example.co.uk"))
    }

    @Test
    fun `trims whitespace before validating`() {
        assertTrue(EmailValidator.isValid("  user@example.com  "))
    }

    @Test
    fun `rejects empty string`() {
        assertFalse(EmailValidator.isValid(""))
    }

    @Test
    fun `rejects missing at sign`() {
        assertFalse(EmailValidator.isValid("user.example.com"))
    }

    @Test
    fun `rejects missing domain`() {
        assertFalse(EmailValidator.isValid("user@"))
    }

    @Test
    fun `rejects missing tld`() {
        assertFalse(EmailValidator.isValid("user@example"))
    }

    @Test
    fun `rejects single-char tld`() {
        assertFalse(EmailValidator.isValid("user@example.c"))
    }

    @Test
    fun `rejects spaces in local part`() {
        assertFalse(EmailValidator.isValid("user name@example.com"))
    }
}
