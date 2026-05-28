package com.medsurgery.kiruplus.data.drugs

import com.medsurgery.kiruplus.core.assets.AssetReader
import com.medsurgery.kiruplus.domain.drugs.Drug
import com.medsurgery.kiruplus.domain.drugs.DrugsRepository
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
class DrugsRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
) : DrugsRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile private var cache: List<Drug>? = null

    private suspend fun loadAll(): List<Drug> {
        cache?.let { return it }
        return withContext(Dispatchers.IO) {
            val stream = assetReader.open(ASSET_PATH)
                ?: run {
                    Timber.w("drugs.json asset not found")
                    return@withContext emptyList()
                }
            runCatching {
                json.decodeFromString<List<DrugDto>>(stream.bufferedReader().readText())
                    .map { it.toDomain() }
            }.onFailure { Timber.w(it, "drugs.json parse failed") }
             .getOrElse { emptyList() }
        }.also { cache = it }
    }

    override suspend fun getDrugs(): List<Drug> = loadAll()

    override suspend fun getDrugById(id: String): Drug? =
        loadAll().firstOrNull { it.id == id }

    override suspend fun getDrugsByCategory(category: String): List<Drug> =
        loadAll().filter { it.category == category }

    override fun searchDrugs(query: String, drugs: List<Drug>): List<Drug> {
        if (query.isBlank()) return drugs
        val q = query.trim().lowercase()
        return drugs.filter { drug ->
            drug.nameEs.lowercase().contains(q) ||
                drug.genericName.lowercase().contains(q) ||
                drug.category.lowercase().contains(q) ||
                drug.indication.lowercase().contains(q) ||
                drug.tags.any { it.lowercase().contains(q) }
        }
    }

    private companion object {
        const val ASSET_PATH = "drugs.json"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DrugsRepositoryBindings {
    @Binds
    abstract fun bindDrugsRepository(impl: DrugsRepositoryImpl): DrugsRepository
}
