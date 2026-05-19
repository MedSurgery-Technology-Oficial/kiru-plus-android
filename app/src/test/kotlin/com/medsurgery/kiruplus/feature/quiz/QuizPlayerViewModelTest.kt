package com.medsurgery.kiruplus.feature.quiz

import androidx.lifecycle.SavedStateHandle
import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.R
import com.medsurgery.kiruplus.domain.quiz.QuizQuestion
import com.medsurgery.kiruplus.domain.quiz.QuizRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class QuizPlayerViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val specialty = "Cirugía general (General Surgery)"

    private fun savedState() = SavedStateHandle(
        mapOf("specialty" to specialty),
    )

    private fun makeQuestion(id: String, correct: String = "A. Correct") = QuizQuestion(
        id = id,
        questionText = "Question $id",
        options = listOf("A. Correct", "B. Wrong", "C. Wrong", "D. Wrong"),
        correctAnswer = correct,
        explanation = "Because A is right.",
        topic = "Topic $id",
        specialty = specialty,
    )

    @Test
    fun `load success populates questions`() = runTest {
        val repo: QuizRepository = mockk()
        val questions = listOf(makeQuestion("1"), makeQuestion("2"))
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(questions)

        val vm = QuizPlayerViewModel(repo, savedState())

        assertEquals(2, vm.state.value.questions.size)
        assertEquals(0, vm.state.value.currentIndex)
        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.errorRes)
        assertFalse(vm.state.value.finished)
    }

    @Test
    fun `load failure surfaces errorRes`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.failure(RuntimeException("boom"))

        val vm = QuizPlayerViewModel(repo, savedState())

        assertTrue(vm.state.value.questions.isEmpty())
        assertEquals(R.string.quiz_error_load, vm.state.value.errorRes)
    }

    @Test
    fun `selectAnswer correct increments correctCount`() = runTest {
        val repo: QuizRepository = mockk()
        val question = makeQuestion("1", correct = "A. Correct")
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(listOf(question))

        val vm = QuizPlayerViewModel(repo, savedState())
        vm.selectAnswer("A. Correct")

        assertEquals(1, vm.state.value.correctCount)
        assertTrue(vm.state.value.showExplanation)
        assertEquals("A. Correct", vm.state.value.selectedAnswer)
    }

    @Test
    fun `selectAnswer wrong does not increment correctCount`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(
            listOf(makeQuestion("1", correct = "A. Correct")),
        )

        val vm = QuizPlayerViewModel(repo, savedState())
        vm.selectAnswer("B. Wrong")

        assertEquals(0, vm.state.value.correctCount)
        assertTrue(vm.state.value.showExplanation)
    }

    @Test
    fun `selectAnswer twice is ignored`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(
            listOf(makeQuestion("1", correct = "A. Correct")),
        )

        val vm = QuizPlayerViewModel(repo, savedState())
        vm.selectAnswer("A. Correct")
        vm.selectAnswer("B. Wrong") // second tap ignored

        assertEquals(1, vm.state.value.correctCount)
        assertEquals("A. Correct", vm.state.value.selectedAnswer)
    }

    @Test
    fun `next advances index and clears selection`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(
            listOf(makeQuestion("1"), makeQuestion("2")),
        )

        val vm = QuizPlayerViewModel(repo, savedState())
        vm.selectAnswer("A. Correct")
        vm.next()

        assertEquals(1, vm.state.value.currentIndex)
        assertNull(vm.state.value.selectedAnswer)
        assertFalse(vm.state.value.showExplanation)
        assertFalse(vm.state.value.finished)
    }

    @Test
    fun `next on last question sets finished`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(
            listOf(makeQuestion("1")),
        )

        val vm = QuizPlayerViewModel(repo, savedState())
        vm.selectAnswer("A. Correct")
        vm.next()

        assertTrue(vm.state.value.finished)
    }

    @Test
    fun `progress computed correctly`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(
            listOf(makeQuestion("1"), makeQuestion("2"), makeQuestion("3"), makeQuestion("4")),
        )

        val vm = QuizPlayerViewModel(repo, savedState())

        assertEquals(0.25f, vm.state.value.progress, 0.001f)

        vm.selectAnswer("A. Correct")
        vm.next()

        assertEquals(0.5f, vm.state.value.progress, 0.001f)
    }

    @Test
    fun `empty questions surfaces quiz_empty error`() = runTest {
        val repo: QuizRepository = mockk()
        coEvery { repo.fetchQuestions(specialty) } returns Result.success(emptyList())

        val vm = QuizPlayerViewModel(repo, savedState())

        assertEquals(R.string.quiz_empty, vm.state.value.errorRes)
        assertTrue(vm.state.value.questions.isEmpty())
    }
}
