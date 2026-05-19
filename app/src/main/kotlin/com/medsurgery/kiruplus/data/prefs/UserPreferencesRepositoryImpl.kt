package com.medsurgery.kiruplus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.medsurgery.kiruplus.core.prefs.AppLanguage
import com.medsurgery.kiruplus.core.prefs.AppTheme
import com.medsurgery.kiruplus.core.prefs.UserPreferences
import com.medsurgery.kiruplus.core.prefs.UserPreferencesKeys
import com.medsurgery.kiruplus.core.prefs.UserPreferencesRepository
import com.medsurgery.kiruplus.core.prefs.kiruDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    private object Keys {
        val Language = stringPreferencesKey(UserPreferencesKeys.LANGUAGE)
        val Theme = stringPreferencesKey(UserPreferencesKeys.THEME)
        val Haptics = booleanPreferencesKey(UserPreferencesKeys.HAPTICS)
        val SentryEnabled = booleanPreferencesKey(UserPreferencesKeys.SENTRY_ENABLED)
        val DisclaimerAccepted = booleanPreferencesKey(UserPreferencesKeys.DISCLAIMER_ACCEPTED)
    }

    override val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            language = AppLanguage.fromTag(prefs[Keys.Language]),
            theme = AppTheme.fromName(prefs[Keys.Theme]),
            hapticsEnabled = prefs[Keys.Haptics] ?: true,
            sentryEnabled = prefs[Keys.SentryEnabled] ?: false,
            disclaimerAccepted = prefs[Keys.DisclaimerAccepted] ?: false,
        )
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.Language] = language.tag }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.Theme] = theme.name }
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.Haptics] = enabled }
    }

    override suspend fun setSentryEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SentryEnabled] = enabled }
    }

    override suspend fun setDisclaimerAccepted(accepted: Boolean) {
        dataStore.edit { it[Keys.DisclaimerAccepted] = accepted }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {
    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.kiruDataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserPreferencesBindings {
    @Binds
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository
}
