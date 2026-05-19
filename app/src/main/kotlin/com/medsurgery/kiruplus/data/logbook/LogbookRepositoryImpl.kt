package com.medsurgery.kiruplus.data.logbook

import com.medsurgery.kiruplus.domain.logbook.LogbookRepository
import com.medsurgery.kiruplus.domain.logbook.NewLogInput
import com.medsurgery.kiruplus.domain.logbook.Procedure
import com.medsurgery.kiruplus.domain.logbook.SurgicalLog
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogbookRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : LogbookRepository {

    override suspend fun fetchLogs(): Result<List<SurgicalLog>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(LOGS).select {
                    order(column = "procedure_date", order = Order.DESCENDING)
                }.decodeList<SurgicalLog>()
            }
        }.onFailure { Timber.w(it, "fetchLogs failed") }

    override suspend fun fetchProcedures(): Result<List<Procedure>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(PROCEDURES).select {
                    order(column = "name_es", order = Order.ASCENDING)
                }.decodeList<Procedure>()
            }
        }.onFailure { Timber.w(it, "fetchProcedures failed") }

    override suspend fun deleteLog(id: String): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(LOGS).delete {
                    filter { eq("id", id) }
                }
                Unit
            }
        }.onFailure { Timber.w(it, "deleteLog %s failed", id) }

    override suspend fun createLog(input: NewLogInput): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                val uid = supabase.auth.currentUserOrNull()?.id
                    ?: error("No active session — login required to insert a surgical log.")

                supabase.from(LOGS).insert(
                    InsertPayload(
                        userId = uid,
                        procedureId = input.procedureId,
                        procedureDate = input.procedureDate.toString(),
                        complexity = input.complexity?.takeIf { it.isNotBlank() },
                        outcome = input.outcome?.takeIf { it.isNotBlank() },
                        notes = input.notes?.takeIf { it.isNotBlank() },
                    ),
                )
                Unit
            }
        }.onFailure { Timber.w(it, "createLog failed") }

    /**
     * DTO de inserción — coincide con columnas de `surgical_logs` excluyendo
     * los que tienen DEFAULT (id, created_at). RLS valida que `user_id` matchee
     * `auth.uid()` con el `with_check` de la policy "Surgeons manage own logs".
     */
    @Serializable
    private data class InsertPayload(
        @SerialName("user_id") val userId: String,
        @SerialName("procedure_id") val procedureId: String,
        @SerialName("procedure_date") val procedureDate: String,
        val complexity: String? = null,
        val outcome: String? = null,
        val notes: String? = null,
    )

    private companion object {
        const val LOGS = "surgical_logs"
        const val PROCEDURES = "procedures"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LogbookRepositoryBindings {
    @Binds
    abstract fun bindLogbookRepository(impl: LogbookRepositoryImpl): LogbookRepository
}
