package com.medsurgery.kiruplus.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medsurgery.kiruplus.R
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Disclaimer médico bloqueante (Google Play Health Apps + protección legal).
 *
 * E9 polish:
 * - Detección de scroll: `Continue` queda gris hasta que el usuario llegue al
 *   final de la lista (`hasScrolledToEnd`) — fuerza lectura de las 5 secciones.
 * - Cuando aún no llegó al final, muestra un hint "Scroll to read all…".
 * - Mantiene el checkbox explícito ("I have read and accept…"); ambos deben
 *   estar true para habilitar Continue.
 */
@Composable
fun MedicalDisclaimerScreen(
    onAccepted: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    var accepted by remember { mutableStateOf(false) }
    var hasScrolledToEnd by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // `canScrollForward == false` cubre dos casos a la vez:
    //  1) Contenido cabe entero en pantalla (no hay scroll necesario) → habilitamos.
    //  2) Usuario llegó al final por scroll → habilitamos.
    // Sólo flipamos a true (no a false) para que un re-scroll hacia arriba no
    // re-bloquee el botón después de que el usuario ya leyó todo.
    LaunchedEffect(listState) {
        snapshotFlow { !listState.canScrollForward }
            .distinctUntilChanged()
            .collect { atEnd -> if (atEnd) hasScrolledToEnd = true }
    }

    val sections = listOf(
        R.string.disclaimer_section_1_title to R.string.disclaimer_section_1_body,
        R.string.disclaimer_section_2_title to R.string.disclaimer_section_2_body,
        R.string.disclaimer_section_3_title to R.string.disclaimer_section_3_body,
        R.string.disclaimer_section_4_title to R.string.disclaimer_section_4_body,
        R.string.disclaimer_section_5_title to R.string.disclaimer_section_5_body,
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            Text(
                text = stringResource(R.string.disclaimer_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
            HorizontalDivider()

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(sections) { (titleRes, bodyRes) ->
                    Column {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(bodyRes),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
                item {
                    Column {
                        TextButton(onClick = onPrivacyPolicy) {
                            Text(stringResource(R.string.legal_privacy_policy))
                        }
                        TextButton(onClick = onTerms) {
                            Text(stringResource(R.string.legal_terms))
                        }
                    }
                }
            }

            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AnimatedVisibility(
                    visible = !hasScrolledToEnd,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = stringResource(R.string.disclaimer_scroll_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                AcceptanceRow(
                    accepted = accepted,
                    onAcceptedChange = { accepted = it },
                )
                Button(
                    onClick = { viewModel.acceptDisclaimer(onAccepted) },
                    enabled = accepted && hasScrolledToEnd,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.disclaimer_continue))
                }
            }
        }
    }
}

@Composable
private fun AcceptanceRow(
    accepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = accepted,
            onCheckedChange = onAcceptedChange,
        )
        Text(
            text = stringResource(R.string.disclaimer_accept_checkbox),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
