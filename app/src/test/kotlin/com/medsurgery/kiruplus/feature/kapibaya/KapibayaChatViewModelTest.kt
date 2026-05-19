package com.medsurgery.kiruplus.feature.kapibaya

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.kapibaya.KapibayaRepository
import com.medsurgery.kiruplus.domain.kapibaya.KapibayaTurn
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class KapibayaChatViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `send empty input is no-op`() = runTest {
        val repo: KapibayaRepository = mockk()
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.send()

        assertTrue(viewModel.state.value.turns.isEmpty())
        coVerify(exactly = 0) { repo.sendMessage(any(), any()) }
    }

    @Test
    fun `send success appends user turn + assistant turn`() = runTest {
        val repo: KapibayaRepository = mockk()
        coEvery { repo.sendMessage(any(), any()) } returns Result.success("Hello, ask me about surgery.")
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("Hi")
        viewModel.send()

        val turns = viewModel.state.value.turns
        assertEquals(2, turns.size)
        assertEquals(KapibayaTurn.Role.USER, turns[0].role)
        assertEquals("Hi", turns[0].content)
        assertEquals(KapibayaTurn.Role.ASSISTANT, turns[1].role)
        assertEquals("Hello, ask me about surgery.", turns[1].content)
        assertEquals("", viewModel.state.value.input)
        assertFalse(viewModel.state.value.isSending)
    }

    @Test
    fun `send failure flips errorRes and keeps user turn`() = runTest {
        val repo: KapibayaRepository = mockk()
        coEvery { repo.sendMessage(any(), any()) } returns Result.failure(RuntimeException("boom"))
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("Ping")
        viewModel.send()

        val turns = viewModel.state.value.turns
        // The user turn stays so user knows what failed; no assistant turn.
        assertEquals(1, turns.size)
        assertEquals(KapibayaTurn.Role.USER, turns[0].role)
        assertEquals(R.string.kapibaya_error_send, viewModel.state.value.errorRes)
        assertFalse(viewModel.state.value.isSending)
    }

    @Test
    fun `trim whitespace in input before sending`() = runTest {
        val repo: KapibayaRepository = mockk()
        coEvery { repo.sendMessage(any(), any()) } returns Result.success("ok")
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("   spaced   ")
        viewModel.send()

        coVerify(exactly = 1) { repo.sendMessage(any(), "spaced") }
    }

    @Test
    fun `onInputChange clears errorRes`() = runTest {
        val repo: KapibayaRepository = mockk()
        coEvery { repo.sendMessage(any(), any()) } returns Result.failure(RuntimeException("boom"))
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("x")
        viewModel.send()
        assertEquals(R.string.kapibaya_error_send, viewModel.state.value.errorRes)

        viewModel.onInputChange("new")
        assertNull(viewModel.state.value.errorRes)
    }
}
