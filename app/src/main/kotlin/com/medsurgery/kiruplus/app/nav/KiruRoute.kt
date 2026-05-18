package com.medsurgery.kiruplus.app.nav

import kotlinx.serialization.Serializable

/**
 * Type-safe destinations for KIRU+ Android.
 * Espejo del enum `AppRoute` de iOS (260 rutas).
 * En esta primera entrega solo modelamos las P0/P1; el resto se incorpora en releases v1.x.
 */
sealed interface KiruRoute {

    @Serializable data object Splash : KiruRoute
    @Serializable data object MedicalDisclaimer : KiruRoute

    @Serializable data object Login : KiruRoute
    @Serializable data object Register : KiruRoute
    @Serializable data object ForgotPassword : KiruRoute

    @Serializable data object Home : KiruRoute
    @Serializable data object Profile : KiruRoute
    @Serializable data object Settings : KiruRoute

    @Serializable data object Paywall : KiruRoute
    @Serializable data object Store : KiruRoute

    @Serializable data object AccountDeletion : KiruRoute

    @Serializable
    data class WebView(val title: String, val url: String) : KiruRoute
}
