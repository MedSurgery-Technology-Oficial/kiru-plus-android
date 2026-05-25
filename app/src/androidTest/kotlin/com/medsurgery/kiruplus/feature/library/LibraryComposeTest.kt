package com.medsurgery.kiruplus.feature.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.medsurgery.kiruplus.domain.library.CurriculumBlock
import com.medsurgery.kiruplus.domain.library.CurriculumChapter
import com.medsurgery.kiruplus.domain.library.CurriculumUnit
import com.medsurgery.kiruplus.domain.library.LibraryModule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI tests for LibraryContent (stateless).
 * Run with: ./gradlew connectedDebugAndroidTest
 * Requires a connected device or running emulator.
 */
@RunWith(AndroidJUnit4::class)
class LibraryComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun makeModule(id: String, title: String) = LibraryModule(
        id = id,
        title = title,
        points = listOf(),
    )

    private fun makeBlock(
        blockNumber: Int = 1,
        availableChapters: Int = 1,
        lockedChapters: Int = 1,
    ): CurriculumBlock {
        val chapters = buildList {
            repeat(availableChapters) { i ->
                add(CurriculumChapter(
                    id = "B0${blockNumber}-U01-C00${i + 1}",
                    chapterNumber = i + 1,
                    title = "Capítulo disponible ${i + 1}",
                    isAvailable = true,
                ))
            }
            repeat(lockedChapters) { i ->
                add(CurriculumChapter(
                    id = "B0${blockNumber}-U01-C0${availableChapters + i + 1}",
                    chapterNumber = availableChapters + i + 1,
                    title = "Capítulo bloqueado ${i + 1}",
                    isAvailable = false,
                ))
            }
        }
        return CurriculumBlock(
            id = "B0$blockNumber",
            blockNumber = blockNumber,
            title = "Bloque $blockNumber: Fundamentos",
            units = listOf(
                CurriculumUnit(
                    id = "B0${blockNumber}-U01",
                    unitNumber = 1,
                    title = "Unidad de cirugía general",
                    chapters = chapters,
                ),
            ),
        )
    }

    // --- Loading state ---

    @Test
    fun loadingState_isShownWhenLoading() {
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = true),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = {},
                onRetry = {},
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithText("Library").assertIsDisplayed()
    }

    // --- Error state ---

    @Test
    fun errorState_showsErrorAndRetry() {
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, error = "fail"),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = {},
                onRetry = {},
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithText("Couldn't load library. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButton_callsOnRetry() {
        var retryCalled = false
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, error = "fail"),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = {},
                onRetry = { retryCalled = true },
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retryCalled)
    }

    // --- Modules tab ---

    @Test
    fun modulesTab_showsModuleList() {
        val modules = listOf(
            makeModule("m1", "Principios de Cirugía"),
            makeModule("m2", "Manejo perioperatorio"),
        )
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, modules = modules, selectedTab = 0),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = {},
                onRetry = {},
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithText("Principios de Cirugía").assertIsDisplayed()
        composeTestRule.onNodeWithText("Manejo perioperatorio").assertIsDisplayed()
    }

    @Test
    fun modulesTab_clickModule_callsOnOpenModule() {
        var openedModuleId = ""
        val modules = listOf(makeModule("m1", "Principios de Cirugía"))
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, modules = modules, selectedTab = 0),
                onBack = {},
                onOpenModule = { openedModuleId = it },
                onStartChapterQuiz = {},
                onRetry = {},
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithText("Principios de Cirugía").performClick()
        assertTrue(openedModuleId == "m1")
    }

    // --- Curriculum tab ---

    @Test
    fun curriculumTab_showsBlockTitle() {
        val block = makeBlock(blockNumber = 1, availableChapters = 1, lockedChapters = 0)
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, curriculum = listOf(block), selectedTab = 1),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = {},
                onRetry = {},
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithText("Bloque 1: Fundamentos").assertIsDisplayed()
    }

    @Test
    fun quizButton_onlyShown_forAvailableChapters() {
        val block = makeBlock(blockNumber = 1, availableChapters = 1, lockedChapters = 1)
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, curriculum = listOf(block), selectedTab = 1),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = {},
                onRetry = {},
                onSelectTab = {},
            )
        }
        // Expand the block via the expand icon
        composeTestRule.onNodeWithContentDescription("Expand block").performClick()
        // Only one Start quiz button should exist (locked chapter has none)
        val quizButtons = composeTestRule.onAllNodesWithText("Start quiz")
        quizButtons[0].assertIsDisplayed()
        assertTrue(
            runCatching { quizButtons[1].assertIsDisplayed() }.isFailure,
        )
    }

    @Test
    fun quizButton_click_callsOnStartChapterQuiz() {
        var quizExamId = ""
        val block = makeBlock(blockNumber = 1, availableChapters = 1, lockedChapters = 0)
        composeTestRule.setContent {
            LibraryContent(
                state = LibraryUiState(isLoading = false, curriculum = listOf(block), selectedTab = 1),
                onBack = {},
                onOpenModule = {},
                onStartChapterQuiz = { quizExamId = it },
                onRetry = {},
                onSelectTab = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("Expand block").performClick()
        composeTestRule.onNodeWithText("Start quiz").performClick()
        assertTrue(quizExamId.startsWith("Chapter-"))
    }
}
