package com.medsurgery.kiruplus.domain.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Perfil del usuario en la tabla `public.profiles` (Supabase).
 *
 * Schema (2026-05-18):
 *   id          uuid NOT NULL  → auth.uid()
 *   full_name   text NOT NULL
 *   specialty   text NULL      → default 'General Surgery'
 *   role        text NOT NULL  → e.g. 'medical_student', 'resident', 'attending'
 *   created_at  timestamptz
 *
 * RLS: sólo SELECT (al row donde `auth.uid() = id` y `is_anonymous = false`).
 * No hay UPDATE policy → este modelo es read-only desde el cliente.
 * Edits posteriores (local-only paridad iOS UserSettings) viven en DataStore.
 */
@Serializable
data class Profile(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val specialty: String? = "General Surgery",
    val role: String,
)
