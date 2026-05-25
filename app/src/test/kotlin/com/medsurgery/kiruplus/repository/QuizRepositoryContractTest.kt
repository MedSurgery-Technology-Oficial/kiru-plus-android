package com.medsurgery.kiruplus.repository

import com.medsurgery.kiruplus.domain.quiz.QuizQuestion
import com.medsurgery.kiruplus.domain.quiz.QuizRepository
import com.medsurgery.kiruplus.domain.quiz.QuizSpecialty
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizRepositoryContractTest {

    private val specialties = listOf(
        QuizSpecialty(name = "Hepatobiliary", questionCount = 10),
        QuizSpecialty(name = "Emergency Surgery", questionCount = 15),
    )

    private val sampleQuestion = QuizQuestion(
        id = "q1",
        questionText = "¿Cuál es el score APACHE II?",
        options = listOf("A", "B", "C", "D"),
        correctAnswer = "B",
        explanation = "El score APACHE II valora...",
        topic = "Scoring",
        specialty = "Hepatobiliary",
    )

    // --- fetchSpecialties ---

    @Test
    fun `fetchSpecialties returns list on success`() = runTest {
        val repo = FakeQuizRepository(specialties = specialties)
        val result = repo.fetchSpecialties()
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
        assertEquals("Hepatobiliary", result.getOrThrow()[0].name)
    }

    @Test
    fun `fetchSpecialties returns empty list when no specialties`() = runTest {
        val repo = FakeQuizRepository()
        val result = repo.fetchSpecialties()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `fetchSpecialties returns failure on error`() = runTest {
        val repo = FakeQuizRepository(shouldFail = true)
        val result = repo.fetchSpecialties()
        assertTrue(result.isFailure)
    }

    // --- fetchQuestions ---

    @Test
    fun `fetchQuestions returns questions for matching specialty`() = runTest {
        val repo = FakeQuizRepository(
            questions = listOf(sampleQuestion),
        )
        val result = repo.fetchQuestions("Hepatobiliary")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("q1", result.getOrThrow()[0].id)
    }

    @Test
    fun `fetchQuestions returns empty for unmatched specialty`() = runTest {
        val repo = FakeQuizRepository(questions = listOf(sampleQuestion))
        val result = repo.fetchQuestions("Thoracic")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `fetchQuestions correctAnswer is preserved`() = runTest {
        val repo = FakeQuizRepository(questions = listOf(sampleQuestion))
        val question = repo.fetchQuestions("Hepatobiliary").getOrThrow()[0]
        assertEquals("B", question.correctAnswer)
        assertEquals(4, question.options.size)
    }

    @Test
    fun `fetchQuestions returns failure on error`() = runTest {
        val repo = FakeQuizRepository(shouldFail = true)
        val result = repo.fetchQuestions("Any")
        assertTrue(result.isFailure)
    }
}

private class FakeQuizRepository(
    private val specialties: List<QuizSpecialty> = emptyList(),
    private val questions: List<QuizQuestion> = emptyList(),
    private val shouldFail: Boolean = false,
) : QuizRepository {

    override suspend fun fetchSpecialties(): Result<List<QuizSpecialty>> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(specialties)

    override suspend fun fetchQuestions(specialty: String): Result<List<QuizQuestion>> =
        if (shouldFail) Result.failure(RuntimeException("network error"))
        else Result.success(questions.filter { it.specialty == specialty })
}
