package com.medsurgery.kiruplus.core.auth

import kotlinx.coroutines.flow.Flow

/**
 * Contrato de autenticación KIRU+.
 * Implementación en data/repository/AuthRepositoryImpl.kt usa supabase-kt v3.
 *
 * Equivalente conceptual al SupabaseAuthService de iOS.
 */
interface AuthRepository {

    /** Estado de sesión observable. Emite en login, logout y refresh. */
    val sessionState: Flow<SessionState>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signUp(email: String, password: String): Result<Unit>

    suspend fun signOut(): Result<Unit>

    suspend fun resetPassword(email: String): Result<Unit>

    /**
     * Solicita eliminación de cuenta con 48h de grace period.
     * Llama a Edge Function `process_account_deletions`.
     * Requerido por Google Play (Account Deletion policy) y GDPR Art. 17.
     */
    suspend fun requestAccountDeletion(): Result<Unit>

    /**
     * Solicita exportación de datos personales del usuario (GDPR Art. 15 /
     * LFPDPPP Art. 23). Llama a Edge Function `process_data_export`, que
     * agrega los datos en un payload y lo deja en storage `gdpr-exports`.
     * El usuario recibe por email un signed URL para descarga.
     */
    suspend fun requestDataExport(): Result<Unit>
}

sealed interface SessionState {
    data object Unknown : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val userId: String, val email: String?) : SessionState
}
