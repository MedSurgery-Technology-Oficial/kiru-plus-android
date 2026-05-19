package com.medsurgery.kiruplus.domain.logbook

import kotlinx.datetime.LocalDate

interface LogbookRepository {
    /** Lista de registros del usuario actual, orden DESC por `procedure_date`. */
    suspend fun fetchLogs(): Result<List<SurgicalLog>>

    /** Catálogo público de procedimientos (lookup para forms). */
    suspend fun fetchProcedures(): Result<List<Procedure>>

    /** Inserta un nuevo log. `user_id` se infiere del JWT (validado por RLS). */
    suspend fun createLog(input: NewLogInput): Result<Unit>

    /** Elimina el log del usuario actual por ID. RLS lo valida. */
    suspend fun deleteLog(id: String): Result<Unit>
}

/**
 * Payload de entrada para crear un log. `userId` lo agrega el repository tras
 * consultar la sesión actual.
 */
data class NewLogInput(
    val procedureId: String,
    val procedureDate: LocalDate,
    val complexity: String?,
    val outcome: String?,
    val notes: String?,
)
