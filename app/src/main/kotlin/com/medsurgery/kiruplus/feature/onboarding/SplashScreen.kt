package com.medsurgery.kiruplus.feature.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import com.medsurgery.kiruplus.core.designsystem.KiruCyanBlue
import com.medsurgery.kiruplus.core.designsystem.KiruNavyBlue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medsurgery.kiruplus.R

/**
 * Splash screen — espejo de `App/SplashView.swift` (iOS):
 *   - Fondo navy `#0F172A` (KiruNavyBlue) ignorando edge-to-edge.
 *   - Logo central 150dp (mismo que iOS 150pt).
 *   - Animación: opacity 0.5→1.0 + scale 0.8→1.1 con easeOut 1.5s al onAppear.
 *
 * El destino real (Disclaimer / Login / Home) lo decide SplashViewModel leyendo
 * DataStore y la sesión de Supabase.
 */
@Composable
fun SplashScreen(
    onContinueToDisclaimer: () -> Unit,
    onContinueToLogin: () -> Unit,
    onContinueToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    // Animaciones espejo de iOS: arrancan en 0.5 / 0.8 y van a 1.0 / 1.1.
    val opacity = remember { Animatable(0.5f) }
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        opacity.animateTo(1f, animationSpec = tween(durationMillis = 1500))
    }
    LaunchedEffect(Unit) {
        scale.animateTo(1.1f, animationSpec = tween(durationMillis = 1500))
    }

    LaunchedEffect(destination) {
        when (destination) {
            SplashViewModel.Destination.Disclaimer -> onContinueToDisclaimer()
            SplashViewModel.Destination.Login -> onContinueToLogin()
            SplashViewModel.Destination.Home -> onContinueToHome()
            null -> Unit
        }
    }

    // Fondo navy directo (no MaterialTheme) — el splash siempre se ve dark, igual que iOS.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KiruNavyBlue),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.kiru_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(150.dp)
                    .alpha(opacity.value)
                    .scale(scale.value),
                contentScale = ContentScale.Fit,
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .size(28.dp)
                    .alpha(opacity.value),
                color = KiruCyanBlue,
                strokeWidth = 2.5.dp,
            )
        }
    }
}
