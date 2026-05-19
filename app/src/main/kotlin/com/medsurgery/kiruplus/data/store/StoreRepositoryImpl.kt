package com.medsurgery.kiruplus.data.store

import com.medsurgery.kiruplus.domain.store.StoreProduct
import com.medsurgery.kiruplus.domain.store.StoreRepository
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
class StoreRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : StoreRepository {

    override suspend fun fetchVisibleProducts(): Result<List<StoreProduct>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE)
                    .select {
                        filter { eq("is_visible", true) }
                        order(column = "sort_order", order = Order.ASCENDING)
                    }
                    .decodeList<StoreProduct>()
            }
        }.onFailure { Timber.w(it, "fetchVisibleProducts failed") }

    private companion object {
        const val TABLE = "store_products"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreRepositoryBindings {
    @Binds
    abstract fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository
}
