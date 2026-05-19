package com.medsurgery.kiruplus.domain.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Producto del catálogo de la tienda KIRU+.
 *
 * Mapea 1:1 a la tabla `public.store_products` en Supabase (`tttxmupjteqpljtfgmgo`).
 * `price` se mantiene como String porque la columna es `numeric` en Postgres —
 * trabajar con BigDecimal en Compose es overkill; un parse a Double sólo se hace
 * para formato.
 */
@Serializable
data class StoreProduct(
    val id: String,
    val title: String,
    val description: String? = null,
    val price: String,
    val currency: String = "MXN",
    @SerialName("image_url") val imageUrl: String? = null,
    val category: String = "general",
    val permalink: String? = null,
    @SerialName("stock_status") val stockStatus: String = "instock",
    @SerialName("stock_quantity") val stockQuantity: Int? = null,
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
) {
    val isInStock: Boolean get() = stockStatus == "instock"
    val priceAsDouble: Double get() = price.toDoubleOrNull() ?: 0.0
}
