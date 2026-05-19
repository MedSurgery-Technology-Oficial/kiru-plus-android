package com.medsurgery.kiruplus.domain.logbook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Registro de procedimiento quirúrgico realizado por el usuario.
 *
 * Schema `public.surgical_logs` (2026-05-18):
 *   id                uuid NOT NULL DEFAULT uuid_generate_v4()
 *   user_id           uuid NOT NULL  → auth.uid()
 *   procedure_id      uuid NOT NULL  → FK a public.procedures
 *   patient_id_hash   text NULL      (hash anónimo opcional)
 *   procedure_date    date NOT NULL DEFAULT CURRENT_DATE
 *   complexity        text NULL
 *   outcome           text NULL
 *   notes             text NULL
 *   created_at        timestamptz
 *
 * RLS: policy "Surgeons manage own logs" otorga ALL (CRUD) al usuario donde
 * `auth.uid() = user_id` (no anonymous). Cliente puede hacer INSERT/UPDATE/
 * DELETE directos vía Postgrest sin Edge Function — la convención del backend
 * sugiere `ingest_surgical_log` para casos con validation extra, pero para
 * CRUD simple bypaseamos.
 */
@Serializable
data class SurgicalLog(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("procedure_id") val procedureId: String,
    @SerialName("procedure_date") val procedureDate: String, // ISO yyyy-MM-dd
    val complexity: String? = null,
    val outcome: String? = null,
    val notes: String? = null,
    @SerialName("patient_id_hash") val patientIdHash: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
