package com.medsurgery.kiruplus.domain.store

interface StoreRepository {
    /**
     * Devuelve los productos visibles ordenados por `sort_order ASC`.
     * Filtra `is_visible = true` server-side para no traer borradores ni archivados.
     */
    suspend fun fetchVisibleProducts(): Result<List<StoreProduct>>
}
