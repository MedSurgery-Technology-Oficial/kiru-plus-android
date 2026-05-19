package com.medsurgery.kiruplus.data.academy

import com.medsurgery.kiruplus.domain.academy.ContentItem
import com.medsurgery.kiruplus.domain.academy.ContentRepository
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
class ContentRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : ContentRepository {

    override suspend fun fetchLessons(): Result<List<ContentItem>> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE)
                    .select {
                        filter {
                            // Excluir pearls y questions: tienen vistas dedicadas
                            // o aún no implementadas.
                            isIn("category", LESSON_CATEGORIES)
                        }
                        order(column = "updated_at", order = Order.DESCENDING)
                        limit(100)
                    }
                    .decodeList<ContentItem>()
            }
        }.onFailure { Timber.w(it, "fetchLessons failed") }

    override suspend fun fetchById(id: String): Result<ContentItem?> =
        runCatching {
            withContext(Dispatchers.IO) {
                supabase.from(TABLE)
                    .select { filter { eq("id", id) } }
                    .decodeList<ContentItem>()
                    .firstOrNull()
            }
        }.onFailure { Timber.w(it, "fetchById %s failed", id) }

    private companion object {
        const val TABLE = "content_items"
        val LESSON_CATEGORIES = listOf(
            "atls_chapter",
            "cbm_bundle",
            "summary_topic",
            "tokyo_guideline",
            "salud_ley_general",
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ContentRepositoryBindings {
    @Binds
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository
}
