package com.medsurgery.kiruplus.feature.kapibaya

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.kapibaya.KapibayaRepository
import com.medsurgery.kiruplus.domain.kapibaya.KapibayaTurn
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
        verify(exactly = 0) { repo.sendMessageStream(any(), any()) }
    }

    @Test
    fun `send success appends user turn and finalizes assistant turn`() = runTest {
        val repo: KapibayaRepository = mockk()
        every { repo.sendMessageStream(any(), any()) } returns flowOf("Hello,", " ask me.")
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("Hi")
        viewModel.send()

        val turns = viewModel.state.value.turns
        assertEquals(2, turns.size)
        assertEquals(KapibayaTurn.Role.USER, turns[0].role)
        assertEquals("Hi", turns[0].content)
        assertEquals(KapibayaTurn.Role.ASSISTANT, turns[1].role)
        assertEquals("Hello, ask me.", turns[1].content)
        assertEquals("", viewModel.state.value.input)
        assertFalse(viewModel.state.value.isSending)
        assertNull(viewModel.state.value.streamingContent)
    }

    @Test
    fun `send failure flips errorRes and keeps user turn`() = runTest {
        val repo: KapibayaRepository = mockk()
        every { repo.sendMessageStream(any(), any()) } returns flow { throw RuntimeException("boom") }
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("Ping")
        viewModel.send()

        val turns = viewModel.state.value.turns
        assertEquals(1, turns.size)
        assertEquals(KapibayaTurn.Role.USER, turns[0].role)
        assertEquals(R.string.kapibaya_error_send, viewModel.state.value.errorRes)
        assertFalse(viewModel.state.value.isSending)
        assertNull(viewModel.state.value.streamingContent)
    }

    @Test
    fun `trim whitespace in input before sending`() = runTest {
        val repo: KapibayaRepository = mockk()
        every { repo.sendMessageStream(any(), "spaced") } returns flowOf("ok")
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("   spaced   ")
        viewModel.send()

        verify(exactly = 1) { repo.sendMessageStream(any(), "spaced") }
    }

    @Test
    fun `onInputChange clears errorRes`() = runTest {
        val repo: KapibayaRepository = mockk()
        every { repo.sendMessageStream(any(), any()) } returns flow { throw RuntimeException("boom") }
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("x")
        viewModel.send()
        assertEquals(R.string.kapibaya_error_send, viewModel.state.value.errorRes)

        viewModel.onInputChange("new")
        assertNull(viewModel.state.value.errorRes)
    }

    @Test
    fun `streaming concatenates chunks into final assistant turn`() = runTest {
        val repo: KapibayaRepository = mockk()
        every { repo.sendMessageStream(any(), any()) } returns flowOf("part1", " part2", " part3")
        val viewModel = KapibayaChatViewModel(repo)

        viewModel.onInputChange("question")
        viewModel.send()

        assertNull(viewModel.state.value.streamingContent)
        assertEquals("part1 part2 part3", viewModel.state.value.turns.last().content)
    }
}
