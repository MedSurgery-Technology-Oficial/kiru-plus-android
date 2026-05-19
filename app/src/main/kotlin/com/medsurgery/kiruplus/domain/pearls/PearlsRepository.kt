package com.medsurgery.kiruplus.domain.pearls

interface PearlsRepository {
    /**
     * Trae todas las perlas (200 filas hoy — sin paginación por simplicidad).
     * RLS gates server-side a usuarios autenticados no-anónimos.
     */
    suspend fun fetchAllPearls(): Result<List<Pearl>>

    /** Trae una perla por id. Null si no existe / no visible para el user. */
    suspend fun fetchPearl(id: Int): Result<Pearl?>
}
