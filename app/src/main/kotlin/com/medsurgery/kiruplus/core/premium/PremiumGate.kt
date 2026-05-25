package com.medsurgery.kiruplus.core.premium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

/**
 * Wraps premium-gated content.
 *
 * Shows [content] when [EntitlementState.Premium] is active. For [EntitlementState.Free]
 * or [EntitlementState.Error], renders a static upgrade prompt — it does NOT navigate
 * automatically. Navigation only occurs when the user taps "Ver planes", which invokes
 * [onUpgrade] once per tap. There is no automatic redirect loop.
 *
 * During [EntitlementState.Loading] renders an empty [Box] to avoid a flash of the
 * upgrade screen before RevenueCat returns. Sprint B (A7) will resolve this state
 * quickly on subsequent launches via the RevenueCat customer info cache.
 *
 * Usage:
 * ```kotlin
 * PremiumGate(
 *     entitlementFlow = entitlementFlow,
 *     onUpgrade = { navController.navigate(KiruRoute.Paywall) },
 * ) {
 *     AcademyScreen(...)
 * }
 * ```
 */
@Composable
fun PremiumGate(
    entitlementFlow: StateFlow<EntitlementState>,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state by entitlementFlow.collectAsState()

    when (state) {
        is EntitlementState.Premium -> content()
        is EntitlementState.Loading -> Box(modifier = modifier.fillMaxSize())
        is EntitlementState.Free, is EntitlementState.Error -> {
            PremiumUpgradePrompt(
                modifier = modifier,
                onUpgrade = onUpgrade,
            )
        }
    }
}

@Composable
private fun PremiumUpgradePrompt(
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Contenido Premium",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Suscríbete a KIRU+ para acceder a este contenido.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onUpgrade) {
                Text("Ver planes")
            }
        }
    }
}
