package com.medsurgery.kiruplus.feature.chapterexam

import androidx.lifecycle.SavedStateHandle
import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExam
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExamQuestion
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExamRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ChapterExamViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val examId = "Chapter-042"

    private fun savedState() = SavedStateHandle(mapOf("examId" to examId))

    private fun makeQuestion(
        id: String,
        correctIndex: Int = 0,
        optionCount: Int = 4,
    ) = ChapterExamQuestion(
        id = id,
        prompt = "Question $id",
        options = (0 until optionCount).map { "Option $it" },
        correctIndex = correctIndex,
        rationale = "Because option $correctIndex is right.",
    )

    private fun makeExam(questionCount: Int = 3) = ChapterExam(
        id = examId,
        chapterNumber = 42,
        title = "Test Exam",
        questions = (1..questionCount).map { makeQuestion("q$it", correctIndex = 0) },
    )

    private fun makeRepo(result: Result<ChapterExam?>): ChapterExamRepository {
        val repo: ChapterExamRepository = mockk()
        every { repo.getChapterExam(examId) } returns flowOf(result)
        return repo
    }

    @Test
    fun `loading resolves to content on success`() = runTest {
        val exam = makeExam()
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        assertFalse(vm.state.value.isLoading)
        assertEquals(exam, vm.state.value.exam)
        assertNull(vm.state.value.error)
        assertFalse(vm.state.value.isEmpty)
    }

    @Test
    fun `loading resolves to error on failure`() = runTest {
        val vm = ChapterExamViewModel(
            savedState(),
            makeRepo(Result.failure(RuntimeException("network error"))),
        )

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.exam)
        assertTrue(vm.state.value.error != null)
    }

    @Test
    fun `loading resolves to error when exam not found (null)`() = runTest {
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(null)))

        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.exam)
        assertEquals("chapter_exam_not_found", vm.state.value.error)
    }

    @Test
    fun `selectOption correct increments correctCount`() = runTest {
        val exam = makeExam(questionCount = 2)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(0)

        assertEquals(0, vm.state.value.selectedOptionIndex)
        assertEquals(1, vm.state.value.correctCount)
        assertTrue(vm.state.value.isAnswered)
    }

    @Test
    fun `selectOption wrong does not increment correctCount`() = runTest {
        val exam = makeExam()
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(1)

        assertEquals(1, vm.state.value.selectedOptionIndex)
        assertEquals(0, vm.state.value.correctCount)
        assertTrue(vm.state.value.isAnswered)
    }

    @Test
    fun `selectOption is ignored when already answered`() = runTest {
        val exam = makeExam()
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(0)
        vm.selectOption(2)

        assertEquals(0, vm.state.value.selectedOptionIndex)
        assertEquals(1, vm.state.value.correctCount)
    }

    @Test
    fun `nextQuestion advances currentIndex`() = runTest {
        val exam = makeExam(questionCount = 3)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(0)
        vm.nextQuestion()

        assertEquals(1, vm.state.value.currentIndex)
        assertNull(vm.state.value.selectedOptionIndex)
        assertFalse(vm.state.value.isCompleted)
    }

    @Test
    fun `nextQuestion is ignored when not answered`() = runTest {
        val exam = makeExam(questionCount = 3)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.nextQuestion()

        assertEquals(0, vm.state.value.currentIndex)
    }

    @Test
    fun `completing all questions sets isCompleted`() = runTest {
        val exam = makeExam(questionCount = 2)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(0); vm.nextQuestion()
        vm.selectOption(0); vm.nextQuestion()

        assertTrue(vm.state.value.isCompleted)
    }

    @Test
    fun `scorePct is correct after all questions answered correctly`() = runTest {
        val exam = makeExam(questionCount = 4)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        repeat(4) { vm.selectOption(0); vm.nextQuestion() }

        assertEquals(4, vm.state.value.correctCount)
        assertEquals(100, vm.state.value.scorePct)
    }

    @Test
    fun `scorePct is 50 when half correct`() = runTest {
        val exam = makeExam(questionCount = 4)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(0); vm.nextQuestion()
        vm.selectOption(1); vm.nextQuestion()
        vm.selectOption(0); vm.nextQuestion()
        vm.selectOption(1); vm.nextQuestion()

        assertEquals(2, vm.state.value.correctCount)
        assertEquals(50, vm.state.value.scorePct)
    }

    @Test
    fun `restart resets all progress`() = runTest {
        val exam = makeExam(questionCount = 2)
        val vm = ChapterExamViewModel(savedState(), makeRepo(Result.success(exam)))

        vm.selectOption(0); vm.nextQuestion()
        vm.selectOption(0); vm.nextQuestion()
        assertTrue(vm.state.value.isCompleted)

        vm.restart()

        assertEquals(0, vm.state.value.currentIndex)
        assertNull(vm.state.value.selectedOptionIndex)
        assertEquals(0, vm.state.value.correctCount)
        assertFalse(vm.state.value.isCompleted)
    }
}
