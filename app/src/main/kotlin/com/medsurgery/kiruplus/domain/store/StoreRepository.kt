package com.medsurgery.kiruplus.domain.store

interface StoreRepository {
    /**
     * Devuelve los productos visibles ordenados por `sort_order ASC`.
     * Filtra `is_visible = true` server-side para no traer borradores ni archivados.
     */
    suspend fun fetchVisibleProducts(): Result<List<StoreProduct>>

    /**
     * Trae un único producto por id. Devuelve null si no existe o no es visible
     * (la RLS server-side ya filtra `is_visible = true` para el usuario).
     */
    suspend fun fetchProduct(id: String): Result<StoreProduct?>
}
