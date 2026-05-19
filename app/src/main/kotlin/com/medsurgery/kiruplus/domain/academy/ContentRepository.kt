package com.medsurgery.kiruplus.domain.academy

interface ContentRepository {
    /**
     * Trae los lessons más recientes del catálogo educativo, excluyendo `pearl`
     * y `question` (vistas separadas). Limit 100 por paginación implícita.
     */
    suspend fun fetchLessons(): Result<List<ContentItem>>

    /** Detalle por id. Null si no existe / no visible. */
    suspend fun fetchById(id: String): Result<ContentItem?>
}
