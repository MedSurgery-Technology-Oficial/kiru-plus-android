package com.medsurgery.kiruplus.core.prefs

import kotlinx.coroutines.flow.Flow

/**
 * Preferencias persistidas del usuario.
 * Espejo conceptual de `App/UserSettings.swift` (iOS) — pero solo expone lo que
 * Android necesita en este punto. Schema versionable: agregar campos sin romper.
 */
data class UserPreferences(
    val language: AppLanguage = AppLanguage.System,
    val theme: AppTheme = AppTheme.System,
    val hapticsEnabled: Boolean = true,
    /**
     * Crash reporting opt-in. Si `false`, Sentry NO se inicializa al startup
     * aunque `BuildConfig.SENTRY_DSN` esté presente. Default false (privacy-first).
     */
    val sentryEnabled: Boolean = false,
)

enum class AppLanguage(val tag: String) {
    /** Sigue el idioma del sistema (default). */
    System("system"),
    Spanish("es"),
    English("en");

    companion object {
        fun fromTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag == tag } ?: System
    }
}

enum class AppTheme {
    System, Light, Dark;

    companion object {
        fun fromName(name: String?): AppTheme = entries.firstOrNull { it.name == name } ?: System
    }
}

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setLanguage(language: AppLanguage)
    suspend fun setTheme(theme: AppTheme)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setSentryEnabled(enabled: Boolean)
}

/**
 * Keys de DataStore. Públicas para que `KiruApp.onCreate()` pueda leer la
 * pref de `sentryEnabled` antes de que Hilt esté listo — sin duplicar strings.
 */
object UserPreferencesKeys {
    const val LANGUAGE = "language"
    const val THEME = "theme"
    const val HAPTICS = "haptics_enabled"
    const val SENTRY_ENABLED = "sentry_enabled"
}
