package com.medsurgery.kiruplus.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medsurgery.kiruplus.R

/**
 * Perfil + Settings + acciones críticas: privacy/terms/subscriptions, eliminar cuenta.
 *
 * Eliminación de cuenta es REQUERIDA por Google Play (account deletion policy).
 * E4 conectará con AuthRepository.requestAccountDeletion() → Edge Function
 * `process_account_deletions` (48h grace, GDPR Art. 17 / LFPDPPP Art. 23).
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onDeleteAccount: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    onSubscriptions: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.tab_profile)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

            TextButton(onClick = onPrivacyPolicy, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.legal_privacy_policy))
            }
            TextButton(onClick = onTerms, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.legal_terms))
            }
            TextButton(onClick = onSubscriptions, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.legal_subscriptions))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            TextButton(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.auth_delete_account),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
