package com.medsurgery.kiruplus.feature.kcortex

import com.medsurgery.kiruplus.MainDispatcherRule
import com.medsurgery.kiruplus.domain.kcortex.KCortexAnalysis
import com.medsurgery.kiruplus.domain.kcortex.KCortexAnalysisType
import com.medsurgery.kiruplus.domain.kcortex.KCortexRepository
import com.medsurgery.kiruplus.domain.kcortex.KCortexRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class KCortexViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private fun fakeAnalysis(type: KCortexAnalysisType = KCortexAnalysisType.LABORATORIOS) =
        KCortexAnalysis(
            id = "fake-id",
            analysisType = type,
            status = "success",
            quality = "Analizable",
            modality = type.displayName,
            findings = "Hemoglobina normal. Sin alteraciones críticas.",
            preliminaryInterpretation = "Resultados dentro de parámetros normales.",
            limitations = "Ninguna identificada.",
            missingData = "",
            recommendations = "Seguimiento de rutina.",
            redFlags = emptyList(),
            clinicalData = emptyList(),
            clinicalDataExtractedCount = 0,
            adminDataIgnoredCount = 0,
            rejectionReason = null,
            rawInput = "Hb 14 g/dL",
        )

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial state is Input phase`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        assertTrue(vm.uiState.value.phase is KCortexPhase.Input)
    }

    @Test
    fun `initial state has LABORATORIOS as default type`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        assertEquals(KCortexAnalysisType.LABORATORIOS, vm.uiState.value.selectedType)
    }

    @Test
    fun `initial state has empty clinicalInput`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        assertEquals("", vm.uiState.value.clinicalInput)
    }

    @Test
    fun `initial state has null errorMessage and null result`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        assertNull(vm.uiState.value.errorMessage)
        assertNull(vm.uiState.value.result)
    }

    // ── onTypeSelected ────────────────────────────────────────────────────────

    @Test
    fun `onTypeSelected updates selectedType`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        vm.onTypeSelected(KCortexAnalysisType.ECG)
        assertEquals(KCortexAnalysisType.ECG, vm.uiState.value.selectedType)
    }

    @Test
    fun `onTypeSelected can switch to all available types`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        KCortexAnalysisType.entries.forEach { type ->
            vm.onTypeSelected(type)
            assertEquals(type, vm.uiState.value.selectedType)
        }
    }

    // ── onInputChanged ────────────────────────────────────────────────────────

    @Test
    fun `onInputChanged updates clinicalInput`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        vm.onInputChanged("pH 7.32 PaCO2 50 HCO3 24")
        assertEquals("pH 7.32 PaCO2 50 HCO3 24", vm.uiState.value.clinicalInput)
    }

    @Test
    fun `onInputChanged clears errorMessage`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        // trigger validation error first
        vm.submitAnalysis()
        assertNotNull(vm.uiState.value.errorMessage)
        // typing clears error
        vm.onInputChanged("some text")
        assertNull(vm.uiState.value.errorMessage)
    }

    // ── submitAnalysis — validation ───────────────────────────────────────────

    @Test
    fun `submit with empty input shows validation error`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        vm.submitAnalysis()
        assertNotNull(vm.uiState.value.errorMessage)
        assertTrue(vm.uiState.value.phase is KCortexPhase.Input)
        coVerify(exactly = 0) { repo.analyze(any()) }
    }

    @Test
    fun `submit with whitespace-only input shows validation error`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        vm.onInputChanged("   \n   ")
        vm.submitAnalysis()
        assertNotNull(vm.uiState.value.errorMessage)
        assertTrue(vm.uiState.value.phase is KCortexPhase.Input)
        coVerify(exactly = 0) { repo.analyze(any()) }
    }

    // ── submitAnalysis — success ──────────────────────────────────────────────

    @Test
    fun `successful analysis transitions to Result phase`() = runTest {
        val analysis = fakeAnalysis()
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.success(analysis)
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("Hb 14 g/dL Na 140 K 4.0")
        vm.submitAnalysis()

        assertTrue(vm.uiState.value.phase is KCortexPhase.Result)
        assertEquals(analysis, vm.uiState.value.result)
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `submit sends correct analysisType to repository`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.success(fakeAnalysis(KCortexAnalysisType.GASOMETRIA))
        val vm = KCortexViewModel(repo)

        vm.onTypeSelected(KCortexAnalysisType.GASOMETRIA)
        vm.onInputChanged("pH 7.28 PaCO2 52")
        vm.submitAnalysis()

        coVerify {
            repo.analyze(
                match { it.analysisType == KCortexAnalysisType.GASOMETRIA }
            )
        }
    }

    @Test
    fun `submit trims input before sending to repository`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.success(fakeAnalysis())
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("  Hb 14  ")
        vm.submitAnalysis()

        coVerify {
            repo.analyze(
                match { it.clinicalInput == "Hb 14" }
            )
        }
    }

    // ── submitAnalysis — failure ──────────────────────────────────────────────

    @Test
    fun `failed analysis returns to Input phase with error`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.failure(RuntimeException("network error"))
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("Hb 14 g/dL")
        vm.submitAnalysis()

        assertTrue(vm.uiState.value.phase is KCortexPhase.Input)
        assertNotNull(vm.uiState.value.errorMessage)
        assertNull(vm.uiState.value.result)
    }

    @Test
    fun `HTTP 429 error shows rate limit message`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.failure(RuntimeException("HTTP 429"))
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("test input")
        vm.submitAnalysis()

        assertTrue(vm.uiState.value.errorMessage?.contains("429") == true ||
                   vm.uiState.value.errorMessage?.contains("muchas solicitudes") == true)
    }

    @Test
    fun `network error shows connectivity message`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.failure(RuntimeException("Unable to resolve host"))
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("test input")
        vm.submitAnalysis()

        val msg = vm.uiState.value.errorMessage ?: ""
        assertTrue("Error should mention connection",
            msg.contains("Internet") || msg.contains("conexión") || msg.contains("red"))
    }

    // ── reset ─────────────────────────────────────────────────────────────────

    @Test
    fun `reset returns to Input phase`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.success(fakeAnalysis())
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("Hb 14")
        vm.submitAnalysis()
        assertTrue(vm.uiState.value.phase is KCortexPhase.Result)

        vm.reset()
        assertTrue(vm.uiState.value.phase is KCortexPhase.Input)
    }

    @Test
    fun `reset clears result and errorMessage`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.success(fakeAnalysis())
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("Hb 14")
        vm.submitAnalysis()
        vm.reset()

        assertNull(vm.uiState.value.result)
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `reset preserves selectedType`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)

        vm.onTypeSelected(KCortexAnalysisType.ECG)
        vm.reset()

        assertEquals(KCortexAnalysisType.ECG, vm.uiState.value.selectedType)
    }

    @Test
    fun `reset clears clinicalInput`() = runTest {
        val repo = mockk<KCortexRepository>()
        coEvery { repo.analyze(any()) } returns Result.success(fakeAnalysis())
        val vm = KCortexViewModel(repo)

        vm.onInputChanged("some long clinical text")
        vm.submitAnalysis()
        vm.reset()

        assertEquals("", vm.uiState.value.clinicalInput)
    }

    // ── dismissError ──────────────────────────────────────────────────────────

    @Test
    fun `dismissError clears errorMessage`() = runTest {
        val repo = mockk<KCortexRepository>(relaxed = true)
        val vm = KCortexViewModel(repo)
        vm.submitAnalysis() // triggers validation error
        assertNotNull(vm.uiState.value.errorMessage)

        vm.dismissError()
        assertNull(vm.uiState.value.errorMessage)
    }
}
