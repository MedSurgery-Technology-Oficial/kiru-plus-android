package com.medsurgery.kiruplus.domain.pearls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Perla clínica de la tabla `public.pearls` (Supabase).
 *
 * Schema (2026-05-18):
 *   id              integer NOT NULL
 *   title           text NOT NULL
 *   description     text NOT NULL
 *   category        text NOT NULL  (slug e.g. "cat.cuidadosCriticosQuirurgicos")
 *   image_name      text NOT NULL  (asset name iOS, puede estar vacío)
 *   hero_image_name text NOT NULL  (asset name iOS hero, puede estar vacío)
 *   content_blocks  jsonb NOT NULL (UI rich content — render diferido a v1)
 *   updated_at      timestamptz NOT NULL
 *
 * RLS: SELECT abierto a `{authenticated}` con `is_anonymous = false`.
 * Edits sólo por `service_role` (admin) — read-only desde la app.
 */
@Serializable
data class Pearl(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    @SerialName("image_name") val imageName: String? = null,
    @SerialName("hero_image_name") val heroImageName: String? = null,
)
