package com.medsurgery.kiruplus.core.network

import com.medsurgery.kiruplus.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import timber.log.Timber
import javax.inject.Singleton

/**
 * Cliente Supabase singleton.
 * Espejo de Core/Networking/SupabaseManager.swift (iOS).
 *
 * - Credenciales vienen de BuildConfig (inyectado desde local.properties).
 * - Si las credenciales faltan en build debug, se logea pero NO se crashea:
 *   las llamadas auth fallarán con error de credencial, no a nivel de proceso.
 *   Igual patrón que iOS con el fallback a `localhost.invalid`.
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        val url = BuildConfig.SUPABASE_URL.ifBlank {
            Timber.w("SUPABASE_URL missing. Usando placeholder. Configura local.properties.")
            "https://localhost.invalid"
        }
        val key = BuildConfig.SUPABASE_ANON_KEY
        if (key.isBlank()) {
            Timber.w("SUPABASE_ANON_KEY missing. Auth fallará hasta configurar local.properties.")
        }

        return createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key,
        ) {
            install(Auth) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest)
            install(Storage)
            install(Realtime)
            install(Functions)
        }
    }
}
