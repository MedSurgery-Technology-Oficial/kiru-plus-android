package com.medsurgery.kiruplus.data.cases

import com.medsurgery.kiruplus.core.assets.AssetReader
import com.medsurgery.kiruplus.domain.cases.ClinicalCase
import com.medsurgery.kiruplus.domain.cases.ClinicalCasesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClinicalCasesRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
) : ClinicalCasesRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile private var cache: List<ClinicalCase>? = null

    private suspend fun loadAll(): List<ClinicalCase> {
        cache?.let { return it }
        return withContext(Dispatchers.IO) {
            val stream = assetReader.open(ASSET_PATH)
                ?: run {
                    Timber.w("clinical_cases.json asset not found")
                    return@withContext emptyList()
                }
            runCatching {
                json.decodeFromString<List<ClinicalCaseDto>>(stream.bufferedReader().readText())
                    .map { it.toDomain() }
            }.onFailure { Timber.w(it, "clinical_cases.json parse failed") }
             .getOrElse { emptyList() }
        }.also { cache = it }
    }

    override suspend fun getCases(): List<ClinicalCase> = loadAll()

    override suspend fun getCaseById(id: String): ClinicalCase? =
        loadAll().firstOrNull { it.id == id }

    override suspend fun getCasesByCategory(category: String): List<ClinicalCase> =
        loadAll().filter { it.category == category }

    override fun searchCases(query: String, cases: List<ClinicalCase>): List<ClinicalCase> {
        if (query.isBlank()) return cases
        val q = query.trim().lowercase()
        return cases.filter { case ->
            case.title.lowercase().contains(q) ||
                case.category.lowercase().contains(q) ||
                case.difficulty.lowercase().contains(q) ||
                case.diagnosis.lowercase().contains(q) ||
                case.presentation.lowercase().contains(q) ||
                case.tags.any { it.lowercase().contains(q) }
        }
    }

    private companion object {
        const val ASSET_PATH = "clinical_cases.json"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ClinicalCasesRepositoryBindings {
    @Binds
    abstract fun bindClinicalCasesRepository(impl: ClinicalCasesRepositoryImpl): ClinicalCasesRepository
}
