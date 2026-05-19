package com.medsurgery.kiruplus.core.browser

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.medsurgery.kiruplus.R
import timber.log.Timber

/**
 * Helper para abrir URLs externas en Chrome Custom Tabs.
 *
 * Compliance Google Play: checkout de pagos NUNCA debe hacerse en un WebView
 * embebido — debe abrirse en el browser del sistema (CustomTabs o intent VIEW).
 * Custom Tabs es preferible porque:
 *   - Mantiene la marca KIRU+ (toolbar color)
 *   - Sesiones se comparten con Chrome instalado (autocompletar, payment methods)
 *   - Mejor performance que `Intent.ACTION_VIEW` a un browser
 *
 * Falla silenciosamente si no hay browser instalado — el caller decide UX.
 */
object CustomTabsLauncher {

    fun launch(context: Context, url: String): Boolean {
        val uri = runCatching { Uri.parse(url) }.getOrNull()
        if (uri == null || uri.scheme.isNullOrBlank()) {
            Timber.w("CustomTabsLauncher: URL inválida: %s", url)
            return false
        }

        val toolbarColor = context.getColor(R.color.kiru_navy_blue)
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(false)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(toolbarColor)
                    .build(),
            )
            .build()

        return try {
            intent.launchUrl(context, uri)
            true
        } catch (e: ActivityNotFoundException) {
            Timber.w(e, "CustomTabsLauncher: no hay browser para abrir %s", url)
            false
        }
    }
}
