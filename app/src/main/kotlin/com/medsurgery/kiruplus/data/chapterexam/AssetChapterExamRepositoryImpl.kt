package com.medsurgery.kiruplus.data.chapterexam

import com.medsurgery.kiruplus.core.assets.AssetReader
import com.medsurgery.kiruplus.data.chapterexam.dto.ChapterExamDto
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExam
import com.medsurgery.kiruplus.domain.chapterexam.ChapterExamRepository
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
class AssetChapterExamRepositoryImpl @Inject constructor(
    private val assetReader: AssetReader,
) : ChapterExamRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile private var cacheResult: Result<Map<String, ChapterExam>>? = null

    private fun loadAll(): Result<Map<String, ChapterExam>> {
        cacheResult?.let { return it }
        val stream = assetReader.open(ASSET_PATH)
            ?: return Result.failure<Map<String, ChapterExam>>(
                Exception("asset_chapter_exams_missing"),
            ).also { cacheResult = it }

        return runCatching {
            json.decodeFromString<List<ChapterExamDto>>(stream.bufferedReader().readText())
                .associate { it.id to it.toDomain() }
        }.onFailure { Timber.w(it, "ChapterExams parse failed") }
         .also { cacheResult = it }
    }

    override fun getChapterExam(examId: String): Flow<Result<ChapterExam?>> = flow {
        loadAll().fold(
            onSuccess = { emit(Result.success(it[examId])) },
            onFailure = { emit(Result.failure(it)) },
        )
    }.flowOn(Dispatchers.IO)

    override fun getAvailableExamIds(): Flow<Result<Set<String>>> = flow {
        loadAll().fold(
            onSuccess = { emit(Result.success(it.keys)) },
            onFailure = { emit(Result.failure(it)) },
        )
    }.flowOn(Dispatchers.IO)

    private companion object {
        const val ASSET_PATH = "curriculum/ChapterExams.json"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ChapterExamRepositoryBindings {
    @Binds
    abstract fun bindChapterExamRepository(
        impl: AssetChapterExamRepositoryImpl,
    ): ChapterExamRepository
}
