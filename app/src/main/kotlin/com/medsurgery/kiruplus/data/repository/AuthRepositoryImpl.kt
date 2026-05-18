package com.medsurgery.kiruplus.data.repository

import com.medsurgery.kiruplus.core.auth.AuthRepository
import com.medsurgery.kiruplus.core.auth.SessionState
import com.medsurgery.kiruplus.core.auth.toAuthError
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
 * AuthRepository sobre supabase-kt v3.
 *
 * Errores se mapean a `AuthError` para que la UI muestre mensajes localizados
 * (ver core/auth/AuthError.kt). Endpoint Supabase: `tttxmupjteqpljtfgmgo.supabase.co`.
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
        runAuth("signIn $email") {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }

    override suspend fun signUp(email: String, password: String): Result<Unit> =
        runAuth("signUp $email") {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Unit
        }

    override suspend fun signOut(): Result<Unit> =
        runAuth("signOut") { supabase.auth.signOut() }

    override suspend fun resetPassword(email: String): Result<Unit> =
        runAuth("resetPassword $email") {
            supabase.auth.resetPasswordForEmail(email)
        }

    override suspend fun requestAccountDeletion(): Result<Unit> =
        runAuth("requestAccountDeletion") {
            supabase.functions.invoke("process_account_deletions")
            Unit
        }

    private inline fun runAuth(label: String, block: () -> Unit): Result<Unit> =
        try {
            block()
            Result.success(Unit)
        } catch (t: Throwable) {
            val mapped = t.toAuthError()
            Timber.w(t, "%s failed → %s", label, mapped::class.simpleName)
            Result.failure(mapped)
        }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryBindings {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
