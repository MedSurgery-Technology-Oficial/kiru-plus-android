package com.medsurgery.kiruplus.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsurgery.kiruplus.R
import kotlinx.coroutines.delay

/**
 * Splash screen — pantalla puente entre el SplashScreen API nativo y el primer destino.
 * E0: bootstrap simple (delay + decisión). En E3 leerá DataStore para saber si:
 *  - hasAcceptedMedicalDisclaimer → ir a Login
 *  - !hasAcceptedMedicalDisclaimer → ir a MedicalDisclaimer
 *  - tiene sesión activa → ir a Home
 */
@Composable
fun SplashScreen(
    onContinueToDisclaimer: () -> Unit,
    onContinueToHome: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(800)
        // TODO E3: leer DataStore (disclaimer aceptado + sesión válida) y enrutar.
        onContinueToDisclaimer()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.kiru_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit,
            )
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 32.dp).size(32.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.splash_loading),
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
