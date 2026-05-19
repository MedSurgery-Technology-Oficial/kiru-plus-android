package com.medsurgery.kiruplus

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.medsurgery.kiruplus.core.prefs.UserPreferencesKeys
import com.medsurgery.kiruplus.core.prefs.kiruDataStore
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@HiltAndroidApp
class KiruApp : Application() {

    override fun onCreate() {
        super.onCreate()
        installLogging()
        installRevenueCat()
        installCrashReportingIfOptedIn()
    }

    private fun installRevenueCat() {
        val apiKey = BuildConfig.REVENUECAT_API_KEY
        if (apiKey.isBlank()) {
            Timber.w("RevenueCat API key missing; paywall unavailable in this build.")
            return
        }
        if (BuildConfig.DEBUG) Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(this, apiKey).build()
        )
    }

    private fun installLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Sentry sólo se inicializa cuando se cumplen ambas condiciones:
     *  1) `BuildConfig.SENTRY_DSN` no está vacío.
     *  2) El usuario hizo opt-in en Settings (DataStore flag `sentry_enabled`).
     *
     * Lee el flag con `runBlocking` porque la inicialización del SDK debe ocurrir
     * al startup, antes de cualquier crash. El bloqueo es de ~5-10 ms en cold
     * start; aceptable para un toggle de privacidad. Si el usuario lo activa
     * en runtime, el cambio se aplica al próximo launch.
     */
    private fun installCrashReportingIfOptedIn() {
        val dsn = BuildConfig.SENTRY_DSN
        if (dsn.isBlank()) {
            Timber.w("Sentry DSN missing; crash reporting unavailable in this build.")
            return
        }

        val optedIn = runBlocking {
            kiruDataStore.data
                .first()[booleanPreferencesKey(UserPreferencesKeys.SENTRY_ENABLED)] ?: false
        }
        if (!optedIn) {
            Timber.i("Sentry opt-in OFF — crash reporting disabled for this session.")
            return
        }

        SentryAndroid.init(this) { options ->
            options.dsn = dsn
            options.isDebug = BuildConfig.DEBUG
            options.tracesSampleRate = if (BuildConfig.DEBUG) 1.0 else 0.2
            options.environment = if (BuildConfig.DEBUG) "debug" else "release"
            options.release = "${BuildConfig.APPLICATION_ID}@${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}"
        }
    }
}
