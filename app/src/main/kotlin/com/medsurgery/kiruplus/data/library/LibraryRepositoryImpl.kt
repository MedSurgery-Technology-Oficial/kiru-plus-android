package com.medsurgery.kiruplus.data.library

import com.medsurgery.kiruplus.core.assets.AssetReader
import com.medsurgery.kiruplus.data.library.dto.CurriculumIndexFileDto
import com.medsurgery.kiruplus.data.library.dto.StudyModulesFileDto
import com.medsurgery.kiruplus.domain.library.CurriculumBlock
import com.medsurgery.kiruplus.domain.library.LibraryModule
import com.medsurgery.kiruplus.domain.library.LibraryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
) : LibraryRepository {

    private val json = Json { ignoreUnknownKeys = true }

    // In-memory caches — safe because @Singleton lives for the app lifetime.
    @Volatile private var cachedModules: List<LibraryModule>? = null
    @Volatile private var cachedCurriculum: List<CurriculumBlock>? = null

    override fun getStudyModules(): Flow<Result<List<LibraryModule>>> = flow {
        cachedModules?.let { emit(Result.success(it)); return@flow }
        val stream = assetReader.open("modules/StudyModules.json")
            ?: run { emit(Result.failure(Exception("asset_modules_missing"))); return@flow }
        runCatching {
            val dto = json.decodeFromString<StudyModulesFileDto>(stream.bufferedReader().readText())
            dto.modules.map { it.toDomain() }
        }.fold(
            onSuccess = { modules ->
                cachedModules = modules
                emit(Result.success(modules))
            },
            onFailure = { e ->
                Timber.w(e, "Failed to parse StudyModules")
                emit(Result.failure(e))
            },
        )
    }.flowOn(Dispatchers.IO)

    override fun getStudyModule(id: String): Flow<Result<LibraryModule?>> = flow {
        cachedModules?.find { it.id == id }?.let { emit(Result.success(it)); return@flow }
        getStudyModules().collect { result ->
            emit(result.map { modules -> modules.find { it.id == id } })
        }
    }.flowOn(Dispatchers.IO)

    override fun getCurriculumBlocks(): Flow<Result<List<CurriculumBlock>>> = flow {
        cachedCurriculum?.let { emit(Result.success(it)); return@flow }
        val stream = assetReader.open("curriculum/curriculum_index_v1.json")
            ?: run { emit(Result.failure(Exception("asset_curriculum_missing"))); return@flow }
        runCatching {
            val dto = json.decodeFromString<CurriculumIndexFileDto>(stream.bufferedReader().readText())
            dto.blocks.map { it.toDomain() }
        }.fold(
            onSuccess = { blocks ->
                cachedCurriculum = blocks
                emit(Result.success(blocks))
            },
            onFailure = { e ->
                Timber.w(e, "Failed to parse curriculum index")
                emit(Result.failure(e))
            },
        )
    }.flowOn(Dispatchers.IO)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LibraryRepositoryBindings {
    @Binds
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository
}
