package com.medsurgery.kiruplus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import timber.log.Timber

@HiltAndroidApp
class KiruApp : Application() {

    override fun onCreate() {
        super.onCreate()
        installLogging()
        installCrashReporting()
    }

    private fun installLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun installCrashReporting() {
        val dsn = BuildConfig.SENTRY_DSN
        if (dsn.isBlank()) {
            Timber.w("Sentry DSN missing; crash reporting disabled.")
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
