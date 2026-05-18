package com.medsurgery.kiruplus.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsurgery.kiruplus.R

/**
 * Disclaimer médico bloqueante de primera ejecución.
 * Espejo exacto del flujo iOS `MedicalDisclaimerView`.
 * E0: UI; E2 conecta DataStore para persistir aceptación.
 *
 * Crítico para Google Play Health Apps policy y para protección legal:
 * el usuario confirma explícitamente uso educativo NO clínico.
 */
@Composable
fun MedicalDisclaimerScreen(
    onAccepted: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
) {
    var accepted by remember { mutableStateOf(false) }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
            HorizontalDivider()

            LazyColumn(
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
                AcceptanceRow(
                    accepted = accepted,
                    onAcceptedChange = { accepted = it },
                )
                Button(
                    onClick = onAccepted,
                    enabled = accepted,
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
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
