package com.medsurgery.kiruplus.domain.academy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ítem de contenido educativo de la tabla `public.content_items` (Supabase).
 *
 * Schema (2026-05-18):
 *   id              uuid NOT NULL
 *   title           text NOT NULL
 *   content         text NULL  (cuerpo markdown / texto)
 *   category        text NULL  (atls_chapter / cbm_bundle / pearl / question / etc.)
 *   type            text NULL  (subtítulo o tema; texto libre)
 *   metadata        jsonb NULL (refs cross-resources)
 *   specialty       text NULL  (e.g. "Cirugía general (General Surgery)")
 *   updated_at      timestamptz
 *
 * Hay 4515 filas hoy. Para Academy excluimos `category = 'pearl'` (ya viven en
 * su propia tabla y screen) y `category = 'question'` (cuyo render natural es
 * un quiz player que aún no implementamos).
 *
 * RLS: SELECT abierto a `{authenticated}` no-anonymous. Read-only en cliente.
 */
@Serializable
data class ContentItem(
    val id: String,
    val title: String,
    val content: String? = null,
    val category: String? = null,
    val type: String? = null,
    val specialty: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)
