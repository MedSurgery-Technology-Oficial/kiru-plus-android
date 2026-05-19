package com.medsurgery.kiruplus.feature.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import com.medsurgery.kiruplus.R

/**
 * Tabs del bottom navigation Android.
 *
 * Paridad iOS pragmática:
 * - iOS tiene 8 tabs (home, academy, guides oculto, kCortex, calculator, logbook, store, profile).
 * - Material 3 NavigationBar recomienda 3-5 ítems para no cortar labels. Aquí escogemos los 5
 *   más usados como surface primario; calculator/kCortex/K-Tools quedan accesibles desde las
 *   Quick Actions del Home tab.
 */
enum class MainTab(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home(R.string.tab_home, Icons.Filled.Home),
    Academy(R.string.tab_academy, Icons.Filled.School),
    Logbook(R.string.tab_logbook, Icons.AutoMirrored.Filled.MenuBook),
    Store(R.string.tab_store, Icons.Filled.Storefront),
    Profile(R.string.tab_profile, Icons.Filled.Person),
}
