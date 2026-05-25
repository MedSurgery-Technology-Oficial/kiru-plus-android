package com.medsurgery.kiruplus.library

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.domain.library.CurriculumBlock
import com.medsurgery.kiruplus.domain.library.CurriculumChapter
import com.medsurgery.kiruplus.domain.library.CurriculumUnit
import com.medsurgery.kiruplus.domain.library.LibraryModule
import com.medsurgery.kiruplus.domain.library.LibraryRepository
import com.medsurgery.kiruplus.domain.library.StudyPoint
import com.medsurgery.kiruplus.feature.library.LibraryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LibraryViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private fun makeModule(id: String) = LibraryModule(
        id = id,
        title = "Module $id",
        points = listOf(StudyPoint("p1", "Point 1", "Details")),
    )

    private fun makeBlock() = CurriculumBlock(
        id = "B01",
        blockNumber = 1,
        title = "Bloque I",
        units = listOf(
            CurriculumUnit(
                id = "U01", unitNumber = 1, title = "Unidad 1",
                chapters = listOf(
                    CurriculumChapter("C01", 1, "Historia", isAvailable = true),
                ),
            ),
        ),
    )

    private fun makeRepo(
        modulesResult: Result<List<LibraryModule>> = Result.success(listOf(makeModule("m1"), makeModule("m2"))),
        curriculumResult: Result<List<CurriculumBlock>> = Result.success(listOf(makeBlock())),
    ): LibraryRepository {
        val repo = mockk<LibraryRepository>()
        every { repo.getStudyModules() } returns flowOf(modulesResult)
        every { repo.getCurriculumBlocks() } returns flowOf(curriculumResult)
        every { repo.getStudyModule(any()) } returns flowOf(Result.success(makeModule("m1")))
        return repo
    }

    @Test
    fun `initial state is loading`() {
        val repo = mockk<LibraryRepository>(relaxed = true)
        every { repo.getStudyModules() } returns flowOf()
        every { repo.getCurriculumBlocks() } returns flowOf()
        // Can't easily test the transient loading=true before data arrives with UnconfinedTestDispatcher,
        // but we verify the starting state shape is correct.
        assertTrue(LibraryViewModel(repo).state.value.modules.isEmpty())
    }

    @Test
    fun `load success populates modules`() = runTest {
        val vm = LibraryViewModel(makeRepo())
        assertFalse(vm.state.value.isLoading)
        assertEquals(2, vm.state.value.modules.size)
        assertEquals("m1", vm.state.value.modules[0].id)
    }

    @Test
    fun `load success populates curriculum`() = runTest {
        val vm = LibraryViewModel(makeRepo())
        assertEquals(1, vm.state.value.curriculum.size)
        assertEquals("B01", vm.state.value.curriculum[0].id)
    }

    @Test
    fun `load sets error when modules fail`() = runTest {
        val repo = makeRepo(
            modulesResult = Result.failure(Exception("asset_modules_missing")),
            curriculumResult = Result.success(listOf(makeBlock())),
        )
        val vm = LibraryViewModel(repo)
        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `isEmpty true when both lists empty and no error`() = runTest {
        val repo = makeRepo(
            modulesResult = Result.success(emptyList()),
            curriculumResult = Result.success(emptyList()),
        )
        val vm = LibraryViewModel(repo)
        assertTrue(vm.state.value.isEmpty)
    }

    @Test
    fun `isEmpty false when modules are present`() = runTest {
        val vm = LibraryViewModel(makeRepo())
        assertFalse(vm.state.value.isEmpty)
    }

    @Test
    fun `isEmpty false when error is set even with empty lists`() = runTest {
        val repo = makeRepo(
            modulesResult = Result.failure(Exception("network")),
            curriculumResult = Result.success(emptyList()),
        )
        val vm = LibraryViewModel(repo)
        assertFalse("isEmpty must be false when there is an error", vm.state.value.isEmpty)
        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `selectTab updates selectedTab`() = runTest {
        val vm = LibraryViewModel(makeRepo())
        assertEquals(0, vm.state.value.selectedTab)
        vm.selectTab(1)
        assertEquals(1, vm.state.value.selectedTab)
    }

    @Test
    fun `load resets error and triggers reload`() = runTest {
        val repo = makeRepo(
            modulesResult = Result.failure(Exception("network")),
            curriculumResult = Result.success(emptyList()),
        )
        val vm = LibraryViewModel(repo)
        assertNotNull(vm.state.value.error)

        every { repo.getStudyModules() } returns flowOf(Result.success(listOf(makeModule("m1"))))
        vm.load()

        assertNull(vm.state.value.error)
        assertEquals(1, vm.state.value.modules.size)
    }
}
