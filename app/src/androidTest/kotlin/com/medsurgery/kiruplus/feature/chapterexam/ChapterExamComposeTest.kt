package com.medsurgery.kiruplus.feature.chapterexam

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExam
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExamQuestion
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI tests for ChapterExamContent (stateless).
 * Run with: ./gradlew connectedDebugAndroidTest
 * Requires a connected device or running emulator.
 */
@RunWith(AndroidJUnit4::class)
class ChapterExamComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun makeQuestion(
        id: String = "q1",
        prompt: String = "¿Cuál es la respuesta correcta?",
        correctIndex: Int = 0,
    ) = ChapterExamQuestion(
        id = id,
        prompt = prompt,
        options = listOf("Opción A", "Opción B", "Opción C", "Opción D"),
        correctIndex = correctIndex,
        rationale = "La Opción A es correcta porque es la respuesta.",
    )

    private fun makeExam(questionCount: Int = 1) = ChapterExam(
        id = "Chapter-042",
        chapterNumber = 42,
        title = "Capítulo de prueba",
        questions = (1..questionCount).map { makeQuestion("q$it", "Pregunta $it") },
    )

    // --- Loading state ---

    @Test
    fun loadingState_showsIndicator() {
        val state = ChapterExamUiState(isLoading = true)
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Chapter Quiz").assertIsDisplayed()
    }

    // --- Error state ---

    @Test
    fun errorState_showsErrorAndRetryButton() {
        val state = ChapterExamUiState(isLoading = false, error = "load_failed")
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Couldn't load quiz. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButton_callsOnRetry() {
        var retryCalled = false
        val state = ChapterExamUiState(isLoading = false, error = "load_failed")
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = { retryCalled = true },
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retryCalled)
    }

    // --- Question state ---

    @Test
    fun questionState_showsPromptAndOptions() {
        val exam = makeExam()
        val state = ChapterExamUiState(isLoading = false, exam = exam, currentIndex = 0)
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Pregunta 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Opción A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Opción B").assertIsDisplayed()
        composeTestRule.onNodeWithText("Opción C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Opción D").assertIsDisplayed()
    }

    @Test
    fun questionState_progressLabel_isDisplayed() {
        val exam = makeExam(questionCount = 3)
        val state = ChapterExamUiState(isLoading = false, exam = exam, currentIndex = 0)
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Question 1 of 3").assertIsDisplayed()
    }

    @Test
    fun questionState_selectOption_callsCallback() {
        val exam = makeExam()
        val state = ChapterExamUiState(isLoading = false, exam = exam, currentIndex = 0)
        var selectedIndex = -1
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = { selectedIndex = it },
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Opción B").performClick()
        assertTrue(selectedIndex == 1)
    }

    @Test
    fun answeredState_showsRationaleCard() {
        val exam = makeExam()
        val state = ChapterExamUiState(
            isLoading = false,
            exam = exam,
            currentIndex = 0,
            selectedOptionIndex = 0,
        )
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Explanation").assertIsDisplayed()
        composeTestRule.onNodeWithText("La Opción A es correcta porque es la respuesta.").assertIsDisplayed()
    }

    @Test
    fun answeredState_showsNextButton() {
        val exam = makeExam(questionCount = 2)
        val state = ChapterExamUiState(
            isLoading = false,
            exam = exam,
            currentIndex = 0,
            selectedOptionIndex = 0,
        )
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun lastQuestion_answeredState_showsSeeResultsButton() {
        val exam = makeExam(questionCount = 1)
        val state = ChapterExamUiState(
            isLoading = false,
            exam = exam,
            currentIndex = 0,
            selectedOptionIndex = 0,
        )
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("See results").assertIsDisplayed()
    }

    // --- Score state ---

    @Test
    fun scoreState_showsResultsAndButtons() {
        val exam = makeExam(questionCount = 10)
        val state = ChapterExamUiState(
            isLoading = false,
            exam = exam,
            isCompleted = true,
            correctCount = 8,
            currentIndex = 10,
        )
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Quiz complete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry exam").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back to library").assertIsDisplayed()
    }

    @Test
    fun scoreState_restartButton_callsOnRestart() {
        var restartCalled = false
        val exam = makeExam(questionCount = 2)
        val state = ChapterExamUiState(
            isLoading = false,
            exam = exam,
            isCompleted = true,
            correctCount = 1,
            currentIndex = 2,
        )
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = { restartCalled = true },
                onRetry = {},
                onBack = {},
            )
        }
        composeTestRule.onNodeWithText("Retry exam").performClick()
        assertTrue(restartCalled)
    }

    @Test
    fun scoreState_backButton_callsOnBack() {
        var backCalled = false
        val exam = makeExam()
        val state = ChapterExamUiState(
            isLoading = false,
            exam = exam,
            isCompleted = true,
            correctCount = 1,
            currentIndex = 1,
        )
        composeTestRule.setContent {
            ChapterExamContent(
                state = state,
                onSelectOption = {},
                onNext = {},
                onRestart = {},
                onRetry = {},
                onBack = { backCalled = true },
            )
        }
        composeTestRule.onNodeWithText("Back to library").performClick()
        assertTrue(backCalled)
    }
}
