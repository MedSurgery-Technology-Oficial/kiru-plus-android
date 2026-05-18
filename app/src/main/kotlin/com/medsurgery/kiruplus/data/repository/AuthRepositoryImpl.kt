package com.medsurgery.kiruplus.data.repository

import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.auth.SessionState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de AuthRepository sobre supabase-kt v3.
 *
 * E0: el SDK está integrado, las llamadas son reales contra el proyecto
 * `tttxmupjteqpljtfgmgo.supabase.co`. Se afinará en E3 con manejo fino de
 * errores (rate limit, credenciales inválidas, email no confirmado).
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : AuthRepository {

    override val sessionState: Flow<SessionState> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    SessionState.SignedIn(
                        userId = user?.id.orEmpty(),
                        email = user?.email,
                    )
                }
                is SessionStatus.NotAuthenticated -> SessionState.SignedOut
                else -> SessionState.Unknown
            }
        }

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }.onFailure { Timber.w(it, "signIn failed for $email") }

    override suspend fun signUp(email: String, password: String): Result<Unit> =
        runCatching {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Unit
        }.onFailure { Timber.w(it, "signUp failed for $email") }

    override suspend fun signOut(): Result<Unit> =
        runCatching {
            supabase.auth.signOut()
        }.onFailure { Timber.w(it, "signOut failed") }

    override suspend fun resetPassword(email: String): Result<Unit> =
        runCatching {
            supabase.auth.resetPasswordForEmail(email)
        }.onFailure { Timber.w(it, "resetPassword failed for $email") }

    override suspend fun requestAccountDeletion(): Result<Unit> =
        runCatching {
            // Edge Function `process_account_deletions` ya existente
            // (48h grace period + GDPR Art. 17).
            supabase.functions.invoke("process_account_deletions")
            Unit
        }.onFailure { Timber.w(it, "requestAccountDeletion failed") }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryBindings {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
