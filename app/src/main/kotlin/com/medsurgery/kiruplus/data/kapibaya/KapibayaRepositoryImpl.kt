package com.medsurgery.kiruplus.data.kapibaya

import com.medsurgery.kiruplus.domain.kapibaya.KapibayaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KapibayaRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : KapibayaRepository {

    override suspend fun sendMessage(
        conversationId: String,
        message: String,
    ): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val uid = supabase.auth.currentUserOrNull()?.id
                ?: error("No active session — login required to chat with Dr. Kapibaya.")

            val payload = buildJsonObject {
                put("message", message)
                put("conversationId", conversationId)
                put("userId", uid)
            }

            val response = supabase.functions.invoke(
                function = EF_NAME,
                body = payload,
            )

            val raw: String = response.body()
            // El EF responde JSON; el contenido del assistant suele venir en
            // `response`, `text` o `assistantMessage`. Probamos los más comunes.
            val parsed = runCatching { Json.parseToJsonElement(raw).let { it as JsonObject } }.getOrNull()
            if (parsed != null) {
                listOf("response", "text", "assistantMessage", "message", "answer")
                    .firstNotNullOfOrNull { key -> parsed[key]?.jsonPrimitive?.contentOrNull() }
                    ?: raw // fallback a raw si no encontramos campo conocido
            } else {
                raw // si no es JSON, asumimos texto plano
            }
        }
    }.onFailure { Timber.w(it, "sendMessage failed") }

    private fun kotlinx.serialization.json.JsonPrimitive.contentOrNull(): String? =
        runCatching { content }.getOrNull()

    private companion object {
        const val EF_NAME = "ask_kapibaya"
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class KapibayaRepositoryBindings {
    @Binds
    abstract fun bindKapibayaRepository(impl: KapibayaRepositoryImpl): KapibayaRepository
}
