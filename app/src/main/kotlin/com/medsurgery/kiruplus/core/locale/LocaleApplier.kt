package com.medsurgery.kiruplus.core.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.medsurgery.kiruplus.core.prefs.AppLanguage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Aplica el cambio de idioma a nivel app via per-app language API.
 *
 * Extraído del SettingsViewModel para que sea trivialmente mockeable en unit
 * tests sin necesidad de `mockkStatic(AppCompatDelegate::class)`.
 */
fun interface LocaleApplier {
    fun apply(language: AppLanguage)
}

@Module
@InstallIn(SingletonComponent::class)
object LocaleApplierModule {
    @Provides
    @Singleton
    fun provideLocaleApplier(): LocaleApplier = LocaleApplier { language ->
        val locales = when (language) {
            AppLanguage.System -> LocaleListCompat.getEmptyLocaleList()
            else -> LocaleListCompat.forLanguageTags(language.tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
