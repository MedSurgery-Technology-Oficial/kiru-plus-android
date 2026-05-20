package com.medsurgery.kiruplus.core.prefs

import kotlinx.coroutines.flow.Flow

/**
 * Preferencias persistidas del usuario.
 * Espejo conceptual de `App/UserSettings.swift` (iOS) — pero solo expone lo que
 * Android necesita en este punto. Schema versionable: agregar campos sin romper.
 */
data class UserPreferences(
    val language: AppLanguage = AppLanguage.System,
    /**
     * Default Dark para emparejar la app iOS (que es dark-only).
     * El usuario sí puede cambiar a Light o System desde Settings — sólo el default cambió.
     */
    val theme: AppTheme = AppTheme.Dark,
    val hapticsEnabled: Boolean = true,
    val sentryEnabled: Boolean = false,
    val disclaimerAccepted: Boolean = false,
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
        /** Default Dark (sin override) para emparejar iOS dark-only. */
        fun fromName(name: String?): AppTheme = entries.firstOrNull { it.name == name } ?: Dark
    }
}

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setLanguage(language: AppLanguage)
    suspend fun setTheme(theme: AppTheme)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setSentryEnabled(enabled: Boolean)
    suspend fun setDisclaimerAccepted(accepted: Boolean)
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
    const val DISCLAIMER_ACCEPTED = "disclaimer_accepted"
}
