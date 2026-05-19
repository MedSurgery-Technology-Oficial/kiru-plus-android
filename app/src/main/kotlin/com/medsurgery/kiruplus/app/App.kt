package com.medsurgery.kiruplus.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.medsurgery.kiruplus.app.nav.KiruNavHost
import com.medsurgery.kiruplus.core.designsystem.KiruTheme

@Composable
fun App(viewModel: AppViewModel = hiltViewModel()) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    KiruTheme(appTheme = prefs.theme) {
        val navController = rememberNavController()
        KiruNavHost(navController = navController)
    }
}
