package com.medsurgery.kiruplus.data.pearls

import com.medsurgery.kiruplus.domain.pearls.Pearl
import com.medsurgery.kiruplus.domain.pearls.PearlsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PearlsRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : PearlsRepository {

    override suspend fun fetchAllPearls(): Result<List<Pearl>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE)
                    .select {
                        order(column = "id", order = Order.ASCENDING)
                    }
                    .decodeList<Pearl>()
            }
        }.onFailure { Timber.w(it, "fetchAllPearls failed") }

    override suspend fun fetchPearl(id: Int): Result<Pearl?> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE)
                    .select { filter { eq("id", id) } }
                    .decodeList<Pearl>()
                    .firstOrNull()
            }
        }.onFailure { Timber.w(it, "fetchPearl %d failed", id) }

    private companion object {
        const val TABLE = "pearls"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PearlsRepositoryBindings {
    @Binds
    abstract fun bindPearlsRepository(impl: PearlsRepositoryImpl): PearlsRepository
}
