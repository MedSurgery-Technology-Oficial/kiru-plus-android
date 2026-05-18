package com.medsurgery.kiruplus.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.medsurgery.kiruplus.app.nav.KiruNavHost
import com.medsurgery.kiruplus.core.designsystem.KiruTheme

@Composable
fun App() {
    KiruTheme {
        val navController = rememberNavController()
        KiruNavHost(navController = navController)
    }
}
