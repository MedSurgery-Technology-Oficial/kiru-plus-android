package com.medsurgery.kiruplus.domain.logbook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Catálogo de procedimientos quirúrgicos (lookup para el form de Logbook).
 *
 * Schema `public.procedures` (2026-05-18):
 *   id                uuid NOT NULL
 *   name_es           text NOT NULL
 *   name_en           text NOT NULL
 *   category          text NOT NULL  (e.g. "Emergency surgery", "Hepatobiliary")
 *   evidence_level    text NULL
 *   source_guideline  text NULL
 *
 * RLS: "Procedures are public" → SELECT para `{authenticated}`. Read-only.
 */
@Serializable
data class Procedure(
    val id: String,
    @SerialName("name_es") val nameEs: String,
    @SerialName("name_en") val nameEn: String,
    val category: String,
)
