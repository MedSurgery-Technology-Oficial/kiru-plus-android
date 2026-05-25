package com.medsurgery.kiruplus.library

import com.medsurgery.kiruplus.core.assets.AssetReader
import com.medsurgery.kiruplus.data.library.LibraryRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class LibraryRepositoryImplTest {

    private val validStudyModulesJson = """
        {
          "lastUpdated": "2024-01-01",
          "version": "1",
          "modules": [
            {
              "id": "module_0",
              "title": "Principios de Cirugía General",
              "icon": "brain",
              "content": [
                {"id": "point_0_0", "title": "Analgesia", "details": "• Paracetamol ± AINE"}
              ]
            },
            {
              "id": "module_1",
              "title": "Manejo Perioperatorio",
              "icon": "heart",
              "content": []
            }
          ]
        }
    """.trimIndent()

    private val validCurriculumJson = """
        {
          "schemaVersion": 1,
          "blocks": [
            {
              "id": "B01",
              "blockNumber": 1,
              "title": "Bloque I: Fundamentos",
              "units": [
                {
                  "id": "B01-U01",
                  "unitNumber": 1,
                  "title": "Unidad 1",
                  "chapters": [
                    {"id": "B01-U01-C001", "chapterNumber": 1, "title": "Historia", "status": "available"},
                    {"id": "B01-U01-C002", "chapterNumber": 2, "title": "Competencias", "status": "locked"}
                  ]
                }
              ]
            }
          ]
        }
    """.trimIndent()

    private fun makeRepo(
        modulesJson: String? = validStudyModulesJson,
        curriculumJson: String? = validCurriculumJson,
    ): LibraryRepositoryImpl {
        val assetReader = mockk<AssetReader>()
        every { assetReader.open("modules/StudyModules.json") } returns
            modulesJson?.let { ByteArrayInputStream(it.toByteArray()) }
        every { assetReader.open("curriculum/curriculum_index_v1.json") } returns
            curriculumJson?.let { ByteArrayInputStream(it.toByteArray()) }
        return LibraryRepositoryImpl(assetReader)
    }

    // --- Study Modules ---

    @Test
    fun `getStudyModules returns modules on valid JSON`() = runTest {
        val repo = makeRepo()
        val result = repo.getStudyModules().first()
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
        assertEquals("module_0", result.getOrThrow()[0].id)
        assertEquals("Principios de Cirugía General", result.getOrThrow()[0].title)
    }

    @Test
    fun `getStudyModules maps content points correctly`() = runTest {
        val repo = makeRepo()
        val modules = repo.getStudyModules().first().getOrThrow()
        val point = modules[0].points[0]
        assertEquals("point_0_0", point.id)
        assertEquals("Analgesia", point.title)
        assertTrue(point.details.contains("Paracetamol"))
    }

    @Test
    fun `getStudyModules returns failure when asset missing`() = runTest {
        val repo = makeRepo(modulesJson = null)
        val result = repo.getStudyModules().first()
        assertTrue(result.isFailure)
    }

    @Test
    fun `getStudyModules returns failure on malformed JSON`() = runTest {
        val repo = makeRepo(modulesJson = "{ not valid json }")
        val result = repo.getStudyModules().first()
        assertTrue(result.isFailure)
    }

    @Test
    fun `getStudyModule returns correct module by id`() = runTest {
        val repo = makeRepo()
        val result = repo.getStudyModule("module_1").first()
        assertTrue(result.isSuccess)
        assertEquals("module_1", result.getOrThrow()?.id)
        assertEquals("Manejo Perioperatorio", result.getOrThrow()?.title)
    }

    @Test
    fun `getStudyModule returns null for unknown id`() = runTest {
        val repo = makeRepo()
        val result = repo.getStudyModule("module_99").first()
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    @Test
    fun `getStudyModules caches result on second call`() = runTest {
        val assetReader = mockk<AssetReader>()
        var openCount = 0
        every { assetReader.open("modules/StudyModules.json") } answers {
            openCount++
            ByteArrayInputStream(validStudyModulesJson.toByteArray())
        }
        every { assetReader.open("curriculum/curriculum_index_v1.json") } returns
            ByteArrayInputStream(validCurriculumJson.toByteArray())
        val repo = LibraryRepositoryImpl(assetReader)

        repo.getStudyModules().first()
        repo.getStudyModules().first()

        assertEquals("Asset file opened more than once — cache not working", 1, openCount)
    }

    // --- Curriculum ---

    @Test
    fun `getCurriculumBlocks returns blocks on valid JSON`() = runTest {
        val repo = makeRepo()
        val result = repo.getCurriculumBlocks().first()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("B01", result.getOrThrow()[0].id)
    }

    @Test
    fun `getCurriculumBlocks maps chapter availability correctly`() = runTest {
        val repo = makeRepo()
        val block = repo.getCurriculumBlocks().first().getOrThrow()[0]
        val chapters = block.units[0].chapters
        assertTrue(chapters[0].isAvailable)
        assertTrue(!chapters[1].isAvailable)
    }

    @Test
    fun `getCurriculumBlocks chapterCount sums all units`() = runTest {
        val repo = makeRepo()
        val block = repo.getCurriculumBlocks().first().getOrThrow()[0]
        assertEquals(2, block.chapterCount)
    }

    @Test
    fun `getCurriculumBlocks returns failure when asset missing`() = runTest {
        val repo = makeRepo(curriculumJson = null)
        val result = repo.getCurriculumBlocks().first()
        assertTrue(result.isFailure)
    }
}
