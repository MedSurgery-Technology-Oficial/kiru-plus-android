package com.medsurgery.kiruplus.data.profile

import com.medsurgery.kiruplus.domain.profile.Profile
import com.medsurgery.kiruplus.domain.profile.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : ProfileRepository {

    override suspend fun fetchCurrentProfile(): Result<Profile?> =
        runCatching {
            withContext(Dispatchers.IO) {
                // No necesitamos filtrar por id — la RLS policy ya restringe al
                // row del propio usuario. Pedimos limit 1 por seguridad.
                supabase.from(TABLE)
                    .select { limit(1) }
                    .decodeList<Profile>()
                    .firstOrNull()
            }
        }.onFailure { Timber.w(it, "fetchCurrentProfile failed") }

    private companion object {
        const val TABLE = "profiles"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryBindings {
    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
