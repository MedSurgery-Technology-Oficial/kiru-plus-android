package com.medsurgery.kiruplus.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Extension property compartida del DataStore del usuario.
 *
 * Vive en `core/prefs/` (en lugar del Impl) para que tanto el
 * `UserPreferencesRepositoryImpl` (consumidor normal vía Hilt) como
 * `KiruApp.onCreate()` (consumidor pre-Hilt en startup) puedan leer del
 * mismo archivo sin divergir.
 *
 * Nombre del archivo: `kiru_user_prefs` (DataStore lo serializa como
 * `kiru_user_prefs.preferences_pb` en `filesDir/datastore/`).
 */
val Context.kiruDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "kiru_user_prefs",
)
