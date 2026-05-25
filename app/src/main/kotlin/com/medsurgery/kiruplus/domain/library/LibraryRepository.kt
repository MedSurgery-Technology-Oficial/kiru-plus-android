package com.medsurgery.kiruplus.domain.library

import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getStudyModules(): Flow<Result<List<LibraryModule>>>
    fun getStudyModule(id: String): Flow<Result<LibraryModule?>>
    fun getCurriculumBlocks(): Flow<Result<List<CurriculumBlock>>>
}
